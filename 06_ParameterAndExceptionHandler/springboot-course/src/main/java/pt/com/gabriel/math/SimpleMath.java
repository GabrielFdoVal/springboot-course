package pt.com.gabriel.math;

public class SimpleMath {

	public Double sum(Double numberOne, Double numberTwo) {		
		return numberOne + numberTwo;
	}
	
	public Double sub(Double numberOne, Double numberTwo) {		
		return numberOne - numberTwo;
	}
	
	public Double times(Double numberOne, Double numberTwo) {		
		return numberOne * numberTwo;
	}
	
	public Double div(Double numberOne, Double numberTwo) {		
		return numberOne / numberTwo;
	}
	
	public Double mod(Double numberOne, Double numberTwo) {		
		return numberOne % numberTwo;
	}
	
	public Double avg(Double numberOne, Double numberTwo) {		
		return (numberOne + numberTwo) / 2;
	}
	
	public Double sqrt(Double number) {		
		return Math.sqrt(number);
	}
}
