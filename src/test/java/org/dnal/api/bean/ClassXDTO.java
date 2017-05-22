package org.dnal.api.bean;

import java.math.BigDecimal;
import java.util.Date;

public class ClassXDTO {
	private int nn1;
	private Integer nn2;
	private short sh1;
	Short sh2;
	String sstr1;
	private BigDecimal bbigd1;
	long nlong1;
	Long nlong2;
	boolean bb1;
	Boolean bb2;
	double dd1;
	Double dd2;
	float ff1;
	Float ff2;
	Date ddt1;
	Direction ddirection1;

	public ClassXDTO(int nn1, Integer nn2) {
		super();
		this.nn1 = nn1;
		this.nn2 = nn2;
	}
	public int getNn1() {
		return nn1;
	}
	public void setNn1(int nn1) {
		this.nn1 = nn1;
	}
	public Integer getNn2() {
		return nn2;
	}
	public void setNn2(Integer nn2) {
		this.nn2 = nn2;
	}
	public short getSh1() {
		return sh1;
	}
	public void setSh1(short sh1) {
		this.sh1 = sh1;
	}
	public String getSstr1() {
		return sstr1;
	}
	public void setSstr1(String sstr1) {
		this.sstr1 = sstr1;
	}
	public BigDecimal getBbigd1() {
		return bbigd1;
	}
	public void setBbigd1(BigDecimal bbigd1) {
		this.bbigd1 = bbigd1;
	}
	public Short getSh2() {
		return sh2;
	}
	public void setSh2(Short sh2) {
		this.sh2 = sh2;
	}
	public long getNlong1() {
		return nlong1;
	}
	public void setNlong1(long nlong1) {
		this.nlong1 = nlong1;
	}
	public Long getNlong2() {
		return nlong2;
	}
	public void setNlong2(Long nlong2) {
		this.nlong2 = nlong2;
	}
	public boolean isBb1() {
		return bb1;
	}
	public void setBb1(boolean bb1) {
		this.bb1 = bb1;
	}
	public Boolean getBb2() {
		return bb2;
	}
	public void setBb2(Boolean bb2) {
		this.bb2 = bb2;
	}
	public double getDd1() {
		return dd1;
	}
	public void setDd1(double dd1) {
		this.dd1 = dd1;
	}
	public Double getDd2() {
		return dd2;
	}
	public void setDd2(Double dd2) {
		this.dd2 = dd2;
	}
	public float getFf1() {
		return ff1;
	}
	public void setFf1(float ff1) {
		this.ff1 = ff1;
	}
	public Float getFf2() {
		return ff2;
	}
	public void setFf2(Float ff2) {
		this.ff2 = ff2;
	}
	public Date getDdt1() {
		return ddt1;
	}
	public void setDdt1(Date ddt1) {
		this.ddt1 = ddt1;
	}
	public Direction getDdirection1() {
		return ddirection1;
	}
	public void setDdirection1(Direction ddirection1) {
		this.ddirection1 = ddirection1;
	}
}