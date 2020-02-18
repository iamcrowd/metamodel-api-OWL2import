$(document).ready(function () {
    $('#sendJson').click(function () {
        switch($('#convertid')[0].value){
            case "UML to Metamodel":
                url = "http://localhost:8080/umltometa";
                break;
            case "EER to Metamodel":
                url = "http://localhost:8080/eertometa";
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
                url = "http://localhost:8080/umltometa";
                break;
        }
        var settings = {
            "url": url,
            "method": "POST",
            "timeout": 0,
            "headers": {
                "Content-Type": "application/json"
            },
            "data": $(jsonInput)[0].value,
        };

        $.ajax(settings).done(function (response) {
            console.log(response);
            $(jsonOutput)[0].value = JSON.stringify(response, undefined, 4);
        });
    })
});

function beautify() {
    var ugly = document.getElementById('jsonInput').value;
    var obj = JSON.parse(ugly);
    var pretty = JSON.stringify(obj, undefined, 4);
    document.getElementById('jsonInput').value = pretty;
}

$('#convertid')