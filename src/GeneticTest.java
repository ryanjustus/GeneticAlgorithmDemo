import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ryan
 * Date: 9/25/12
 * Time: 10:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class GeneticTest extends junit.framework.TestCase {
	public void testGenerateCode() throws Exception {
		char[] charset = new char[]{'a','b','c','d','e','f','g','h','i','j'};
		for(int i=0;i<3;i++){
			char[] code = Genetic.generateCode(charset,5);
			System.out.println(Arrays.toString(code));
		}
	}

	public void testCost() throws Exception {
		char[] charset = new char[]{'a','b','c','d','e','f','g','h','i','j'};
		char[] code = Genetic.generateCode(charset,5);
		System.out.println(Arrays.toString(code));
		char[] candidate = Genetic.generateCode(charset, 5);
		System.out.println(Arrays.toString(candidate));

		int score = Genetic.score(code,candidate);
		System.out.println("Score: " + score);
	}

	public void testCombine() throws Exception{
		char[] charset = new char[]{'a','b','c','d','e','f','g','h','i','j'};
		char[] c1 = Genetic.generateCode(charset,5);
		System.out.println(Arrays.toString(c1));
		char[] c2 = Genetic.generateCode(charset, 5);
		System.out.println(Arrays.toString(c2));

		char[] child = Genetic.combine(c1,c2);
		System.out.println(Arrays.toString(child));
	}

	public void testMutate() throws Exception{
		char[] charset = new char[]{'a','b','c','d','e','f','g','h','i','j'};
		char[] c1 = Genetic.generateCode(charset,5);
		System.out.println(Arrays.toString(c1));
		char[] c2 = Genetic.mutate(c1,charset);
		System.out.println(Arrays.toString(c2));
	}

	public void testTournamentSelect(){
		char[] charset = new char[]{'a','b','c','d','e','f','g','h','i','j'};
		char[] actual = Genetic.generateCode(charset, 5);
		System.out.println(Arrays.toString(actual));
		System.out.println("*****************");
		List<char[]> population = new ArrayList<char[]>();
		for(int i=0;i<10;i++){
			char[] c = Genetic.generateCode(charset, 5);
			System.out.println(Arrays.toString(c));
			population.add(c);
		}
		System.out.println("******************");
		List<char[]> after = Genetic.tournamentSelect(actual, population,4);
		for(int i=0;i<after.size();i++){
			System.out.println(Arrays.toString(after.get(i)));
		}
	}

	public void testDoRound(){
		char[] charset = new char[]{'a','b','c','d','e','f','g','h','i','j'};
		int length = 5;
		char[] actual = Genetic.generateCode(charset, length);

		List<char[]> initialPop = new ArrayList<char[]>();
		for(int i=0;i<100;i++){
			char[] c = Genetic.generateCode(charset, length);
			System.out.println(Arrays.toString(c));
			initialPop.add(c);
		}
		System.out.println("AFTER******************");

		List<char[]> after = Genetic.doRoundTournament(actual, initialPop, charset);
		System.out.println("Count: "+after.size());
		for(char[] c: after){
			System.out.println(Arrays.toString(c));
		}
	}

	public void testBoltzman(){
		char[] charset = new char[]{'a','b','c','d'};
		int length = 10;
		char[] actual = Genetic.generateCode(charset, length);
		List<char[]> population = new ArrayList<char[]>();
		for(int i=0;i<100;i++){
			char[] c = Genetic.generateCode(charset,length);
			population.add(c);
		}
		List<char[]> selected = Genetic.boltzmanSelect(actual, population, 40);
		for(char[] c: selected){
			System.out.println(Arrays.toString(c));
		}

	}

	public void testSolvePuzzle(){
		char[] charset = new char[]{'a','b','c','d'};
		int length = 10;
		char[] actual = Genetic.generateCode(charset, length);
		Genetic.TOURNAMENT_SELECT=2;
		float[] surviveChance = new float[]{0.0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f};
		float[] mutateChance = new float[]{0.01f, 0.02f, 0.03f, 0.04f, 0.05f,0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.40f, 0.50f, 0.60f, 0.8f, 0.9f, 1.0f};
		for(int s=0;s<surviveChance.length;s++){
			System.out.print(","+surviveChance[s]);
		}
		System.out.print("\n");
		for(int m=0;m<mutateChance.length;m++){
			System.out.print(mutateChance[m]);
			Genetic.setMutateChance(mutateChance[m]);
			for(int s=0;s<surviveChance.length;s++){
				Genetic.setSurviveChance(surviveChance[s]);
				int round=0;
				for(int j=0;j<100;j++){

					List<char[]> population = new ArrayList<char[]>();
					for(int i=0;i<100;i++){
						char[] c = Genetic.generateCode(charset,length);
						population.add(c);
					}

					char[] solved = null;
					while((solved=Genetic.getWinner(actual,population))==null){
						round++;
						population = Genetic.doRoundTournament(actual, population, charset);
					}
				}
				System.out.print(","+round/100.0);
			}
			System.out.print("\n");
		}
	}

	public void testSolvePuzzle_score(){
		char[] charset = new char[]{'a','b','c','d'};
		int length = 10;
		char[] actual = Genetic.generateCode(charset, length);
		Genetic.TOURNAMENT_SELECT=2;
		Genetic.GENERATION_SIZE=50;
		Genetic.SURVIVE_CHANCE=0.5f;
		Genetic.MUTATE_CHANCE=0.15f;
		int populationSize = 50;
		Genetic.CHAR_SCORE=0;

		float[] mm = {0.0f, 0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.35f, 0.40f, 0.45f, 0.50f};
		for(int v=1;v<=10;v++){
			Genetic.POSITION_SCORE=v; //Alter position score
			int round=0;
			for(int t=0;t<1000;t++){

				//Generate initial population
				List<char[]> population = new ArrayList<char[]>();
				for(int i=0;i<populationSize;i++){
					char[] c = Genetic.generateCode(charset,length);
					population.add(c);
				}

				char[] solved = null;
				while((solved=Genetic.getWinner(actual,population))==null){
					round++;
					population = Genetic.doRoundTournament(actual,population,charset);
				}
			}
			System.out.println(round/1000.0f);
		}

		System.out.print("\n");

	}

	public void testSolvePuzzle_mutation(){
		char[] charset = new char[]{'a','b','c','d'};
		int length = 10;
		char[] actual = Genetic.generateCode(charset, length);
		Genetic.TOURNAMENT_SELECT=2;
		Genetic.GENERATION_SIZE=50;
		Genetic.SURVIVE_CHANCE=0.50f;
		Genetic.MUTATE_CHANCE=0.15f;
		int populationSize = 50;
		Genetic.CHAR_SCORE=1;
		Genetic.POSITION_SCORE=3;
		Genetic.CHAR_SCORE=1;

		float[] mm = {0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.35f, 0.40f, 0.45f, 0.50f, 0.55f, 0.60f, 0.65f, 0.70f, 0.75f};
		for(float r: mm){
			Genetic.MUTATE_CHANCE=r;
			int round=0;
			for(int t=0;t<1000;t++){

				//Generate initial population
				List<char[]> population = new ArrayList<char[]>();
				for(int i=0;i<populationSize;i++){
					char[] c = Genetic.generateCode(charset,length);
					population.add(c);
				}

				char[] solved = null;
				while((solved=Genetic.getWinner(actual,population))==null){
					round++;
					population = Genetic.doRoundTournament(actual,population,charset);
				}
			}
			System.out.println(round/1000.0f);
		}
		System.out.print("\n");

	}

	public void testSolvePuzzle_population(){
		char[] charset = new char[]{'a','b','c','d'};
		int length = 10;
		char[] actual = Genetic.generateCode(charset, length);
		Genetic.TOURNAMENT_SELECT=2;
		Genetic.GENERATION_SIZE=50;
		Genetic.SURVIVE_CHANCE=0.50f;
		Genetic.MUTATE_CHANCE=0.15f;
		int populationSize = 50;
		Genetic.CHAR_SCORE=1;
		Genetic.POSITION_SCORE=3;
		Genetic.CHAR_SCORE=1;

		float[] pp = {0.05f, 0.10f, 0.15f, 0.20f, 0.25f, 0.30f, 0.35f, 0.40f, 0.45f, 0.50f, 0.55f, 0.60f, 0.65f, 0.70f, 0.75f};
		for(int p=10;p<=100;p+=10){
			populationSize = p;
			int round=0;
			long start = System.nanoTime();
			for(int t=0;t<1000;t++){

				//Generate initial population
				List<char[]> population = new ArrayList<char[]>();
				for(int i=0;i<populationSize;i++){
					char[] c = Genetic.generateCode(charset,length);
					population.add(c);
				}

				char[] solved = null;
				while((solved=Genetic.getWinner(actual,population))==null){
					round++;
					population = Genetic.doRoundTournament(actual,population,charset);
				}
			}

			System.out.println(round/1000.0f+","+(System.nanoTime()-start)/1000.0/1000.0/1000.0);
		}
		System.out.print("\n");

	}

	public void testSolvePuzzle_survival(){
		char[] charset = new char[]{'a','b','c','d'};
		int length = 10;
		char[] actual = Genetic.generateCode(charset, length);
		Genetic.TOURNAMENT_SELECT=2;
		Genetic.GENERATION_SIZE=50;
		Genetic.SURVIVE_CHANCE=0.50f;
		Genetic.MUTATE_CHANCE=0.15f;
		int populationSize = 50;
		Genetic.CHAR_SCORE=1;
		Genetic.POSITION_SCORE=3;
		Genetic.CHAR_SCORE=1;

		for(float s=0.1f;s<0.95f;s+=.1f){
			Genetic.SURVIVE_CHANCE=s;
			int round=0;
			long start = System.nanoTime();
			for(int t=0;t<1000;t++){

				//Generate initial population
				List<char[]> population = new ArrayList<char[]>();
				for(int i=0;i<populationSize;i++){
					char[] c = Genetic.generateCode(charset,length);
					population.add(c);
				}

				char[] solved = null;
				while((solved=Genetic.getWinner(actual,population))==null){
					round++;
					population = Genetic.doRoundTournament(actual,population,charset);
				}
			}

			System.out.println(round/1000.0f);
		}
		System.out.print("\n");

	}

	public void testSolvePuzzle_tournament_size(){
		char[] charset = new char[]{'a','b','c','d'};
		int length = 10;
		char[] actual = Genetic.generateCode(charset, length);
		Genetic.TOURNAMENT_SELECT=2;
		Genetic.GENERATION_SIZE=50;
		Genetic.SURVIVE_CHANCE=0.50f;
		Genetic.MUTATE_CHANCE=0.15f;
		int populationSize = 50;
		Genetic.CHAR_SCORE=1;
		Genetic.POSITION_SCORE=3;
		Genetic.CHAR_SCORE=1;

		for(int select=2;select<50;select+=3){
			Genetic.TOURNAMENT_SELECT=select;
			int round=0;
			long start = System.nanoTime();
			for(int t=0;t<1000;t++){

				//Generate initial population
				List<char[]> population = new ArrayList<char[]>();
				for(int i=0;i<populationSize;i++){
					char[] c = Genetic.generateCode(charset,length);
					population.add(c);
				}

				char[] solved = null;
				while((solved=Genetic.getWinner(actual,population))==null){
					round++;
					population = Genetic.doRoundTournament(actual,population,charset);
				}
			}

			System.out.println(round/1000.0f+","+(System.nanoTime()-start)/1000.0/1000.0/1000.0);
		}
		System.out.print("\n");

	}
}
