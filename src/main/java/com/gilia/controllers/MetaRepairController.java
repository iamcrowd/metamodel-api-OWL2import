package com.gilia.controllers;

import static com.gilia.utils.Constants.*;

import com.gilia.ontologyrepair.MetaRepairer;
import com.gilia.utils.*;

import org.apache.commons.lang3.exception.*;
import org.apache.jena.atlas.json.JSON;
import org.json.simple.JSONObject;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

/**
 * Controller of the OWL_TO_META_ROUTE endpoint. This controller is in charge of
 * receiving an OWL spec, creating the Metamodel instance and returning a
 * Metamodel JSON.
 */
@RestController
@CrossOrigin(origins = "*")
public class MetaRepairController {

    @PostMapping(value = META_REPAIR)
    public ResponseEntity metaRepair(
            @RequestParam(value = "meta", required = true) String meta,
            @RequestParam(value = "ontology", required = true) String ontology,
            @RequestParam(value = "entity", required = true, defaultValue = "") String entity,
            @RequestParam(value = "reasoner", required = false, defaultValue = "") String reasoner,
            @RequestParam(value = "maxExplanations", required = false, defaultValue = "4") String maxExplanations,
            @RequestParam(value = "precompute", required = false, defaultValue = "true") Boolean precompute,
            @RequestParam(value = "filtering", required = false, defaultValue = "true") Boolean filtering) {
        JSONObject result;

        try {
            if (meta == null || ontology == null || entity.equals(""))
                return new ResponseEntity<>(
                        "There is needed a KF serialized metamodel, their respective RDF/XML ontology translation and a determined unsatisfiable entity type of the KF.",
                        HttpStatus.BAD_REQUEST);

            // try cast maxExplanations to int
            int maxExplanationsInt = Integer.parseInt(maxExplanations);

            // create the repairer
            MetaRepairer repairer = new MetaRepairer(maxExplanationsInt, precompute, filtering);

            // load the metamodel for generate an operative Metamodel object from KFAPI
            System.out.println("Starting metamodel loading");
            repairer.loadMetamodel(meta);
            System.out.println("Finished metamodel loading");

            // load the ontology for generate an operative OWLOntology object from OWLAPI
            System.out.println("Starting ontology loading");
            repairer.loadOntology(ontology);
            System.out.println("Finished ontology loading");

            // prepare the reasoning service
            System.out.println("Loading reasoner: " + reasoner);
            repairer.loadReasoner(reasoner);

            // call the repairer for generate the MUPS for the entity and the ontology TBox
            boolean success = repairer.repair(entity);

            if (success)
                result = repairer.toJSON();
            else {
                JSONObject message = new JSONObject();
                message.put("message", "The concept " + entity + " is satisfiable. No repair is needed.");
                result = message;
            }
            System.out.println(new org.json.JSONObject(result.toJSONString()).toString(4));
        } catch (Exception e) {
            ResponseError error = new ResponseError(HttpStatus.BAD_REQUEST.value(),
                    HttpStatus.BAD_REQUEST.getReasonPhrase(), e.getMessage());
            JSONObject jsonError = error.toJSONObject();
            if (e != null && e.getCause() != null)
                jsonError.put("stackTrace", ExceptionUtils.getStackTrace(e.getCause()));
            return new ResponseEntity<>(jsonError, HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
