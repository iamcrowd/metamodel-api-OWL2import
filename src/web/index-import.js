var jsonOutput = "";
var jsonUnsupported = "";

$(document).ready(function () {
    $('#copyOutput').click(function (event) {
        event.preventDefault();
        copyToClipboardByValue(jsonOutput);
    });

    $('#copyUnsupported').click(function (event) {
        event.preventDefault();
        copyToClipboardByValue(jsonUnsupported);
    });

    $('#send').click(function () {
        url = "http://localhost:3333/owlnormalisedtometa";
        var formData = new FormData();
        formData.append('onto', $('#jsonInput')[0].value.toString());
        files = $("#ontoFile")[0].files;
        console.log(files);
        if (files.length) {
            for (var i = 0; i < files.length; i++) {
                formData.append('ontoFile', files[i]);
            }
        }
        formData.append('entity', $('#jsonInput2')[0].value.toString());
        formData.append('reasoning', $('#reasoning').is(":checked"));

        switch ($('#convertid')[0].value) {
            case "Show Ontology":
                url = "http://localhost:3333/showontology";
                break;
            case "Show Normalised Ontology":
                url = "http://localhost:3333/shownormalisedontology";
                break;
            case "Normalised":
                url = "http://localhost:3333/owlnormalisedtometa";
                break;
            case "Normalised 1-A":
                url = "http://localhost:3333/owlnormalised1atometa";
                break;
            case "Normalised 1-B":
                url = "http://localhost:3333/owlnormalised1btometa";
                break;
            case "Normalised 1-C":
                url = "http://localhost:3333/owlnormalised1ctometa";
                break;
            case "Normalised 1-D":
                url = "http://localhost:3333/owlnormalised1dtometa";
                break;
            case "Normalised 2":
                url = "http://localhost:3333/owlnormalised2tometa";
                break;
            case "Normalised 3":
                url = "http://localhost:3333/owlnormalised3tometa";
                break;
            case "Normalised 4":
                url = "http://localhost:3333/owlnormalised4tometa";
                break;
            default:
                url = "http://localhost:3333/owlclassestometa";
                break;
        }
        ;

        $.ajax({
            url: url,
            method: "POST",
            enctype: 'multipart/form-data',
            processData: false, // tell jQuery not to process the data
            contentType: false, // tell jQuery not to set contentType
            cache: false,
            data: formData
        }).done(function (response) {
            console.log(response);
            try {
                if (response.kf && response.unsupported && response.metrics) {
                    //$("#jsonOutput").val(JSON.stringify(response.kf, undefined, 4));
                    jsonOutput = JSON.stringify(response.kf, undefined, 4);
                    $("#jsonOutput").jsonViewer(response.kf);
                    
                    showResponse(response);
                } else if (response.success) {
                    jsonOutput = JSON.stringify(response, undefined, 4);
                    $("#jsonOutput").jsonViewer(response, {collapsed: true});

                    var avgResponse = {
                        unsupported: {},
                        metrics: {
                            nOfLogAxioms: {},
                            nOfEntities: {},
                            nOfNormAxioms: {},
                            nOfNormEntities: {},
                            nOfLogUnsupportedAxioms: {},
                            nOfFresh: {},
                            nOfImport: {},
                            importingTime: {},
                            nOfClassesInOrig: {},
                            nOfObjectPropertiesInOrig: {},
                            nOfDataPropertiesInOrig: {},
                            nOfClassesInNormalised: {},
                            nOfObjectPropertiesInNormalised: {},
                            nOfDataPropertiesInNormalised: {},
                            nOfAx1A: {},
                            nOfAx1B: {},
                            nOfAx1C: {},
                            nOfAx1D: {},
                            nOfAx2A: {},
                            nOfAx2B: {},
                            nOfAx2C: {},
                            nOfAx2D: {},
                            nOfAx3: {},
                            nOfAx3Inv: {},
                            nOfAx4: {},
                            nOfAx4Inv: {}
                        }
                    }

                    Object.entries(avgResponse.metrics).forEach(([key, value]) => {
                        avgResponse.metrics[key].avg = 0;
                        avgResponse.metrics[key].total = 0;
                        avgResponse.metrics[key].max = Math.max();
                        avgResponse.metrics[key].min = Math.min();
                    });

                    Object.entries(response.success).forEach(([respKey, resp]) => {
                        Object.entries(resp.unsupported).forEach(([unsKey, uns]) => {
                            avgResponse.unsupported["axiom" + Object.keys(avgResponse.unsupported).length] = uns;
                        });
                        Object.entries(resp.metrics).forEach(([metricKey, metric]) => {
                            avgResponse.metrics[metricKey].total = avgResponse.metrics[metricKey].total + metric;
                            avgResponse.metrics[metricKey].max = (avgResponse.metrics[metricKey].max > metric) ? avgResponse.metrics[metricKey].max : metric;
                            avgResponse.metrics[metricKey].min = (avgResponse.metrics[metricKey].min < metric) ? avgResponse.metrics[metricKey].min : metric;
                        });
                    });

                    Object.entries(avgResponse.metrics).forEach(([metricKey, metric]) => {
                        avgResponse.metrics[metricKey].avg = avgResponse.metrics[metricKey].total / Object.entries(response.success).length;
                    });

                    Object.entries(avgResponse.metrics).forEach(([key, value]) => {
                        avgResponse.metrics[key] = "total: " + avgResponse.metrics[key].total + ", avg: " + avgResponse.metrics[key].avg.toFixed(3) + ", min: " + avgResponse.metrics[key].min + ", max: " + avgResponse.metrics[key].max;
                    });

                    showResponse(avgResponse);
                } else {
                    throw Exception;
                }
            } catch (e) {
                console.log(e)
                //$("#jsonOutput").val(JSON.stringify(response, undefined, 4));
                jsonOutput = JSON.stringify(response, undefined, 4);
                $("#jsonOutput").jsonViewer(response);
                
                $("#metrics").hide();
            }
        });
    })
});

function beautify() {
    var ugly = document.getElementById('jsonOutput').value;
    var obj = JSON.parse(ugly);
    var pretty = JSON.stringify(obj, undefined, 4);
    document.getElementById('jsonOutput').value = pretty;
}

function copyToClipboard(element) {
    var $temp = $("<textarea></textarea");
    $("body").append($temp);
    $temp.val($(element).text()).select();
    document.execCommand("copy");
    $temp.remove();
}

function copyToClipboardByValue(value) {
    var $temp = $("<input>");
    $("body").append($temp);
    $temp.val(value).select();
    document.execCommand("copy");
    $temp.remove();
}

function showResponse(response) {
    $("#metrics").show();

    jsonUnsupported = JSON.stringify(response.unsupported, undefined, 4);
    $("#jsonUnsupported").jsonViewer(response.unsupported);

    $("#nOfLogAxioms").html(response.metrics.nOfLogAxioms);
    $("#nOfEntities").html(response.metrics.nOfEntities);
    $("#nOfNormAxioms").html(response.metrics.nOfNormAxioms);
    $("#nOfNormEntities").html(response.metrics.nOfNormEntities);
    $("#nOfLogUnsupportedAxioms").html(response.metrics.nOfLogUnsupportedAxioms);
    $("#nOfFresh").html(response.metrics.nOfFresh);
    $("#nOfImport").html(response.metrics.nOfImport);
    $("#importingTime").html(response.metrics.importingTime);
    $("#nOfClassesInOrig").html(response.metrics.nOfClassesInOrig);
    $("#nOfObjectPropertiesInOrig").html(response.metrics.nOfObjectPropertiesInOrig);
    $("#nOfDataPropertiesInOrig").html(response.metrics.nOfDataPropertiesInOrig);
    $("#nOfClassesInNormalised").html(response.metrics.nOfClassesInNormalised);
    $("#nOfObjectPropertiesInNormalised").html(response.metrics.nOfObjectPropertiesInNormalised);
    $("#nOfDataPropertiesInNormalised").html(response.metrics.nOfDataPropertiesInNormalised);
    $("#nOfAx1A").html(response.metrics.nOfAx1A);
    $("#nOfAx1B").html(response.metrics.nOfAx1B);
    $("#nOfAx1C").html(response.metrics.nOfAx1C);
    $("#nOfAx1D").html(response.metrics.nOfAx1D);
    $("#nOfAx2A").html(response.metrics.nOfAx2A);
    $("#nOfAx2B").html(response.metrics.nOfAx2B);
    $("#nOfAx2C").html(response.metrics.nOfAx2C);
    $("#nOfAx2D").html(response.metrics.nOfAx2D);
    $("#nOfAx3").html(response.metrics.nOfAx3);
    $("#nOfAx3Inv").html(response.metrics.nOfAx3Inv);
    $("#nOfAx4").html(response.metrics.nOfAx4);
    $("#nOfAx4Inv").html(response.metrics.nOfAx4Inv);
}