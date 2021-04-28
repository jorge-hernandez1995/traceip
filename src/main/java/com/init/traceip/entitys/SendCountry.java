package com.init.traceip.entitys;

import java.io.Serializable;

public class SendCountry implements Serializable {

	private static final long serialVersionUID = 1L;
	private String name;
	private int distance;
	private int invocations;

	public SendCountry(String name, int distance, int invocations) {
		this.name = name;
		this.distance = distance;
		this.invocations = invocations;
	}

	public SendCountry(String name, int distance) {
		this.name = name;
		this.distance = distance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public int getInvocations() {
		return invocations;
	}

	public void setInvocations(int invocations) {
		this.invocations = invocations;
	}

}
