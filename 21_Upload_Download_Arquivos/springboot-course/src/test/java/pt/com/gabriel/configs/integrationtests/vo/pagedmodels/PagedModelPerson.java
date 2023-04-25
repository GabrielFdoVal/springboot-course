package pt.com.gabriel.configs.integrationtests.vo.pagedmodels;

import java.util.List;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import pt.com.gabriel.configs.integrationtests.vo.PersonVO;

@XmlRootElement
public class PagedModelPerson {

	@XmlElement(name="content")
	private List<PersonVO> content;
	
	public PagedModelPerson() {}

	public List<PersonVO> getContent() {
		return content;
	}

	public void setContent(List<PersonVO> content) {
		this.content = content;
	}
}
