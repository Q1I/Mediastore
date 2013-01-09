/**
 * Kapselt DVD-Daten.
 *
 */
package media.definitions;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.LinkedHashSet;

/**
 * @author Silvio Paschke, Stefan Endrullis
 */
public class DVD extends Product {

	private String format = "";
	private String regionCode = null;
	private Integer runningTime = null;
	private Set<Person> persons = new LinkedHashSet<Person>();


	public DVD() {
		setType(Type.dvd);
	}

//	/**
//	 * @return Returns the actors.
//	 */
//	public Set<Actor> getActors() {
//		return this.actors;
//	}
//
//	/**
//	 * @param actors The actors to set.
//	 */
//	public void setActors(Set<Actor> actors) {
//		this.actors = actors;
//	}
//
//	/**
//	 * Adds a actor to the actor list.
//	 *
//	 * @param actor actor to add
//	 */
//	public void addActor(Actor actor) {
//		actors.add(actor);
//		actor.setProduct(this);
//	}
//
//	/**
//	 * @return Returns the creators.
//	 */
//	public Set<Creator> getCreators() {
//		return this.creators;
//	}
//
//	/**
//	 * @param creators The creators to set.
//	 */
//	public void setCreators(Set<Creator> creators) {
//		this.creators = creators;
//	}
//
//	/**
//	 * Adds a creator to the creator list.
//	 *
//	 * @param creator creator to add
//	 */
//	public void addCreator(Creator creator) {
//		creators.add(creator);
//		creator.setProduct(this);
//	}
//
//	/**
//	 * @return Returns the directors.
//	 */
//	public Set<Director> getDirectors() {
//		return this.directors;
//	}
//
//	/**
//	 * @param directors The directors to set.
//	 */
//	public void setDirectors(Set<Director> directors) {
//		this.directors = directors;
//	}
//
//	/**
//	 * Adds a director to the director list.
//	 *
//	 * @param director director to add
//	 */
//	public void addDirector(Director director) {
//		directors.add(director);
//		director.setProduct(this);
//	}

	/**
	 * @return Returns the format.
	 */
	public String getFormat() {
		return this.format;
	}

	/**
	 * @param format The format to set.
	 */
	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * @return Returns the regioncode.
	 */
	public String getRegionCode() {
		return this.regionCode;
	}

	/**
	 * @param regionCode The regioncode to set.
	 */
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	/**
	 * @return Returns the runningtime.
	 */
	public Integer getRunningTime() {
		return this.runningTime;
	}

	/**
	 * @param runningTime The runningtime to set.
	 */
	public void setRunningTime(Integer runningTime) {
		this.runningTime = runningTime;
	}



// inner classes

	public void setPersons(Set<Person> persons) {
		this.persons = persons;
	}

	public Set<Person> getPersons() {
		return persons;
	}



	public static class Actor extends Person<DVD> {
		public Actor() { }
		public Actor(String name) { super(name); }
	}

	public static class Creator extends Person<DVD> {
		public Creator() { }
		public Creator(String name) { super(name); }
	}

	public static class Director extends Person<DVD> {
		public Director() { }
		public Director(String name) { super(name); }
	}
	
	public static class Involved extends Person<DVD> {
		public Involved() { }
		public Involved(String name) { super(name); }
	}
	
}
