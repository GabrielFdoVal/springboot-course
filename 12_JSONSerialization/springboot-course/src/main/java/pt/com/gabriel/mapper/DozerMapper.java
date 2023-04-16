package pt.com.gabriel.mapper;

import java.util.ArrayList;
import java.util.List;

import com.github.dozermapper.core.DozerBeanMapperBuilder;
import com.github.dozermapper.core.Mapper;

public class DozerMapper {

	private static Mapper mapper = DozerBeanMapperBuilder.buildDefault();
	
	public static <O, D> D parseObject(O origem, Class<D> destination) {
		return mapper.map(origem, destination);
	}
	
	public static <O, D> List<D> parseListObjects(List<O> origem, Class<D> destination) {
		List<D> destinationObjects = new ArrayList<>();
		for (O o: origem) {
			destinationObjects.add(mapper.map(o, destination));
		}
		return destinationObjects;
	}
}
