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
        file = $("#ontoFile")[0].files[0] ? $("#ontoFile")[0].files[0] : null;
        var formData = new FormData();
        formData.append('onto', $('#jsonInput')[0].value.toString());
        formData.append('ontoFile', file);
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
                //$("#jsonOutput").val(JSON.stringify(response.kf, undefined, 4));
                jsonOutput = JSON.stringify(response.kf, undefined, 4);
                $("#jsonOutput").jsonViewer(response.kf);
                jsonUnsupported = JSON.stringify(response.unsupported, undefined, 4);
                $("#jsonUnsupported").jsonViewer(response.unsupported);

                $("#metrics").show();
                $("#nOfLogAxioms").html(response.metrics.nOfLogAxioms);
                $("#nOfEntities").html(response.metrics.nOfEntities);
                $("#nOfNormAxioms").html(response.metrics.nOfNormAxioms);
                $("#nOfNormEntities").html(response.metrics.nOfNormEntities);
                $("#nOfLogUnsupportedAxioms").html(response.metrics.nOfLogUnsupportedAxioms);
                $("#nOfFresh").html(response.metrics.nOfFresh);
                $("#nOfImport").html(response.metrics.nOfImport);
                $("#importingTime").html(response.metrics.importingTime);
            } catch (e) {
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