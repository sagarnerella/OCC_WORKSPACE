package com.osi.agent.webservice;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement
public class SnmpTableRow {

private List<String> value;

public List<String> getValue() {
	return value;
}

@XmlElement
public void setValue(List<String> value) {
	this.value = value;
}
}

