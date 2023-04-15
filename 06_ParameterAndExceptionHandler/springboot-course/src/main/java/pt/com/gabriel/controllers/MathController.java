package pt.com.gabriel.controllers;

import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import pt.com.gabriel.converters.NumberConverter;
import pt.com.gabriel.exceptions.UnsupportedMathOperationException;
import pt.com.gabriel.math.SimpleMath;

@RestController
public class MathController {

	private final AtomicLong counter = new AtomicLong();
	private SimpleMath math = new SimpleMath();
	
	@RequestMapping(value="/sum/{numberOne}/{numberTwo}",method = RequestMethod.GET)
	public Double sum(@PathVariable("numberOne") String numberOne, 
			@PathVariable("numberTwo") String numberTwo) throws Exception {
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		return math.sum(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value="/sub/{numberOne}/{numberTwo}",method = RequestMethod.GET)
	public Double sub(@PathVariable("numberOne") String numberOne, 
			@PathVariable("numberTwo") String numberTwo) throws Exception {
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		return math.sub(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value="/times/{numberOne}/{numberTwo}",method = RequestMethod.GET)
	public Double times(@PathVariable("numberOne") String numberOne, 
			@PathVariable("numberTwo") String numberTwo) throws Exception {
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		return math.times(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value="/div/{numberOne}/{numberTwo}",method = RequestMethod.GET)
	public Double div(@PathVariable("numberOne") String numberOne, 
			@PathVariable("numberTwo") String numberTwo) throws Exception {
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		if(NumberConverter.convertToDouble(numberTwo) == 0D) {
			throw new UnsupportedMathOperationException("It's not possible divide by zero");
		}
		
		return math.div(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value="/mod/{numberOne}/{numberTwo}",method = RequestMethod.GET)
	public Double mod(@PathVariable("numberOne") String numberOne, 
			@PathVariable("numberTwo") String numberTwo) throws Exception {
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		return math.mod(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value="/avg/{numberOne}/{numberTwo}",method = RequestMethod.GET)
	public Double avg(@PathVariable("numberOne") String numberOne, 
			@PathVariable("numberTwo") String numberTwo) throws Exception {
		if(!NumberConverter.isNumeric(numberOne) || !NumberConverter.isNumeric(numberTwo)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		
		return math.avg(NumberConverter.convertToDouble(numberOne), NumberConverter.convertToDouble(numberTwo));
	}
	
	@RequestMapping(value="/sqrt/{number}",method = RequestMethod.GET)
	public Double sqrt(@PathVariable("number") String number) throws Exception {
		if(!NumberConverter.isNumeric(number)) {
			throw new UnsupportedMathOperationException("Please set a numeric value");
		}
		
		
		return math.sqrt(NumberConverter.convertToDouble(number));
	}
	
	
}
