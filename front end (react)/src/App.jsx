import './App.css';
import {Examples} from './Examples.jsx';

import { Chart as ChartJS,
  LinearScale,
  PointElement,
  LineElement,
  Tooltip,
  Legend, } from "chart.js";
import { Scatter } from "react-chartjs-2";
import {useState} from 'react';

ChartJS.register(LinearScale, PointElement, LineElement, Tooltip, Legend);

const initOdeModel = {
  equations: [
    {
      variable: "Ra228",
      equation: "-1 * Ra228 * k1",
      initialValue: "100",
      allownegative: false,
      uom: "%"
    },
    {
      variable: "Th228",
      equation: "Ra228 * k1 - Th228 * k2",
      initialValue: "0",
      allownegative: false,
      uom: "%"
    },
    {
      variable: "Ra224",
      equation: "Th228 * k2 - Ra224 * k3",
      initialValue: "0",
      allownegative: false,        
      uom: "%"      
    }, {
      variable: "Pb212",
      equation: "Ra224 * k3 - Pb212 * k4",
      initialValue: "0",
      allownegative: false,  
      uom: "%"           
    }, {
      variable: "Pb208",
      equation: "Pb212 * k4",
      initialValue: "0",
      allownegative: false,    
      uom: "%"          
    }
  ],
  constants: [
    {
      varName: "k1",
      varValue: 0.12,
      uom: "1/year"
    },
    {
      varName: "k2",
      varValue: 0.36,
      uom: "1/year"
    },
    {
      varName: "k3",
      varValue: 69,
      uom: "1/year"      
    },
    {
      varName: "k4",
      varValue: 573,
      uom: "1/year"      
    }        
  ],
  dependentVar: "t",
  uom: "years"
};


const initialIntegrationParams = {
  start: 0,
  end: 50,
  stepsize: 0.003,
  algorithm: "RK4",
};


//const apiurl = "http://localhost:8080/";
const apiurl = "https://apps.guidobreuer.nl/odesolverapi/"


function App() {
  const [odemodel, setOdemodel] = useState(initOdeModel);
  const [integrationParams, setIntegrationParams] = useState(initialIntegrationParams);
  const [graphData, setGraphData] = useState([]);
  const [errorMessage, setErrorMessage] = useState(null);
  const [expData, setExpData] = useState("");

  return (
    <div>
      <ErrorMessage errorMessage={errorMessage} setErrorMessage={setErrorMessage} />
      <Examples odemodel={odemodel} setOdemodel={setOdemodel} integrationParams={integrationParams} setIntegrationParams={setIntegrationParams} expData={expData} setExpData={setExpData} />
      <Data expData={expData} setExpData={setExpData} odemodel={odemodel} integrationParams={integrationParams} setErrorMessage={setErrorMessage} setGraphData={setGraphData} />
      <ODEModel odemodel={odemodel} setOdemodel={setOdemodel} />
      <Constants odemodel={odemodel} setOdemodel={setOdemodel} />
      <Integrator integrationParams={integrationParams} setIntegrationParams={setIntegrationParams} odemodel={odemodel} setOdemodel={setOdemodel} setGraphData={setGraphData} expData={expData} setErrorMessage={setErrorMessage} />
      <Graph graphData={graphData} />
    </div>
  );
  
}

function ErrorMessage({errorMessage, setErrorMessage}) {

  function reset(e) {
    e.preventDefault();
    setErrorMessage(null);
  }

  return (
    <>
    {
     (errorMessage != null) ?
      <div className="error">
        <p>
          <span>
            <b><u>Error:</u></b> {errorMessage}
          </span>
          <span>
            |
          </span>
          <span>
            <a href="#" onClick={reset} >X</a>
          </span>
        </p>
      </div>
      :
        <></>
      
    }
    </>
  );
}

export function Header({title, visible, setVisible, displayClass}) {

  function handleClick(e) {
    e.preventDefault();
    setVisible(!visible);
  }

  return (
    <div className={displayClass} >
        <span><b>{title}</b></span>
        <span><a href="#" onClick={handleClick} >Show/hide</a></span>
    </div>
  );
}

function Data({expData, setExpData, odemodel, integrationParams, setErrorMessage, setGraphData}) {
  const [visible, setVisible] = useState(true);

  function showData() {
    let body = {
      integrationParams: integrationParams,
      model: odemodel,
      expdata: expData
    };

    try {
      fetch(apiurl + "plotuserdata", {
        method: "POST",  
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body)
      })
      .then(response => {
        if (!response.ok) { 
          return response.text().then(err => Promise.reject(err));
        } else {
          return response.json();
        }
      })
      .then(data => {
        setGraphData(data);
        setErrorMessage(null);
      })
      .catch((error) => {;
          setErrorMessage(error.toString());
      });

    } catch(e) {
      console.log("error 2");
    }
  }

  const eq = odemodel.equations.find(Boolean);
  const variable = (eq === null || eq === undefined) ? "y" : eq.variable;

  return (
    <div className="box">
      <Header title="Data" visible={visible} setVisible={setVisible} displayClass="header" />  
      {
        visible ?
        <div className="content">
          
            <p><b>Enter data</b></p>
            <p>Format data as one row per variable. Start with variable name, followed by ":", and then data. Separate each number by a ";".</p>
             <p>For example:</p>
            <p>{odemodel.dependentVar}: 0; 0.5; 1.0; 1.5</p>
            <p>{variable}: 4; 5.5; 7.0; 3.5</p>
            <hr />
            <label>
              <p>Data:</p>
              <p><textarea cols="80" rows="6" value={expData} onChange={e => setExpData(e.target.value)} /></p>
            </label>
            <div className="intbutton">
              <p>
                <input type="button" value="Plot data" onClick={showData} />
              </p>
            </div>
        </div>
        : <></>
      }
    </div>
  );
}

function ODEModel({odemodel, setOdemodel}) {

  function add() {
    let odemodelTemp = {...odemodel};
    
    odemodelTemp.equations.push({variable: "y" + (odemodelTemp.equations.length+1),  equation: "", initialValue: "0", allownegative: true});
    setOdemodel(odemodelTemp);
  }

  function changeDepVar(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.dependentVar = e.target.value;
    setOdemodel(odemodelTemp);
  }

  function changeUoM(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.uom = e.target.value;
    setOdemodel(odemodelTemp);
  }

  let rows = [];
  for (let equation in odemodel.equations) {
    rows.push(<Equation key={equation} id={equation} equation={odemodel.equations[equation]} odemodel={odemodel} setOdemodel={setOdemodel} />);
  }

  const [visible, setVisible] = useState(true);

  return (
    <div className="box">
      <Header title="Model" visible={visible} setVisible={setVisible} displayClass="header" />  
      {visible ?   
        <div className="content">
          <div>
            <p><b>Enter model equations:</b></p>
            <ul>
              <li>Enter dependent variable to be used for integration.</li>
              <li>
                Enter model variables. Variable names are case-sensitive, cannot contain whitespaces (" "), and cannot start with a number.
              </li>
              <li>
                Type equations using the specified variable names (Model) or constant names (Model constants). 
                More equations can be added and equations can be removed.
                Valid operators are: +, -, *, /, ^, (, ), sin, cos, tan, mod, sqrt, log.
              </li>
              <li>
                Enter initial values that should be used for integration.
              </li>
              <li>Select if negative values are allowed for each variable.</li>
            </ul>
            <hr />
          </div>
          <div>
            <p>
              <span>
                <label>
                  Dependent variable: <input type="text" size="6" value={odemodel.dependentVar} onChange={changeDepVar} />
                </label>
              </span>
              <span>
                <label>
                  Unit of measure: 
                  {
                    ("uom" in odemodel) ?
                      <input type="text" size="6" value={odemodel.uom} onChange={changeUoM} />
                    :
                      <input type="text" size="6" value="" onChange={changeUoM} />
                  }
                </label>
              </span>
            </p>
          </div>
          <div>
            {rows}
          </div>
          <div className="button">
            <span><input type="button" value="Add equation" onClick={add} /></span>
          </div>
        </div>  
        : <></>
      }
    </div>
  );
}

function Equation({id, equation, odemodel, setOdemodel}) {

  function deleteEntry() {
    let odemodelTemp = {...odemodel};
    delete odemodelTemp.equations[id];
    setOdemodel(odemodelTemp);
  }

  function changeVariable(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.equations[id].variable = e.target.value;
    setOdemodel(odemodelTemp);
  }

  function changeEquation(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.equations[id].equation = e.target.value;
    setOdemodel(odemodelTemp);
  }

  function changeInitValue(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.equations[id].initialValue = e.target.value;
    setOdemodel(odemodelTemp);
  }

  function changeAllowNegative(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.equations[id].allownegative = e.target.checked;
    setOdemodel(odemodelTemp);
  }

  function changeUoM(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.equations[id].uom = e.target.value;
    setOdemodel(odemodelTemp);
  }


  return (
    <div className="equationPanel">
      <div className="equationInput">
        <p>
          <span><label>Variable name: <input type="text" size="9" value={equation.variable} onChange={changeVariable} /></label></span>   
          <span><label>Initial value: <input type="number" size="3" value={equation.initialValue} onChange={changeInitValue} /></label></span>
          {
            ("uom" in equation) ?
              <span><label>Unit of measure: <input type="text" size="4" value={equation.uom} onChange={changeUoM} /></label></span>
            :
              <span><label>Unit of measure: <input type="text" size="4" value="" onChange={changeUoM} /></label></span>
          }
          <span><label><input type="checkbox" checked={equation.allownegative} onChange={changeAllowNegative} /> Allow negative values</label></span>
        </p>
        <p>
          <span style={{minWidth: 150, display: 'inline-block', textAlign: 'left'}}>
            <label><sup>d<b><i>{equation.variable}</i></b></sup>&frasl;<sub>d<i><b>{odemodel.dependentVar}</b></i></sub> = </label>
          </span>
          <span>
            <input type="text" size="80" value={equation.equation} onChange={changeEquation} />
          </span>
          <span>
            (<sup>{equation.uom}</sup>/<sub>{odemodel.uom}</sub>)
          </span>
        </p>
      </div>
      <div className="equationButton">
          <input type="button" value="delete" onClick={deleteEntry} />
      </div>
    </div>
  );
}

function Constants({odemodel, setOdemodel}) {

  const [visible, setVisible] = useState(true);

  function add() {
    let odemodelTemp = {...odemodel};
    odemodelTemp.constants.push({varName: "k" + (odemodelTemp.constants.length+1), varValue: 1.0});
    setOdemodel(odemodelTemp);
  }

  let rows = [];

  for (let constant in odemodel.constants) {
    rows.push(<Constant key={constant} id={constant} constant={odemodel.constants[constant]} odemodel={odemodel} setOdemodel={setOdemodel} />);
  }

  return (
    <div className="box">
      <Header title="Model constants" visible={visible} setVisible={setVisible} displayClass="header" />
      {visible ? 
        <div className="content">
          <div>
            <p><b>Enter model constants:</b></p>
            <p>Enter model constants and their values. Constant names are case-sensitive, cannot contain whitespaces (" "), and cannot start with a number.</p>
            <hr />
          </div>
          <div>
            {rows}
          </div>
          <div className="button">
            <p>
              <span><input type="button" value="Add constant" onClick={add} /></span>
            </p>
          </div>
        </div>

        : <></>}
    </div>
  );
}

function Constant({id, constant, odemodel, setOdemodel}) {

  function deleteEntry() {
    let odemodelTemp = {...odemodel};
    delete odemodelTemp.constants[id];
    setOdemodel(odemodelTemp);
  }

  function changeVarName(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.constants[id].varName = e.target.value;
    setOdemodel(odemodelTemp);
  }

  function changeVarValue(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.constants[id].varValue = e.target.value;
    setOdemodel(odemodelTemp);
  }

  function changeUoM(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.constants[id].uom = e.target.value;
    setOdemodel(odemodelTemp);
  }

  return(
    <p>
      <span><label>Constant name: 
        <input type="text" value={constant.varName} onChange={changeVarName} /></label></span>
      <span><label>Constant value: 
      <input type="number" value={constant.varValue} onChange={changeVarValue} /></label></span>
      <span>
        <label>
          Unit of measure:

          {(("uom" in constant)) ? 
            <input type="text" size="4" value={constant.uom} onChange={changeUoM} />
          :
            <input type="text" size="4" value="" onChange={changeUoM} />
          } 
        </label>
      </span>
      <span><input type="button" value="Delete constant" onClick={deleteEntry} /></span>
    </p>
  );
}

function Integrator({integrationParams, setIntegrationParams, odemodel, setOdemodel, setGraphData, expData, setErrorMessage}) {

  function updateStart(e) {
    let temp = {...integrationParams};
    temp.start = e.target.value;
    setIntegrationParams(temp);
  }

  function updateEnd(e) {
    let temp = {...integrationParams};
    temp.end = e.target.value;
    setIntegrationParams(temp);
  }

  function updateStepsize(e) {
    let temp = {...integrationParams};
    temp.stepsize = e.target.value;
    setIntegrationParams(temp);
  }

  function updateAlgorithm(e) {
    let temp = {...integrationParams};
    temp.algorithm = e.target.value;
    setIntegrationParams(temp);
  }

  const [visible, setVisible] = useState(true);

  return (
    <div className="box">
      <Header title="Integration" visible={visible} setVisible={setVisible} displayClass="header" />
      {visible ? 
      <div className="content">
        <div>
          <p><b>Enter integration range:</b></p>
          <p>
            Integration range and stepsize for <b>{odemodel.dependentVar}</b> and select integration algorithm:
          </p>
          
        </div>
        <div>          
          <p>
            <span><label>Start ({odemodel.dependentVar}): 
            <input type="number" value={integrationParams.start} onChange={updateStart} /></label></span>
            <span><label>End ({odemodel.dependentVar}): 
            <input type="number" value={integrationParams.end} onChange={updateEnd} /></label></span>
            <span><label>Stepsize ({odemodel.dependentVar}): 
            <input type="number" value={integrationParams.stepsize} onChange={updateStepsize} step="0.1" /></label></span>
            <span>{odemodel.uom}</span>
          </p>
          <p>
            <span>
              <label>
                <span>Integration algorithm: </span>
                <select value={integrationParams.algorithm} onChange={updateAlgorithm} >
                  <option value="Euler">Euler</option>
                  <option value="RK4">RK4</option>
                </select>
              </label>
            </span>
          </p>
          <hr />
         </div>

        <div>
          <Integrate odemodel={odemodel} setOdemodel={setOdemodel} integrationParams={integrationParams} expData={expData} setErrorMessage={setErrorMessage} setGraphData={setGraphData} />
          <hr />
          <SensitivityAnalysis odemodel={odemodel} setOdemodel={setOdemodel} integrationParams={integrationParams} expData={expData} setErrorMessage={setErrorMessage} setGraphData={setGraphData} /> 
          <hr />
          <ParameterEstimation odemodel={odemodel} setOdemodel={setOdemodel} integrationParams={integrationParams} expData={expData} setErrorMessage={setErrorMessage} setGraphData={setGraphData} />      
        </div>
      </div>      
    : <></>
    }
    </div>
  );
}

function Integrate({odemodel, setOdemodel, integrationParams, expData, setErrorMessage, setGraphData}) {
  const [visible, setVisible] = useState(true);

  function integrate() {
    let body = {
      integrationParams: integrationParams,
      model: odemodel,
      expdata: expData
    };

    try {
      fetch(apiurl + "integrate", {
        method: "POST",  
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body)
      })
      .then(response => {
        if (!response.ok) { 
          return response.text().then(err => Promise.reject(err));
        } else {
          return response.json();
        }
      })
      .then(data => {
        setGraphData(data);
        setErrorMessage(null);
        const element = document.getElementById('graphpanel');
        element?.scrollIntoView({behavior: 'smooth'}); 
      })
      .catch((error) => {;
          setErrorMessage(error.toString());
      });

    } catch(e) {
      console.log("error 2");
    }
  }

  return (
    <div>
      <Header title="Integrate" visible={visible} setVisible={setVisible} displayClass="header2" />
        {visible ? 
          <div className="content2">
            <div>
              <p>
                <b>Integrate:</b>
              </p>
              <p>
                Integrate model using provided model constant values.
              </p>
            </div>
            <div className="intbutton"> 
              <p>
                <span><input type="button" value="Integrate model" onClick={integrate} /></span>
              </p>
            </div> 
          </div>
        : <></>
        }
    </div>    
  );
}

function SensitivityAnalysis({odemodel, setOdemodel, integrationParams, expData, setErrorMessage, setGraphData}) {
  const [visible, setVisible] = useState(true);
  const firstConst = odemodel.constants.find(Boolean);
  const first = (firstConst === null || firstConst === undefined)? null : firstConst.varName;

  const [selectedConstant, setSelectedConstant] = useState(first);


  function sensitivity() {
    let body = {
      integrationParams: integrationParams,
      model: odemodel,
      expdata: expData
    };

    let selConstant = document.getElementById('sensitivityAnalysisConstantSelector').value;

    try {
      fetch(apiurl + "integrate/" + selConstant, {
        method: "POST",  
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body)
      })
      .then(response => {
        if (!response.ok) { 
          return response.text().then(err => Promise.reject(err));
        } else {
          return response.json();
        }
      })
      .then(data => {
        setGraphData(data);
        setErrorMessage(null);
        const element = document.getElementById('graphpanel');
        element?.scrollIntoView({behavior: 'smooth'});         
      })
      .catch((error) => {;
          setErrorMessage(error.toString());
      });

    } catch(e) {
      console.log("error 2");
    }
  }

  let rows = [];
  for (let constant in odemodel.constants) {
    rows.push(<option key={constant} value={odemodel.constants[constant].varName}>{odemodel.constants[constant].varName} </option>);
  }

  return (
    <div>
      <Header title="Parameter estimation" visible={visible} setVisible={setVisible} displayClass="header2" />
        {visible ? 
        
          <div className="content2">
            <p>
              <b>Sensitivity analysis:</b>
            </p>
            <p>Perform sensitivity analysis. 
              Integrate model using provided model constant values. Selected constant will be varied at 50%, 75%, 100%, 125% and 150% of its value entered under model constants.</p>
            <p>
            <label>
              <span>Select constant for sensitivity analysis: </span>
              <select id="sensitivityAnalysisConstantSelector" value={selectedConstant} onChange={e =>setSelectedConstant(e.target.value)}>
                {rows}
              </select>
            </label>
            </p>
            <div className="intbutton">
              <p>
                <input type="button" value="Perform sensitivity analysis" onClick={sensitivity} />
              </p>
              
            </div>
          </div>
        : <></>
        }
    </div>        
  );
}

function ParameterEstimation({odemodel, setOdemodel, integrationParams, expData, setErrorMessage, setGraphData}) {

  const [optAlgo, setOptAlgo] = useState("gradientdescent");
  const [visible, setVisible] = useState(true);

  function updateAlgo(e) {
    setOptAlgo(e.target.value);
  }

  function estimateParameters() {
    let body = {
      integrationParams: integrationParams,
      model: odemodel,
      expdata: expData,
      optimizationalgorithm: optAlgo
    };

    //console.log(JSON.stringify(body));

    try {
      fetch(apiurl + "estimateparams", {
        method: "POST",  
        headers: {
          'Accept': 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body)
      })
      .then(response => {
        if (!response.ok) { 
          return response.text().then(err => Promise.reject(err));
        } else {
          return response.json();
        }
      })
      .then(data => {
        setGraphData(data.graphdata);
        updateParams(data.estimatedParams);
        setErrorMessage(null);
        const element = document.getElementById('graphpanel');
        element?.scrollIntoView({behavior: 'smooth'});         
      })
      .catch((error) => {;
        setErrorMessage(error.toString());
      });
      
    } catch(e) {
      console.log("error 2");
    }
  }

    
  function updateParams(params) {

    let odemodelTemp = {...odemodel};
    
    //console.log(params);
    for (let param in params) {
      for (let constant in odemodel.constants) {
        if (odemodel.constants[constant].varName === param) {
          //console.log("name: " + param);
          //console.log("value: " + params[param]);
          odemodelTemp.constants[constant].varValue = params[param];
        }
      }
    }
    
    setOdemodel(odemodelTemp);

  }


  let paramEstRows = [];
  for (let constant in odemodel.constants) {
    paramEstRows.push(<EstimateParamInput key={constant} id={constant} constant={odemodel.constants[constant]} odemodel={odemodel} setOdemodel={setOdemodel} />);
  }

  return (
    <div>
      <Header title="Parameter estimation" visible={visible} setVisible={setVisible} displayClass="header2" />
        {visible ? 
        <div className="content2">
          <div>
            <p>
              <b>Parameter estimation:</b>  
            </p>
            <p>Estimate values for model constants (parameters) within the range provided below:</p>
              {paramEstRows}
            <p>
              <label>
              <span>Select optimization algorithm: </span>
              <span>
                <select value={optAlgo} onChange={updateAlgo} >
                  <option value="gradientdescent">Gradient descent</option>
                  <option value="montecarlo">Monte Carlo</option>
                </select>
              </span>
              </label>
            </p>
            <p>
              <u><b>Tip:</b></u> Use Monte-Carlo algorithm in case many local minima exist and initial values might be far from target values.
              Use Gradient Descent to converge towards nearest minimum. Gradient descent uses step sizes of 0.005 * the range between max and min. 
              Once a solution is found, narrow down the min and max values (range) to find more accurate solutions.
            </p>
          </div>
          <div className="intbutton">
            <p>
              <input type="button" value="Perform parameter estimation" onClick={estimateParameters} />
            </p>
          </div>
        </div>
        : <></>
        }
    </div>
  );
}

function EstimateParamInput({id, constant, odemodel, setOdemodel}) {


  function changeMin(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.constants[id].min = e.target.value;
    setOdemodel(odemodelTemp);
  }

  function changeMax(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.constants[id].max = e.target.value;
    setOdemodel(odemodelTemp);
  }

  function updateInclude(e) {
    let odemodelTemp = {...odemodel};
    odemodelTemp.constants[id].include = e.target.checked;
    setOdemodel(odemodelTemp);
    //console.log(JSON.stringify(odemodel));
  }


  return(
    <p>
      <span>Constant: </span>
      <span style={{"minWidth": "100px", display: 'inline-block'}}><b>{constant.varName}</b></span>
      {
        ("uom" in constant && (constant.uom !== null || constant.uom !== "" || constant.uom !== undefined)) ?
          <span style={{"minWidth": "120px", display: 'inline-block'}}>(value: {constant.varValue} {constant.uom})</span>
        :
          <span style={{"minWidth": "120px", display: 'inline-block'}}>(value: {constant.varValue})</span>
      }
      
      <span> </span>
      <label>
        <span>Min: </span>
        {
          (("min" in constant)) ? 
            <span><input type="number" value={constant.min} onChange={changeMin} /></span>
           : 
            <span><input type="number" value={constant.varValue * 0.5} onChange={changeMin} /></span>
        }
      </label>
      <span> </span>
      <span>
        <label>
          <span>Max: </span>
          {
            (("max" in constant)) ? 
              <span><input type="number" value={constant.max} onChange={changeMax} /></span>
            : 
              <span><input type="number" value={constant.varValue * 2} onChange={changeMax} /></span>
          }        
        </label>
      </span>
      <span style={{"minWidth": "35px", display: 'inline-block'}}>
        {constant.uom}  
      </span>    
      <span>
        <label>
          {
          ("include" in constant) ?
          <input type="checkbox" checked={constant.include} onChange={updateInclude} />
          :
          <input type="checkbox" checked={true} onChange={updateInclude} />
          }
          Include in parameter estimation
        </label>
      </span>
    </p>
  );
}

function Graph({graphData}) {

  let rows = [];
  for (let graph in graphData) {
    rows.push(
      <div key={graph} className="graph" >
        <Scatter options={graphData[graph].options} data={graphData[graph]} />
      </div>
    );
  }

  const [visible, setVisible] = useState(true);

  if (rows.length === 0) {
    rows.push(
        <p key="emptyresult">
          No results to show.
        </p>
    );
  }
  return (
      <div className="box" id="graphpanel">
        <Header title="Results" visible={visible} setVisible={setVisible} displayClass="header" />
        {visible ? 
          <div className="content">
            <div className="graphContainer">
              {rows}
            </div>
          </div>
          : <></>
        }
      </div>
  );
}



export default App;
