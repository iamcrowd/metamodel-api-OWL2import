$(document).ready(function () {
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
        };

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
