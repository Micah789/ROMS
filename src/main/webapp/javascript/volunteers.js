$(document).ready(function() {

    // create/edit
    $("#surname").blur(function() {
        matchVolunteerPerson($("#forename").val(), $("#surname").val(), $("#personId"));
    });

    $("#spouseSurname").blur(function() {
        matchLinkedPerson(
            $("#spouseForename").val(),
            $("#spouseSurname").val(),
            $("#spousePersonId"),
            populateSpouseFromPerson
        );
    });

    $("#emergencyContactSurname").blur(function() {
        matchLinkedPerson(
            $("#emergencyContactForename").val(),
            $("#emergencyContactSurname").val(),
            $("#emergencyContactPersonId"),
            populateEmergencyContactFromPerson
        );
    });

    $(".datepicker").datepicker({
        dateFormat: "dd/mm/yy",
        changeYear: true
    });

    // elder and ministerial values are exclusive
    $("input[name='elder']").change(function() {
        if($(this).is(':checked')) {
            $("input[name='ministerialServant']").prop("checked", false);
        }
    });
    $("input[name='ministerialServant']").change(function() {
        if($(this).is(':checked')) {
            $("input[name='elder']").prop("checked", false);
        }
    });

    $("#spouse-linked button.close").click(function() {
        populateSpouseFromPerson(null, $("#spousePersonId"));
    });

    $("#emergency-contact-linked button.close").click(function() {
        populateEmergencyContactFromPerson(null, $("#emergencyContactPersonId"));
    });

    $("#congregationName").typeahead({
        source: roms.common.congregationTypeAheadSource,
        minLength: 2
    });

    // when adding a trades row, clone the last one, clear the values
    // and at it after that row
    $("#trades-row-add").click(function() {
       var $lastTradesRow = $(".trades-row").last();
       var $clonedTradesRow = $lastTradesRow.clone();
       var lastIndex = $lastTradesRow.data("index");
       var nextIndex = lastIndex + 1;
       $clonedTradesRow.data("index", nextIndex);
       $clonedTradesRow.find("input").each(function() {
           $(this).val('');
           var name = $(this).prop("name");
           name = name.replace('[' + lastIndex + ']', '[' + nextIndex + ']');
           $(this).prop("name", name);
       })
       $clonedTradesRow.hide();
       $clonedTradesRow.insertAfter($lastTradesRow);
       initialiseTradeRow($clonedTradesRow);
       $clonedTradesRow.slideDown(500);
    });

    $("#volunteer").validate({
        rules: {
            forename: {
                minlength: 2,
                required: true
            },
            surname: {
                minlength: 2,
                required: true
            },
            gender: {
                required: true
            },
            birthDate: {
                required: true
            },
            baptismDate: {
                required: true
            },
            street: {
                minlength: 2,
                required: true
            },
            town: {
                minlength: 2,
                required: true
            },
            postcode: {
                minlength: 2,
                required: true
            },
            email: {
                required: true,
                email: true
            },
            maritalStatusId: {
                required: true
            },
            emergencyContactForename: {
                minlength: 2,
                required: true
            },
            emergencyContactSurname: {
                minlength: 2,
                required: true
            },
            emergencyContactStreet: {
                minlength: 2,
                required: true
            },
            emergencyContactTown: {
                minlength: 2,
                required: true
            },
            emergencyRelationshipId: {
                required: true
            },
            congregationName: {
                required: true,
                remote: {
                    // check for an exact match. Populate the congregation id
                    url: roms.common.relativePath + "/congregations/search",
                    contentType: "application/json",
                    dataType: "json",
                    data: {
                        name: function() {
                            return $("#congregationName").val();
                        }
                    },
                    dataFilter: function(rawData) {
                        var data = JSON.parse(rawData)
                        if (data.results && data.results[0].name == $("#congregationName").val()) {
                            $("#congregationId").val(data.results[0].id);
                            return true;

                        }
                        return false;
                    }
                }
            },
            congregationId: {
                required: true
            },
            formDate: {
                required: true
            }
        },
        submitHandler :function(form) {
            form.submit();
        },
        errorPlacement: roms.common.validatorErrorPlacement
    });

    $(".trades-row").each(function() {
        initialiseTradeRow($(this));
    });

    /**
     * Initialise the actions on the trade rows.
     * Note: this function call needs to be made after the initial form.validate
     * initialisation, otherwise we cannot add validation.
     */
    function initialiseTradeRow($row) {
        // limit inputs
        $(".trade-experience-years", $row).numeric({ negative : false, decimal: false });

        // add validation. completely empty rows are allowed (ignored server side)
        // so the field is only required if other fields are populated
        $(".trade-experience-name", $row).rules("add", {
			required: function() {
                var result = false;
                $("input", $row).each(function() {
                    if ($(this).val()) {
                        // the input has a value set, the name field is required
                        result = true;
                        // break the loop
                        return false;
                    }
                    return true;
                });
                return result;
            }
        });

        // add delete button functionality
        $(".trades-row-delete", $row).click(function() {
            // if this is the only row, clear the values instead
            if ($(".trades-row").length < 2) {
                $("input", $row).val('');
            } else {
                $row.slideUp(1000, new function() {
                    $row.remove();
                });
            }
        });
    }

    /**
     * Match the entered name of the volunteer with an existing person/volunteer
     * @param forename entered forename
     * @param surname entered surname
     * @param $personId jquery reference to the hidden field containing the person id
     */
    function matchVolunteerPerson(forename, surname, $personId) {

       if(!forename || !surname) {
           return;
       }

       var existingPersonName = $personId.data("full-name");
       if (existingPersonName == forename + " " + surname) {
           // no change in value
           return;
       }
       this.findVolunteerPerson(forename, surname, $personId, existingPersonName);

       $personId.data("full-name", forename + " " + surname);
    }

    /**
     * Look up the person matching the name and show a popover
     * to allow the user the select the match, if any
     */
    function findVolunteerPerson(forename, surname, $personId, existingPersonName)  {
        var existingPersonId = $personId.val();
        $.ajax({
            url: roms.common.relativePath + '/persons/search',
            contentType: "application/json",
            dataType: 'json',
            data:  {
                forename: forename,
                surname: surname,
                checkVolunteer: true
            },
            success: function(data) {
                // no match, and no person linked. We don't show anything
                if (!data.results && !existingPersonId) {
                    return;
                }

                // enrich the results, splitting out the volunters from the persons.
                // if we are matching an existing volunteer we don't want to create a new one
                var volunteers = new Array();
                var persons = new Array();
                if (data.results) {
                    for (var i = 0; i < data.results.length; i++) {
                        var result = data.results[i];

                        if (result.personId != existingPersonId) {
                            if (result.volunteer) {
                                volunteers.push(result);
                            } else {
                                persons.push(result);
                            }
                        }
                    }
                }

                data.existingPersonId = existingPersonId;
                data.existingPersonName = existingPersonName;
                if (volunteers.length > 0) {
                    data.matchedVolunteers = true;
                    data.volunteers = volunteers;
                }

                if (persons.length > 0) {
                    data.matchedPersons = true;
                    data.persons = persons;
                }

                var template = $("#volunteer-person-search-form").html();
                var html = Mustache.to_html(template, data);

                $("#volunteer-person-modal .modal-body").html(html)
                var modalElement = $("#volunteer-person-modal")

                modalElement.modal('show')

                // if they select the person id, set it to the hidden volunteer person id field
                $("a.matched-person").on("click", function(event){
                    populateVolunteerFromPerson($(this).data("person-id"), $personId);
                    modalElement.modal('hide')
                });
            }
        });
    }

    /**
     * Populate the volunteer form from the existing person
     */
    function populateVolunteerFromPerson(selectedPersonId, $personId) {
        // chosen nobody. Clear the existing value
        if (!selectedPersonId) {
            $personId.val(selectedPersonId);
            return;
        }
        var existingPersonId = $personId.val();

        if (existingPersonId == selectedPersonId) {
            return;
        }

        // new person - pull the person data and populate the form.
        $.ajax({
            url: roms.common.relativePath + '/persons/' + selectedPersonId  + "/reference",
            contentType: "application/json",
            dataType: 'json',
            success: function(data) {
                $("#birthDate").val(data.birthDate);
                $("input[name='middleName']").val(data.middleName);
                $("input[name='telephone']").val(data.telephone);
                $("input[name='mobile']").val(data.mobile);
                $("input[name='workPhone']").val(data.workPhone);
                $("input[name='email']").val(data.email);
                if(data.congregation) {
                    $("input[name='congregationId']").val(data.congregation.congregationId);
                }
                if (data.address) {
                    $("input[name='street']").val(data.address.street);
                    $("input[name='town']").val(data.address.town);
                    $("input[name='county']").val(data.address.county);
                    $("input[name='postcode']").val(data.address.postcode);
                }
            }
        });
        $personId.val(selectedPersonId);
    }

    function matchLinkedPerson(forename, surname, $personId, populateFunction) {
        if(!forename || !surname) {
            return;
        }

        var existingPersonName = $personId.data("full-name");
        if (existingPersonName == forename + " " + surname) {
            // no change in value
            return;
        }
        var existingPersonId = $personId.val();

        $.ajax({
            url: roms.common.relativePath + '/persons/search',
            contentType: "application/json",
            dataType: 'json',
            data:  {
                forename: forename,
                surname: surname,
                checkVolunteer: false
            },
            success: function(data) {
                // no match, and no person linked. We don't show anything
                if (!data.results && !existingPersonId) {
                    return;
                }

                data.existingPersonId = existingPersonId;
                data.existingPersonName = existingPersonName;

                if (data.results) {
                    data.matchedPersons = true;
                }

                var template = $("#volunteer-person-link-search-form").html();
                var html = Mustache.to_html(template, data);

                $("#volunteer-person-modal .modal-body").html(html)
                var modalElement = $("#volunteer-person-modal")

                modalElement.modal('show')

                // if they select the person id, set it to the hidden volunteer person id field
                $("a.matched-person").on("click", function(event){
                    populateFunction($(this).data("person-id"), $personId);
                    modalElement.modal('hide')
                });
            }
        });

        $personId.data("full-name", forename + " " + surname);
    }

    function populateSpouseFromPerson(selectedPersonId, $personId) {
        if (selectedPersonId) {
            $("#spouse-linked").show("fast");
        } else {
            $("#spouse-linked").hide("fast");
        }
        $personId.val(selectedPersonId);
    }

    function populateEmergencyContactFromPerson(selectedPersonId, $personId) {

        if (selectedPersonId) {
            // disable all the additional fields. include the text that indicates we are
            $("#emergency-contact-additional-fields input").prop("disabled", true);
            $("#emergency-contact-additional-fields").hide("fast");
            $("#emergency-contact-linked").show("fast");
        } else {
            $("#emergency-contact-additional-fields input").prop("disabled", false)
            $("#emergency-contact-additional-fields").show("fast");
            $("#emergency-contact-linked").hide("fast");
        }
        $personId.val(selectedPersonId);
    }

    // list
    var listActionTemplate = $("#list-action").html();

    roms.common.datatables(
        $('#volunteer-list'),
        {
            "iDisplayLength": 20,
            "bProcessing": true,
            "bServerSide": true,
            "sAjaxSource": roms.common.relativePath + '/volunteers',
            "aoColumns": [
                {   "sName": "ID", "mData": "id" },
                {   "sName": "forename", "mData": "forename" },
                {   "sName": "surname", "mData": "surname" },
                {   "sName": "congregation", "mData": "congregation.name", "sDefaultContent": "" },
                {   "sName": "action", "bSortable": false,
                    "mData":
                        function ( data, type, val ) {
                            data.uriBase = roms.common.relativePath;
                            return Mustache.to_html(listActionTemplate, data);
                        }
                }
            ]
        }
    );

    // display
    roms.common.datatables(
        $("#volunteer-skills-trades"),
        {
            "iDisplayLength": 20
        }
    );

});
