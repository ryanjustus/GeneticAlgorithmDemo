import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 9/25/12
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class Genetic {
	public static int CHAR_SCORE = 1;     //value of a correct symbol
	public static int POSITION_SCORE = 2; //value of a correct position

	public static float MUTATE_CHANCE = 0.15f; //chance of mutation
	public static float SURVIVE_CHANCE=0.5f;  //Number of candidates to survive each generation

	public static int TOURNAMENT_SELECT = 2;  //Number of candidates in each tournament
	public static int GENERATION_SIZE = 50;


	public static final Random r = new Random(System.currentTimeMillis());


	public static void setMutateChance(float p){
		MUTATE_CHANCE=p;
	}

	public static void setSurviveChance(float p){
		SURVIVE_CHANCE=p;
	}

	public static char[] generateCode(char[] charset, int length){
		char[] code = new char[length];
		for(int i=0;i<length;i++){
			code[i] = charset[r.nextInt(charset.length)];
		}
		return code;
	}

	public static char[] getWinner(char[] actual, List<char[]> population){
		for(int i=0;i<population.size();i++){
			char[] candidate = population.get(i);
			boolean matched=true;
			for(int j=0;j<actual.length;j++){
				if(candidate[j]!=actual[j]){
					matched=false;
					break;
				}
			}
			if(matched)
				return  candidate;
		}
		return null;
	}


	public static List<char[]> doRoundTournament(final char[] actual, List<char[]> population, final char[] charset){
	    //tournament select X percent to breed
		List<char[]> selected = tournamentSelect(actual,population,(int)(SURVIVE_CHANCE*population.size()));
		while(selected.size()<population.size()){
		    //combine two random candidates from the population
			char[] p1 = population.get(r.nextInt(population.size()));
			char[] p2 = population.get(r.nextInt(population.size()));
			char[] child = Genetic.combine(p1,p2);

			//chance for mutation
			if(r.nextFloat()<MUTATE_CHANCE){
				child = Genetic.mutate(child,charset);
				//System.out.println("mutating child");
			}
			//System.out.println("adding child");
			selected.add(child);
		}
		return selected;
	}


	public static List<char[]> doRoundBoltzman(final char[] actual, List<char[]> population, final char[] charset){
		//tournament select X percent to breed
		List<char[]> selected = boltzmanSelect(actual, population, (int) (SURVIVE_CHANCE * population.size()));
		while(selected.size()<population.size()){
			//combine two random candidates from the population
			char[] p1 = population.get(r.nextInt(population.size()));
			char[] p2 = population.get(r.nextInt(population.size()));
			char[] child = Genetic.combine(p1,p2);

			//chance for mutation
			if(r.nextFloat()<MUTATE_CHANCE){
				child = Genetic.mutate(child,charset);
				//System.out.println("mutating child");
			}
			//System.out.println("adding child");
			selected.add(child);
		}
		return selected;
	}

	public static List<char[]> boltzmanSelect(char[] actual, List<char[]> population, int num){
		float sum = 0;
		for(int i=0;i<population.size();i++){
			sum+=score(actual,population.get(i));
		}
		Set<char[]> newPopulation = new HashSet<char[]>();
		int count =0;
		while(newPopulation.size()<=num){
			float chance = score(actual,population.get(count))/sum;
			//System.out.println("chance: "+chance);
			if(r.nextFloat()<chance){
				newPopulation.add(population.get(count));			}

			count++;
			if(count>=population.size()){
				count=0;
			}
		}
		return new ArrayList<char[]>(newPopulation);
	}


	public static List<char[]> tournamentSelect(char[] actual, List<char[]> population, int num){
		population = new ArrayList<char[]>(population);
		int count=0;
		Set<char[]> newPopulation = new HashSet<char[]>();
		while(count<num){
			List<char[]> tournament = new ArrayList<char[]>();
			for(int i=0;i<Genetic.TOURNAMENT_SELECT;i++){
				int idx1 = r.nextInt(population.size());
				char[] c1 = population.get(idx1);
				tournament.add(c1);

			}
			char[] winner =  Genetic.selectBest(actual, tournament);
			newPopulation.add(winner);
			population.remove(winner);
			count++;
		}
		return new ArrayList<char[]>(newPopulation);
	}

	public static int score(char[] actual, char[] candidate){
		boolean[] matched = new boolean[candidate.length];
		for(int i=0;i<matched.length;i++){
			matched[i]=false;
		}
		int correctPosition=0;
		int correctCharacters=0;
		for(int i=0;i<candidate.length;i++){
			char c = candidate[i];
			//Check for correct position and correct char
			if(c==actual[i]){
				correctPosition++;
				correctCharacters++;
				matched[i]=true;
			}
			//Character matches not in correct position
			else{
				for(int j=0;j<actual.length;j++){
					if(!matched[j] && c==actual[j]){
						matched[j]=true;
						correctCharacters++;
						break;
					}
				}
			}
		}
		return correctCharacters*CHAR_SCORE+correctPosition*POSITION_SCORE;
	}

	/**
	 * Swaps a section of c2 into c1 to generate new char[]
	 * @param c1
	 * @param c2
	 * @return
	 */
	public static char[] combine(char[] c1, char[] c2){
		int start = r.nextInt(c1.length);
		int end = r.nextInt(c1.length);
		char[] ret = c1.clone();
		//Wrap from end to start;
		if(start>end){
			for(int i=0;i<=end;i++){
				ret[i]=c2[i];
			}
			for(int i=start;i<c2.length;i++){
				ret[i]=c2[i];
			}
		}
		//Start and end are in the middle
		else{
			for(int i=start;i<=end;i++){
				ret[i]=c2[i];
			}
		}
		return ret;
	}

	public static  char[] selectBest(char[] actual, List<char[]> candidates){
		char[] best = candidates.get(0);
		int bestScore = score(actual, candidates.get(0));
		for(int i= 1; i<candidates.size();i++){
			char[] test = candidates.get(i);
			int testScore = score(actual,test);
			if(testScore>bestScore){
				best=test;
				bestScore=testScore;
			}
		}
		return best;
	}


	public static char[] mutate(char[] c, char[] charset){
		char[] ret = c.clone();
		ret[r.nextInt(ret.length)]=charset[r.nextInt(charset.length)];
		return ret;
	}
}
