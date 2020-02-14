$(document).ready(function () {
    $('#send').click(function () {
        switch($('#convertid')[0].value){
            case "OWL Classes to Metamodel":
                url = "http://localhost:8080/owlclassestometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "text/plain"
                        },
                        "data": $('#jsonInput')[0].value.toString(),
                    };
                break;
            case "All OWL SubClasses to Metamodel":
                url = "http://localhost:8080/owlallsubstometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "text/plain"
                        },
                        "data": $('#jsonInput')[0].value.toString(),
                    };
                break;
            case "One OWL SubClass to Metamodel":
                url = "http://localhost:8080/owlonesubstometa";
                var settings = {
                        "url": url,
                        "method": "POST",
                        "timeout": 0,
                        "headers": {
                            "Content-Type": "text/plain"
                        },
                        "data": {
                        	onto: $('#jsonInput')[0].value.toString(),
                        	entity: $(jsonInput2)[0].value.toString()
                        }
                    };
                break;
            default:
                url = "http://localhost:8080/owlclassestometa";
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