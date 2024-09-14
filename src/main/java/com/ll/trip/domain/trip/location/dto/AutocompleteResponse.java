package com.ll.trip.domain.trip.location.dto;

import java.util.List;

import lombok.Data;

@Data
public class AutocompleteResponse {
	private List<Suggestion> suggestions;

	@Data
	public static class Suggestion {
		private PlacePrediction placePrediction;
	}

	@Data
	public static class PlacePrediction {
		private String place;
		private String placeId;
		private Text text;
		private StructuredFormat structuredFormat;
	}

	@Data
	public static class Text {
		private String text;
	}

	@Data
	public static class StructuredFormat {
		private MainText mainText;
		private SecondaryText secondaryText;
	}

	@Data
	public static class MainText {
		private String text;
	}

	@Data
	public static class SecondaryText {
		private String text;
	}
}
