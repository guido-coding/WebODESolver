
import {Header} from './App.jsx';
import {useState} from 'react';

//Thorium
const thoriumOdeModel = {
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

const thoriumInitialIntegrationParams = {
  start: 0,
  end: 50,
  stepsize: 0.003,
  algorithm: "RK4",
};

const thorium = {
  model: thoriumOdeModel,
  integrationParams: thoriumInitialIntegrationParams,
  expdata: "",
  info: "Simplified model for the Thorium decay series (omitting short lived intermediates)."
};


//Biomass
const biomassOdeModel = {
  equations: [
    {
      variable: "Biomass",
      equation: "mumax * Biomass * (substrate / (substrate + Ks)) * (substrate2 / (substrate2 + Ks2))",
      initialValue: "1",
      allownegative: false,
      uom: "gr"
    },
    {
      variable: "substrate",
      equation: "-1 * ((mumax * Biomass)/Yxs) * (substrate / (substrate + Ks)) * (substrate2 / (substrate2 + Ks2)) + feedrate",
      initialValue: "50",
      allownegative: false,
      uom: "gr"      
    },
    {
      variable: "substrate2",
      equation: "-1 * ((mumax * Biomass)/Yxs2) * (substrate / (substrate + Ks)) * (substrate2 / (substrate2 + Ks2)) - prodrate * Biomass * (substrate2 / (substrate2 + Kp))",
      initialValue: "10",
      allownegative: false,
      uom: "gr"      
    },
    {
      variable: "product",
      equation: "prodrate * Biomass * (substrate2 / (substrate2 + Kp))",
      initialValue: "0",
      allownegative: false,
      uom: "gr"      
    },
  ],
  constants: [
    {
      varName: "mumax",
      varValue: 0.2,
      uom: "1/h"      
    },
    {
      varName: "Yxs",
      varValue: 0.4,
      uom: "g/g"      
    },
    {
      varName: "Ks",
      varValue: 0.1,
      uom: "gr"      
    },
    {
      varName: "feedrate",
      varValue: 0,
      uom: "gr/h"      
    },    
    {
      varName: "Ks2",
      varValue: 0.1,
      uom: "gr"      
    },
    {
      varName: "Yxs2",
      varValue: 5.0,
      uom: "gr/gr"      
    },  
    {
      varName: "Kp",
      varValue: 6.0,
      uom: "gr"      
    },
    {
      varName: "prodrate",
      varValue: 0.05,
      uom: "gr/hr"      
    },

  ],
  dependentVar: "t",
  uom: "hr"
};

const biomassInitialIntegrationParams = {
  start: 0,
  end: 50,
  stepsize: 0.05,
  algorithm: "RK4",
};

const biomassData = "";

const biomass = {
  model: biomassOdeModel,
  integrationParams: biomassInitialIntegrationParams,
  expdata: biomassData,
  info: "Biomass growth model describing growth on 2 essential substrates and product formation using one of those substrates."
};


//Empyt
const emptyOdeModel = {
  equations: [
    {
      variable: "Variable_1",
      equation: "",
      initialValue: "1",
      allownegative: true,
    }
  ],
  constants: [
    {
      varName: "constant_1",
      varValue: 1,
    } 
  ],
  dependentVar: "t",
};

const emptyInitialIntegrationParams = {
  start: 0,
  end: 1,
  stepsize: 0.1,
  algorithm: "RK4",
};

const emptyData = "";

const empty = {
  model: emptyOdeModel,
  integrationParams: emptyInitialIntegrationParams,
  expdata: emptyData,
  info: "Empty model"
};


//Biomass simple
const biomassSimpleOdeModel = {
  equations: [
    {
      variable: "Biomass",
      equation: "mumax * Biomass * (substrate / (substrate + Ks))",
      initialValue: "1",
      allownegative: false,
      uom: "gr"
    },
    {
      variable: "substrate",
      equation: "-1 * ((mumax * Biomass)/Yxs) * (substrate / (substrate + Ks))",
      initialValue: "50",
      allownegative: false,
      uom: "gr"      
    }
  ],
  constants: [
    {
      varName: "mumax",
      varValue: 0.2,
      uom: "1/hr"      
    },
    {
      varName: "Yxs",
      varValue: 0.4,
      uom: "gr/gr"      
    },
    {
      varName: "Ks",
      varValue: 0.1,
      uom: "gr"      
    }
  ],
  dependentVar: "t",
  uom: "hr"
};


const biomassSimpleInitialIntegrationParams = {
  start: 0,
  end: 20,
  stepsize: 0.005,
  algorithm: "RK4",
};

const biomassSimpleData = "t_hours: 0; 5; 10; 15; 20\nBiomass: 2; 3; 8; 20; 19\nsubstrate: 49; 45; 35; 5; 0";

const biomassSimple = {
  model: biomassSimpleOdeModel,
  integrationParams: biomassSimpleInitialIntegrationParams,
  expdata: biomassSimpleData,
  info: `
    Simple biomass growth model. Equation to describe production of new biomass and consumption of substrate.
  `
};



//all data
const data = {
  thorium: thorium,
  biomass: biomass,
  empty: empty,
  biomassSimple: biomassSimple
};






//other settings
const debug = false;

//Saved data
let savedODEModel = null;
let savedInitialIntParas = null;
let savedData = null;



export function Examples({odemodel, setOdemodel, integrationParams, setIntegrationParams, expData, setExpData}) {

  const [visible, setVisible] = useState(true);
  const [selectedExample, setSelectedExample] = useState("thorium");

  function setExample() {

    if (selectedExample === "saved") {
        if (savedODEModel !== null && savedInitialIntParas !== null && savedData !== null) {
            setOdemodel(savedODEModel);
            setIntegrationParams(savedInitialIntParas);
            setExpData(savedData);
        } else {
            alert("No saved data");
        }
    } else {
      let selectedData = data[selectedExample];
      if (selectedData !== null && selectedData !== undefined) {
        setOdemodel(selectedData.model);
        setIntegrationParams(selectedData.integrationParams);
        setExpData(selectedData.expdata);
      } else {
        alert("Invalid dataset");
      }
    }


    /*
    if (selectedExample === "thorium") {        
        setOdemodel(thoriumOdeModel);
        setIntegrationParams(thoriumInitialIntegrationParams);
        setExpData("");
        
    } else if (selectedExample === "biomass") {
        setOdemodel(biomassOdeModel);
        setIntegrationParams(biomassInitialIntegrationParams);
        setExpData(biomassData);
    } else if (selectedExample === "saved") {
        if (savedODEModel !== null && savedInitialIntParas !== null && savedData !== null) {
            setOdemodel(savedODEModel);
            setIntegrationParams(savedInitialIntParas);
            setExpData(savedData);
        } else {
            alert("No saved data");
        }
    } else if (selectedExample === "empty") {
        setOdemodel(emptyOdeModel);
        setIntegrationParams(emptyInitialIntegrationParams);
        setExpData(emptyData);
    } else if (selectedExample === "biomasssimple") {
        setOdemodel(biomassSimpleOdeModel);
        setIntegrationParams(biomassSimpleInitialIntegrationParams);
        setExpData(biomassSimpleData);
    } else {
        alert(selectedExample);
        return;
    }
        */
  }

  function save() {
    savedODEModel = odemodel;
    savedInitialIntParas = integrationParams;
    savedData = expData;
  }

  function updateSelectedExample(e) {
    setSelectedExample(e.target.value);
  }

    return(
        <div className="box">
            <Header title="Examples" visible={visible} setVisible={setVisible} displayClass="header3" />
            {visible ?               
                <div className="content">
                    <p>
                        <b>Load example:</b>  
                    </p>                    
                    <p>
                        Load an example. Warning: all unsaved entered data will be lost.
                    </p>
                    <hr />
                    <p>
                        <label>
                            <span>Save current data: </span>
                            <span><input type="button" value="Save" onClick={save} /></span>
                        </label>
                    </p>
                    <hr />
                    <p>
                        <label>
                        <span>
                            Load example:
                        </span>
                        <span>
                            <select value={selectedExample} onChange={updateSelectedExample} >
                                <option value="thorium">Thorium decay series</option>
                                <option value="biomassSimple">Simple bacterial growth model</option>                                
                                <option value="biomass">Bacterial growth and product production model</option>
                                <option value="empty">Empty model (clear data)</option>
                                <option value="saved">Saved data</option>
                            </select>
                        </span>
                        </label>
                    </p>
                    <Info selectedExample={selectedExample} />
                    <p>
                        <span><input type="button" value="Load example" onClick={setExample} /></span>
                    </p>
                    {debug ?
                      <Debug odemodel={odemodel} integrationParams={integrationParams} expData={expData} />
                      : <></>
                    }
                </div>
            : <></>}
        </div>
    );
}

function Info({selectedExample}) {

  let selectedData = data[selectedExample];

  if (selectedData === null || selectedData === undefined) {
    return (
      <></>
    );
  }

  return(
    <p className="info">
      {selectedData.info}
    </p>
  );
}


function Debug({odemodel, integrationParams, expData}) {

  return(
    <div>
      <hr />
      <p>
        <b>Debug information</b>
      </p>
      <p>
        <span><b>Odemodel: </b></span>
        <span>
        {JSON.stringify(odemodel)}
        </span>
      </p>
      <br />
      <p>
        <span><b>Integration params:</b></span>
        <span>
        {JSON.stringify(integrationParams)}
        </span>
      </p>
      <br />
      <p>
        <span><b>User data:</b></span>
        <span>
        {JSON.stringify(expData)}
        </span>
      </p>
    </div>
  );
}