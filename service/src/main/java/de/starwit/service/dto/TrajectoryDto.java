package de.starwit.service.dto;

import java.time.LocalDateTime;

public class TrajectoryDto {

	/**
	 * From which stream is this data coming from?
	 */
	private String streamId;

	/**
	 * This ID defines, what kind of object this is.
	 */
	private int classId;

	/**
	 * Unique ID to track an object over time.
	 */
	private String objectId;

	private boolean hasGeoCoordinates = false;

	private CoordinatesDto coordinates = new CoordinatesDto();

	private ShapeDto shape;

	private LocalDateTime receiveTimestamp;

	private BoundingBoxDto boundingBox = new BoundingBoxDto();

	public String getStreamId() {
		return streamId;
	}

	public void setStreamId(String streamId) {
		this.streamId = streamId;
	}

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public boolean isHasGeoCoordinates() {
		return hasGeoCoordinates;
	}

	public void setHasGeoCoordinates(boolean hasGeoCoordinates) {
		this.hasGeoCoordinates = hasGeoCoordinates;
	}

	public CoordinatesDto getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(CoordinatesDto coordinates) {
		this.coordinates = coordinates;
	}

	public LocalDateTime getReceiveTimestamp() {
		return receiveTimestamp;
	}

	public void setReceiveTimestamp(LocalDateTime receiveTimestamp) {
		this.receiveTimestamp = receiveTimestamp;
	}

	public ShapeDto getShape() {
		return shape;
	}

	public void setShape(int width, int height) {
		this.shape = new ShapeDto(width, height);
	}

	public BoundingBoxDto getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(BoundingBoxDto boundingBox) {
		this.boundingBox = boundingBox;
	}

	@Override
	public String toString() {
		return "TrajectoryDTO [streamId=" + streamId + ", classId=" + classId + ", objectId=" + objectId
				+ ", hasGeoCoordinates=" + hasGeoCoordinates + ", coordinates=" + coordinates + ", boundingBox=" + boundingBox
				+ ", receiveTimestamp=" + receiveTimestamp + "]";
	}
}
