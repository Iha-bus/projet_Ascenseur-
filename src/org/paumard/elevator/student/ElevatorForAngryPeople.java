package org.paumard.elevator.student;

import org.paumard.elevator.Building;
import org.paumard.elevator.Elevator;
import org.paumard.elevator.model.Person;
import java.time.Duration;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ElevatorForAngryPeople implements Elevator {
	public static final LocalTime MORNING_TIME = LocalTime.of(10, 0, 0);
	public static final LocalTime EVENING_TIME = LocalTime.of(15, 30, 0);
	private static final int ANGER_LIMIT_THRESHOLD = 300;
	private int currentFloor = 1;
	private List<List<Person>> peopleByFloor = List.of();
	private List<Person> people = new ArrayList<>();
	private List<Person> listPeopleInElevator = new ArrayList<>();
	private final int capacity;
	private LocalTime time;
	private List<Integer> destinations = List.of();
	private final String id;

	public ElevatorForAngryPeople(int capacity, String id) {
		this.id = id;
		this.capacity = capacity;
	}

	@Override
	public void startsAtFloor(LocalTime time, int initialFloor) {
		this.time = time;
	}

	@Override
	public void peopleWaiting(List<List<Person>> peopleByFloor) {
		this.setPeopleByFloor(peopleByFloor);
	}

	@Override
	public List<Integer> chooseNextFloors() {
		int numberOfPeopleWaiting = countWaitingPeople();
		if (!this.destinations.isEmpty()) {
			return this.destinations;
		}
		if (numberOfPeopleWaiting > 0 && this.time != Building.END_OF_DAY) {
			List<Integer> destinations = destinationsToPickUpAngryPeople();
			if (!destinations.isEmpty()) {
				this.destinations = destinations;
				return this.destinations;
			}
		} else {
			return List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		}
		return List.of(1);

	}

	private List<Integer> destinationsToPickUpAngryPeople() {

		for (int indexFloor = 0; indexFloor < Building.MAX_FLOOR; indexFloor++) {
			List<Person> waitingList = this.getPeopleByFloor().get(indexFloor);
			if (!waitingList.isEmpty()) {
				Person mostPatientPerson = waitingList.get(0);
				LocalTime arrivalTime = mostPatientPerson.getArrivalTime();
				Duration waitingTime = Duration.between(arrivalTime, this.time);
				long waitingTimeInSeconds = waitingTime.toSeconds();
				if (waitingTimeInSeconds >= ANGER_LIMIT_THRESHOLD) {
					if (this.currentFloor == indexFloor + 1) {
						List<Integer> result = List.of(mostPatientPerson.getDestinationFloor());
						return new ArrayList<>(result);
					} else {
						List<Integer> result = List.of(indexFloor + 1, mostPatientPerson.getDestinationFloor());
						return new ArrayList<>(result);
					}
				}
			}
		}
		return List.of();
	}

	private int countWaitingPeople() {
		return getPeopleByFloor().stream().mapToInt(list -> list.size()).sum();
	}

	@Override
	public void arriveAtFloor(int floor) {
		if (!this.destinations.isEmpty()) {
			this.destinations.remove(0);
		}
		this.currentFloor = floor;
	}

	@Override
	public void loadPeople(List<Person> people) {
		this.people.addAll(people);
		int indexFloor = this.currentFloor - 1;
		this.getPeopleByFloor().get(indexFloor).removeAll(people);
		this.listPeopleInElevator.addAll(people);
	}

	@Override
	public void unload(List<Person> people) {
		this.people.removeAll(people);
	}

	@Override
	public void newPersonWaitingAtFloor(int floor, Person person) {
		int indexFloor = floor - 1;
		this.getPeopleByFloor().get(indexFloor).add(person);
	}

	@Override
	public void lastPersonArrived() {
	}

	@Override
	public void timeIs(LocalTime time) {
		this.time = time;
	}

	@Override
	public void standByAtFloor(int currentFloor) {
	}

	@Override
	public String getId() {
		return this.id;
	}

	public List<List<Person>> getPeopleByFloor() {
		return peopleByFloor;
	}

	public void setPeopleByFloor(List<List<Person>> peopleByFloor) {
		this.peopleByFloor = peopleByFloor;
	}

	public int getCapacity() {
		return capacity;
	}
}