package nl.guidobreuer.webodesolver.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.guidobreuer.webodesolver.dto.DatasetDTO;
import nl.guidobreuer.webodesolver.dto.GraphDTO;
import nl.guidobreuer.webodesolver.dto.OptionsDTO;

/*
 * List of graphs (map)
 * 		Map, entry datasets (list)
 * 			List of datasets (map)
 * 				Map per dataset, 
 * 					label,
 * 					line
 * 					backgroundcolor
 * 					data
 * 						list of maps containing x,y pairs
 */
/*
return """
[
	{
		"datasets":
			[
				{
					"label":"Scatter Dataset",
					"data":[{"x":-10,"y":0},{"x":0,"y":10},{"x":10,"y":5},{"x":-10.5,"y":5.5}],
					"backgroundColor":"rgb(255, 99, 132)"
				},{
					"label":"Scatter Dataset 2",
					"showLine":true,
					"data":[{"x":-4,"y":1},{"x":2,"y":8},{"x":1,"y":9},{"x":10.5,"y":4.5}],
					"backgroundColor":"rgb(0, 99, 132)"
				}
			]
	}
]
		""";
		*/

public class DatasetBuilder {
	
	private static final int MAX_DATASET_SIZE = 100;
	
	public static List<GraphDTO> buildDataset(List<Map<String, Double>> results, ODEModel model) {
		return buildDataset(results, model, null);
	}
	
	public static List<GraphDTO> buildDataset(List<Map<String, Double>> results, ODEModel model, Map<String, double[]> expdata) {
		List<GraphDTO> graphs = new ArrayList<GraphDTO>();
		
		//iterate over all model variables
		model.getIndependentVars().forEach(var -> {
			
			List<DatasetDTO> listOfDatasets = new ArrayList<DatasetDTO>();
			
			//create model dataset
			DatasetDTO modelDataset = createModelDataset(var, model, results);
			//add model datasets to list of all datasets
			listOfDatasets.add(modelDataset);
			
			
			//add user provided data
			if (expdata != null && expdata.containsKey(model.getDependentVar()) && expdata.containsKey(var)) {
				DatasetDTO expDataset = createUserDataDataset(var, model, expdata);
				listOfDatasets.add(expDataset);
			}
			
			
			//create map with dataset and options for each graph
			GraphDTO datasetMap = new GraphDTO(listOfDatasets, new OptionsDTO(model.getDependentVar(), var), var);
			
			graphs.add(datasetMap);
		});
		
		return graphs;
	}
	
	
	
	public static List<GraphDTO> createDataset() {
		List<GraphDTO> graphs = new ArrayList<GraphDTO>();
		return graphs;
	}
	
	
	public static List<GraphDTO> createDataset(ODEModel model) {
		List<GraphDTO> graphs = new ArrayList<GraphDTO>();
		
		//iterate over all model variables
		model.getIndependentVars().forEach(var -> {
			
			String indepvarUoM = model.getVariableUoM(var);
			String indepVarSuffix = (indepvarUoM.equals("")) ? "" : " (" + indepvarUoM + ")";
			
			String depvarUoM = model.getDepVarUoM();
			String depvarSuffix = (depvarUoM.equals("")) ? "" : " ("+depvarUoM+")";
			
			List<DatasetDTO> listOfDatasets = new ArrayList<DatasetDTO>();
			GraphDTO datasetMap = new GraphDTO(
					listOfDatasets, 
					new OptionsDTO(
							model.getDependentVar() + depvarSuffix, 
							var + indepVarSuffix), 
					var);
			graphs.add(datasetMap);
		});
			
		return graphs;
	}
	
	
	
	public static List<GraphDTO> appendModelDataset(List<GraphDTO> graphs, List<Map<String, Double>> results, ODEModel model) {
		return appendModelDataset(graphs, results, model, null);
	}
	
	/*
	public static List<GraphDTO> appendModelDataset(List<GraphDTO> graphs, List<Map<String, Double>> results, ODEModel model, String suffix) {
		return appendModelDataset(graphs, results, model, suffix, 0);
	}
	*/
	
	public static List<GraphDTO> appendModelDataset(List<GraphDTO> graphs, List<Map<String, Double>> results, ODEModel model, String suffix) {
		//iterate over all model variables
		
		for (GraphDTO datasetMap : graphs) {
			List<DatasetDTO> listOfDatasets = datasetMap.getDatasets();
			DatasetDTO modelDataset = createModelDataset(datasetMap.getVariable(), suffix, model, results, listOfDatasets.size());
			listOfDatasets.add(modelDataset);
		}
		
		return graphs;
	}
	
	public static List<GraphDTO> appendUserdataDataset(List<GraphDTO> graphs, Map<String, double[]> expdata, ODEModel model) {
		//iterate over all model variables
		/*
		for (GraphDTO datasetMap : graphs) {
			List<DatasetDTO> listOfDatasets = datasetMap.getDatasets();
			DatasetDTO userdataDataset = createUserDataDataset(datasetMap.getVariable(), model, expdata);
			if (userdataDataset != null) {
				listOfDatasets.add(userdataDataset);
			}
		}
		*/
		
		if (expdata == null) {
			return graphs;
		}
		
		outer:
		for (String var : expdata.keySet()) {
			if (var.equals(model.getDependentVar())) {
				continue;
			}
			
			DatasetDTO userdataDataset = createUserDataDataset(var, model, expdata);
			if (userdataDataset == null) {
				continue;
			}
			
			for (GraphDTO datasetMap : graphs) {
				if (var.equals(datasetMap.getVariable())) {					
					List<DatasetDTO> listOfDatasets = datasetMap.getDatasets();
					listOfDatasets.add(userdataDataset);
					continue outer;
				}
			}
			
			List<DatasetDTO> listOfDatasets = new ArrayList<DatasetDTO>();
			listOfDatasets.add(userdataDataset);
			
			String indepvarUoM = model.getVariableUoM(var);
			String indepVarSuffix = (indepvarUoM.equals("")) ? "" : " (" + indepvarUoM + ")";
			
			String depvarUoM = model.getDepVarUoM();
			String depvarSuffix = (depvarUoM.equals("")) ? "" : " ("+depvarUoM+")";
			
			GraphDTO datasetMap = new GraphDTO(
					listOfDatasets, 
					new OptionsDTO(
							model.getDependentVar() + depvarSuffix, 
							var + indepVarSuffix), 
					var);
			graphs.add(datasetMap);
			
		}
		

		return graphs;
	}
	
	
	
	
	private static DatasetDTO createModelDataset(String var, ODEModel model, List<Map<String, Double>> results) {
		return createModelDataset(var, null, model, results, 0);
	}
	
	private static final String[] COLOR_OPTIONS = new String[] {
		"rgb(255, 0, 0)",
		"rgb(255, 255, 0)",
		"rgb(255, 0, 255)",
		"rgb(0, 255, 0)",
		"rgb(0, 0, 255)",
		"rgb(0, 255, 255)",
		"rgb(155, 150, 0)"
	};
	
	private static DatasetDTO createModelDataset(String var, String suffix, ODEModel model, List<Map<String, Double>> results, int entry) {
		String label;
		if (suffix == null) {
			label = var + " (" + model.getDependentVar() + ") - model";
		} else {
			label = var + " (" + model.getDependentVar() + ") - model " + suffix;
		}
		
		String color = COLOR_OPTIONS[entry % COLOR_OPTIONS.length];
		
		//create dataset for model and set parameters
		DatasetDTO modelDataset = new DatasetDTO(
				label,
				color,
				color,
				true,
				results.size() > 20 ? 0 : 3);
		
		//set datapoints from model integration to dataset
		if (results.size() <= MAX_DATASET_SIZE) {				
			for (Map<String, Double> resultsTimepoint : results) {
				modelDataset.addDatapoint(resultsTimepoint.get(model.getDependentVar()).doubleValue(), resultsTimepoint.get(var).doubleValue());
			}
		} else {
			int step = results.size() / MAX_DATASET_SIZE;
			for (int i=0; i<results.size(); i+=step) {
				Map<String, Double> resultsTimepoint = results.get(i);
				modelDataset.addDatapoint(resultsTimepoint.get(model.getDependentVar()).doubleValue(), resultsTimepoint.get(var).doubleValue());
			}
			
		}
		return modelDataset;
	}
	
	private static DatasetDTO createUserDataDataset(String var, ODEModel model, Map<String, double[]> expdata) {
		if (expdata == null || !expdata.containsKey(model.getDependentVar()) || !expdata.containsKey(var)) {
			return null;
		}
		DatasetDTO expDataset = new DatasetDTO(
				var + " (" + model.getDependentVar() + ") - data",
				"rgb(0, 0, 255)",
				"rgb(0, 0, 255)",
				false,
				3);
		
		double[] depVar = expdata.get(model.getDependentVar());
		double[] indepVar = expdata.get(var);
		int items = depVar.length < indepVar.length ? depVar.length : indepVar.length;
		for (int i=0; i<items; i++) {
			expDataset.addDatapoint(depVar[i], indepVar[i]);
		}

		return expDataset;
	}
	
	
	/**
	 * 
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws NumberFormatException
	 */
	public static Map<String, double[]> mapData(String data) throws IOException, NumberFormatException {
		if (data == null || data.equals("")) {
			return null;
		}
		
		Map<String, double[]> output = new HashMap<String, double[]>();
		String[] rows = data.split("\\r?\\n");
		
		for (String row : rows) {
			String[] splittedRow = row.strip().split(":");
			if (splittedRow.length != 2) throw new IOException("Invalid formatted data");
			String[] valuesAsString = splittedRow[1].split(";");
			double[] values = new double[valuesAsString.length];
			for (int i=0; i<values.length; i++) {
				values[i] = Double.parseDouble(valuesAsString[i].strip());
			}
			output.put(splittedRow[0].strip(), values);
		}
		
		return output;
	}

}
