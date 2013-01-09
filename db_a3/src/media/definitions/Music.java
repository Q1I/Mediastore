/**
 * Kapselt ein Produkt aus der Gruppe der Musik.
 */
package media.definitions;

import java.util.*;
import java.io.Serializable;

/**
 * @author Silvio Paschke, Stefan Endrullis
 */
public class Music extends Product {

	private int releaseDate = 0;

	private Set<Track> tracks = new LinkedHashSet<Track>();
	private String label = "";
	private Set<Artist> artists = new LinkedHashSet<Artist>();

	public Music() {
		setType(Type.music);
	}

	/**
	 * @return Returns the releasedate.
	 */
	public int getReleaseDate() {
		return this.releaseDate;
	}

	/**
	 * @param releaseDate The releasedate to set.
	 */
	public void setReleaseDate(int releaseDate) {
		this.releaseDate = releaseDate;
	}

	/**
	 * @return Returns the tracks.
	 */
	public Set<Track> getTracks() {
		return this.tracks;
	}

	/**
	 * @param tracks The tracks to set.
	 */
	public void setTracks(Set<Track> tracks) {
		this.tracks = tracks;
	}

	/**
	 * Adds a track to the track list.
	 *
	 * @param track track to add
	 */
	public void addTrack(Track track) {
		tracks.add(track);
		track.setProduct(this);
	}

	/**
	 * @return Returns the labels.
	 */
	public String getLabel() {
		return this.label;
	}

	/**
	 * @param labels The labels to set.
	 */
	public void setLabel(String labels) {
		this.label = labels;
	}

//	/**
//	 * Adds a label to the label list.
//	 *
//	 * @param label label to add
//	 */
//	public void addLabel(Label label) {
//		labels.add(label);
//		label.setProduct(this);
//	}

	/**
	 * @return Returns the artists.
	 */
	public Set<Artist> getArtists() {
		return this.artists;
	}

	/**
	 * @param artists The artists to set.
	 */
	public void setArtists(Set<Artist> artists) {
		this.artists = artists;
	}

	/**
	 * Adds an artist to the artist list.
	 *
	 * @param artist artist to add
	 */
	public void addArtist(Artist artist) {
		artists.add(artist);
		artist.setProduct(this);
	}
	


// inner classes

//	public static class Track extends ProductChild<Music> {
//		public Track() { }
//		public Track(String name) { super(name); }
//		public int getNo() { return product.getTracks().indexOf(this); }
//
//		/*
//		public boolean equals(Object obj) {
//			if (obj instanceof Track) {
//				Track other = (Track) obj;
//
//				return product == other.product && getNo() == other.getNo();
//			}
//			return false;
//		}
//
//		public int hashCode() {
//			return product.hashCode() + getNo();		
//		}
//		*/
//	}

	public static class Label extends Person<Music> {
		public Label() { }
		public Label(String name) { super(name); }
	}

	public static class Artist extends Person<Music> {
		public Artist() { }
		public Artist(String name) { super(name); }
	}
}
