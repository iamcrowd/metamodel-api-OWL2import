$(document).ready(function () {
    $('#send').click(function () {
        switch($('#convertid')[0].value){
            case "Show Ontology":
                url = "http://localhost:3333/showontology";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break;
            case "OWL Classes to Metamodel":
                url = "http://localhost:3333/owlclassestometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break;
            case "All OWL SubClasses to Metamodel":
                url = "http://localhost:3333/owlallsubstometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break;
            case "One OWL SubClass to Metamodel":
                url = "http://localhost:3333/owlonesubstometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $('#jsonInput2')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break;
             case "Normalised":
                url = "http://localhost:3333/owlnormalisedtometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $('#jsonInput2')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break; 

             case "Normalised 1-A":
                url = "http://localhost:3333/owlnormalised1atometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $('#jsonInput2')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break; 


             case "Normalised 1-B":
                url = "http://localhost:3333/owlnormalised1btometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $('#jsonInput2')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break; 

             case "Normalised 1-C":
                url = "http://localhost:3333/owlnormalised1ctometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $('#jsonInput2')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break; 

             case "Normalised 1-D":
                url = "http://localhost:3333/owlnormalised1dtometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $('#jsonInput2')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break; 

             case "Normalised 2":
                url = "http://localhost:3333/owlnormalised2tometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $('#jsonInput2')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break; 

             case "Normalised 3":
                url = "http://localhost:3333/owlnormalised3tometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $('#jsonInput2')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break;          
 
             case "Normalised 4":
                url = "http://localhost:3333/owlnormalised4tometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $('#jsonInput2')[0].value.toString(),
                            reasoning: $('#reasoning').is(":checked")
                        }
                    };
                break; 
               
            case "ORM to Metamodel":
                url = "http://localhost:8080/ormtometa";
                break;
            case "Metamodel to UML":
                url = "http://localhost:8080/metatouml";
                break;
            case "Metamodel to ORM":
                url = "http://localhost:8080/metatoorm";
                break;
            case "Metamodel to EER":
                url = "http://localhost:8080/metatoeer";
                break;
            default:
                url = "http://localhost:3333/owlclassestometa";
                break;
        };


        $.ajax(settings).done(function (response) {
            console.log(response);
            $(jsonOutput)[0].value = JSON.stringify(response, undefined, 4);
        });
    })
});

function beautify() {
    var ugly = document.getElementById('jsonOutput').value;
    var obj = JSON.parse(ugly);
    var pretty = JSON.stringify(obj, undefined, 4);
    document.getElementById('jsonOutput').value = pretty;
}

$('#convertid')
