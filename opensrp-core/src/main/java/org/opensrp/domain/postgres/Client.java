package org.opensrp.domain.postgres;

public class Client {

	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.client.id
	 * @mbg.generated  Mon Mar 12 18:42:21 EAT 2018
	 */
	private Long id;
	/**
	 * This field was generated by MyBatis Generator. This field corresponds to the database column core.client.json
	 * @mbg.generated  Mon Mar 12 18:42:21 EAT 2018
	 */
	private Object json;

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.client.id
	 * @return  the value of core.client.id
	 * @mbg.generated  Mon Mar 12 18:42:21 EAT 2018
	 */
	public Long getId() {
		return id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.client.id
	 * @param id  the value for core.client.id
	 * @mbg.generated  Mon Mar 12 18:42:21 EAT 2018
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * This method was generated by MyBatis Generator. This method returns the value of the database column core.client.json
	 * @return  the value of core.client.json
	 * @mbg.generated  Mon Mar 12 18:42:21 EAT 2018
	 */
	public Object getJson() {
		return json;
	}

	/**
	 * This method was generated by MyBatis Generator. This method sets the value of the database column core.client.json
	 * @param json  the value for core.client.json
	 * @mbg.generated  Mon Mar 12 18:42:21 EAT 2018
	 */
	public void setJson(Object json) {
		this.json = json;
	}
}