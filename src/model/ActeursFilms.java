package model;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class ActeursFilms {

	public static void main(String[] args) {
		
		
		ActeursFilms ActorFilm= new ActeursFilms();
		Set<Movie> movies= ActeursFilms.readMovies();
		
		
		// 1)Combien de films sont-ils référencés dans ce fichier 
		
		System.out.println("1/ nombre des films = " +movies.size());
		// 2)Combien d'acteurs sont-ils référencés dans ce fichier 
		long NombreActeur=
			movies.stream()
				  .flatMap(movie->movie.actors().stream())
				  .distinct()
				  .count();
		System.out.println("2/ le nombre des acteurs : " +NombreActeur);
				 
		//3)les années referencés dans le fichier
		
		long NombreAnnee=
				movies.stream()
				  .map(movie->movie.releaseYear())
				  .distinct()
				  .count();
		System.out.println("3/ le nombre des années : " +NombreAnnee);
		
		//4)a)année de sortie du film le plus vieux 
		Comparator<Movie> comp=
					Comparator.comparing(movie->movie.releaseYear());
		
		Movie AncienFilm=
				movies.stream()
				  	  .min(comp)
				  	  .orElseThrow() ;
				  	 
		System.out.println("4/a/ le film le plus vieux : " +AncienFilm.releaseYear());
		//4)b)année de sortie du film le plus recent
				Comparator<Movie> comp1=
							Comparator.comparing(movie->movie.releaseYear());
				
				Movie RecentFilm=
						movies.stream()
						  	  .max(comp1)
						  	  .orElseThrow() ;
						  	 
				System.out.println("4/b/ le film le plus recent : " +RecentFilm.releaseYear());
						 
		//5) Durant quelle année le plus grand nombre de films est-il sorti ? Quel est ce nombre ? 
				 Map<Integer, Long> map=
						 movies.stream()
						       .map(movie->movie.releaseYear())
						 		.collect(Collectors.groupingBy(
							    		   Function.identity(),
							    		   Collectors.counting()
							    		   )
							    		   );
					 Map.Entry<Integer, Long> annee=

					 map.entrySet().stream()
					 	           .max(Map.Entry.comparingByValue())
					 	           .orElseThrow();
					 
					 System.out.println("5/ l'année avec plus des films: " + annee);
    //6) Quel film comporte-t-il le plus grand nombre d’acteurs ? Quel est ce film et quel est ce nombre ?
				
				  Comparator<Movie> comp4 =
			        		Comparator.comparing(movie -> movie.actors().size());
			
			  Movie FilmAvecPlusActors=
					 movies.stream()
					  	  .max(comp4)
					  	  .orElseThrow() ;
		System.out.println("6/ le film avec plus des acteurs: " + FilmAvecPlusActors.title() + "=" + FilmAvecPlusActors.actors().size());
	  	 
					  
				
    //	7) Quel acteur a-t-il joué dans le plus grand nombre de films ? 

					 Map<Actor, Long> map1=
						 movies.stream()
						       .flatMap(movie->movie.actors().stream())
							       .collect(Collectors.groupingBy(
							    		   Function.identity(),
							    		   Collectors.counting()
							    		   )
							    		   );
					 Map.Entry<Actor, Long> ActeurPlusPresent=

					 map1.entrySet().stream()
					 	           .max(Map.Entry.comparingByValue())
					 	           .orElseThrow();
					 System.out.println("7/ Acteur le plus présent : " + ActeurPlusPresent);
			//9/a/ creer un comparator qui compare les acteurs par nom puis par prénom
			
//			Comparator<Actor> CmpNomPrenom=
//					Comparator.comparing(Actor::lastName)
//			        .thenComparing(Actor::firstName);	
//			
			
					 
			
			
				     
				     
				  
						 
		
	}
		
		
		
		 public static Set<Movie> readMovies() {

		        Function<String, Stream<Movie>> toMovie =
		                line -> {
		                    String[] elements = line.split("/");
		                    String title = elements[0].substring(0, elements[0].lastIndexOf("(")).trim();
		                    String releaseYear = elements[0].substring(elements[0].lastIndexOf("(") + 1, elements[0].lastIndexOf(")"));
		                    if (releaseYear.contains(",")) {
		                        // Movies with a coma in their title are discarded
		                    	int indexOfComa = releaseYear.indexOf(",");
		                    	releaseYear = releaseYear.substring(0, indexOfComa);
		                        // return Stream.empty();
		                    }
		                    Movie movie = new Movie(title, Integer.valueOf(releaseYear));


		                    for (int i = 1; i < elements.length; i++) {
		                        String[] name = elements[i].split(", ");
		                        String lastName = name[0].trim(); 
		                        String firstName = "";
		                        if (name.length > 1) {
		                            firstName = name[1].trim();
		                        }

		                        Actor actor = new Actor(lastName, firstName);
		                        movie.addActor(actor);
		                    }
		                    return Stream.of(movie);
		                };

		        try (FileInputStream fis = new FileInputStream("files/movies-mpaa.txt.gz");
		             GZIPInputStream gzis = new GZIPInputStream(fis);
		             InputStreamReader reader = new InputStreamReader(gzis);
		             BufferedReader bufferedReader = new BufferedReader(reader);
		             Stream<String> lines = bufferedReader.lines();
		        ) {

		            return lines.flatMap(toMovie).collect(Collectors.toSet());

		        } catch (IOException e) {
		            System.out.println("e.getMessage() = " + e.getMessage());
		        }

		        return Set.of();
		   		}
		

}

