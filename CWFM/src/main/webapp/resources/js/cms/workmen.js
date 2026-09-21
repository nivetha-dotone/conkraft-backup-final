var divIndex=0;
//let selectedDocumentTypes = new Set();
function searchWithPEContractorInWorkmenAadharList(contextPath) {
	 var principalEmployerId = document.getElementById("principalEmployerId").value;
	 var contractorId = document.getElementById("contractorId").value;
 event.preventDefault();
     // Assuming you have jQuery available for making AJAX requests
     $.ajax({
         type: "GET",
         url: contextPath + "/contractworkmen/aadharOnbordingList",
         data: { principalEmployerId: principalEmployerId,contractorId:contractorId}, // Pass the search query as data
         success: function(response) {
             // Handle success response
           //  console.log("Search results:", response);
             document.getElementById("mainContent").innerHTML = response;
         },
         error: function(xhr, status, error) {
             // Handle error response
             console.error("Error searching:", error);
         }
     });
 }
 
 function redirectToWorkmenAadharOBAdd() {

    // Fetch the content of add.jsp using AJAX
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            // Update the mainContent element with the fetched content
            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };
    xhr.open("GET", "/CWFM/contractworkmen/addAadharOB", true);
    xhr.send();
}

function redirectToWorkmenQuickOBAdd() {

    // Fetch the content of add.jsp using AJAX
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            // Update the mainContent element with the fetched content
            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };
    xhr.open("GET", "/CWFM/contractworkmen/addQuickOB", true);
    xhr.send();
}
function getContractorsAndTrades(unitId, userAccount) {
    if (!unitId) {
        alert("Please select a Principal Employer.");
        return;
    }

    // Fetch contractors
    getContractors(unitId, userAccount);

    // Fetch trades
    getTrades(unitId);
    
    getDepartments(unitId);
    
    getZones(unitId);
}


function getContractors(unitId, userAccount) {
    var xhr = new XMLHttpRequest();
    var url = contextPath + "/contractworkmen/getAllContractors?unitId=" + unitId + "&userAccount=" + userAccount;
    //alert("URL: " + url);
    xhr.open("GET", url, true);

    xhr.onload = function() {
        if (xhr.status === 200) {
            // Parse the response as a JSON array of contractor objects
            var contractors = JSON.parse(xhr.responseText);
            console.log("Response:", contractors);
            
            // Find the contractor select element
            var contractorSelect = document.getElementById("contractor");
            
            // Clear existing options
            contractorSelect.innerHTML = '<option value="">Please select Contractor</option>';
            
            // Populate the dropdown with the new list of contractors
            contractors.forEach(function(contractor) {
                var option = document.createElement("option");
                option.value = contractor.contractorId;
                option.text = contractor.contractorName;
                contractorSelect.appendChild(option);
            });
			// after populating dropdown
			autoSelectAndTrigger("contractor", function () {
			    getWorkordersAndWC();
			});
        } else {
            console.error("Error:", xhr.statusText);
        }
    };

    xhr.onerror = function() {
        console.error("Request failed");
    };

    xhr.send();
}

function getWorkordersAndWC() {
	 var principalEmployerSelect = document.getElementById("principalEmployer");
    var unitId = principalEmployerSelect.value; // Get the selected principal employer value
    var contractorSelect = document.getElementById("contractor");
    var contractorId = contractorSelect.value; // Get the selected contractor value
    
 if (!unitId) {
        alert("Please select a Principal Employer.");
        return;
    }
    
  if(!contractorId){
	 alert("Please select a Contractor.");
        return;
  }

    getWorkorders(unitId, contractorId);

    //getWC(unitId, contractorId);
}

function getWorkorders(unitId,contractorId) {
    var xhr = new XMLHttpRequest();
    var url = contextPath + "/contractworkmen/getAllWorkOrders?unitId=" + unitId + "&contractorId=" + contractorId;
    //alert("URL: " + url);
    xhr.open("GET", url, true);

    xhr.onload = function() {
        if (xhr.status === 200) {
            // Parse the response as a JSON array of workorder objects
            var workorders = JSON.parse(xhr.responseText);
            console.log("Response:", workorders);
            
            // Find the workorder select element
            var workorderSelect = document.getElementById("workorder");
            
            // Clear existing options
            workorderSelect.innerHTML = '<option value="">Please select Workorder</option>';
            
            // Populate the dropdown with the new list of workorders
            workorders.forEach(function(workorder) {
                var option = document.createElement("option");
                option.value = workorder.workorderId;
                option.text = workorder.name;
                workorderSelect.appendChild(option);
            });
			autoSelectAndTrigger("workorder", function () {
			    getWC();
			});
        } else {
            console.error("Error:", xhr.statusText);
        }
    };

    xhr.onerror = function() {
        console.error("Request failed");
    };

    xhr.send();
}

function getWC() {
	var principalEmployerSelect = document.getElementById("principalEmployer");
    var unitId = principalEmployerSelect.value; // Get the selected principal employer value
    var contractorSelect = document.getElementById("contractor");
    var contractorId = contractorSelect.value; 
     var workorderSelect =document.getElementById("workorder");
     var workorderId = workorderSelect.value; 
    var xhr = new XMLHttpRequest();
    var url = contextPath + "/contractworkmen/getAllWC?unitId=" + unitId + "&contractorId=" + contractorId +"&workorderId=" + workorderId;
    //alert("URL: " + url+" "+unitId+" "+contractorId+" "+workorderId);
    xhr.open("GET", url, true);

    xhr.onload = function() {
        if (xhr.status === 200) {
            var wcs = JSON.parse(xhr.responseText);
            console.log("Response:", wcs);
            
            var wcSelect = document.getElementById("wc");
			var llSelect = document.getElementById("ll");
            wcSelect.innerHTML = '<option value="">Please select WC Policy/ESIC Reg Number</option>';
			llSelect.innerHTML = '<option value="">Please select Labor License</option>';
            wcs.forEach(function(wc) {
                var option = document.createElement("option");
                option.value = wc.wcId;
                option.text = wc.wcCode;
				 option.setAttribute("data-code", wc.licenceType);
				if (wc.licenceType === "LL") {
				                    llSelect.appendChild(option);
				                } else if (wc.licenceType === "WC" || wc.licenceType === "ESIC") {
				                    wcSelect.appendChild(option);
				                }
            });
			autoSelectAndTrigger("wc", function (wc) {
			    onWcChange(wcId);
			});
        } else {
            console.error("Error:", xhr.statusText);
        }
    };

    xhr.onerror = function() {
        console.error("Request failed");
    };

    xhr.send();
}


function getTrades(unitId) {
    var xhr = new XMLHttpRequest();
    var url = contextPath + "/contractworkmen/getAllTrades?unitId=" + unitId;
    console.log("Fetching trades from URL:", url);
    xhr.open("GET", url, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            var trades = JSON.parse(xhr.responseText);
            console.log("Trades:", trades);
            var tradeSelect = document.getElementById("trade");

            // Clear existing options
            tradeSelect.innerHTML = '<option value="">Please select Trade</option>';

            // Populate the trade dropdown
            trades.forEach(function (trade) {
                var option = document.createElement("option");
                option.value = trade.tradeId;
                option.text = trade.tradeName;
                tradeSelect.appendChild(option);
            });
			// after populating dropdown
			autoSelectAndTrigger("trade", function () {
			   getSkills();
			});
        } else {
            console.error("Error fetching trades:", xhr.statusText);
        }
    };

    xhr.onerror = function () {
        console.error("Request failed while fetching trades");
    };

    xhr.send();
}
function getSkills(unitId,tradeId) {
	var principalEmployerSelect = document.getElementById("principalEmployer");
    var unitId = principalEmployerSelect.value; // Get the selected principal employer value
    var tradeSelect = document.getElementById("trade");
    var tradeId = tradeSelect.value; 
    var xhr = new XMLHttpRequest();
    var url = contextPath + "/contractworkmen/getAllSkills?unitId=" + unitId + "&tradeId=" + tradeId;
    console.log("Fetching trades from URL:", url);
    xhr.open("GET", url, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            var skills = JSON.parse(xhr.responseText);
            console.log("Skills:", skills);
            var skillSelect = document.getElementById("skill");

            // Clear existing options
            skillSelect.innerHTML = '<option value="">Please select Skill</option>';

            // Populate the trade dropdown
            skills.forEach(function (skill) {
                var option = document.createElement("option");
                option.value = skill.skillId;
                option.text = skill.skill;
                skillSelect.appendChild(option);
            });
			// after populating dropdown
						autoSelectAndTrigger("skill", null, false);
        } else {
            console.error("Error fetching skills:", xhr.statusText);
        }
    };

    xhr.onerror = function () {
        console.error("Request failed while fetching skill");
    };

    xhr.send();
}

function getDepartments(unitId) {
    var xhr = new XMLHttpRequest();
    var url = contextPath + "/contractworkmen/getAllDepartments?unitId=" + unitId;
    console.log("Fetching departments from URL:", url);
    xhr.open("GET", url, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            var departments = JSON.parse(xhr.responseText);
            console.log("Departments:", departments);
            var departmentSelect = document.getElementById("department");

            // Clear existing options
            departmentSelect.innerHTML = '<option value="">Please select Department</option>';

            // Populate the trade dropdown
            departments.forEach(function (department) {
                var option = document.createElement("option");
                option.value = department.departmentId;
                option.text = department.department;
                departmentSelect.appendChild(option);
            });
			autoSelectAndTrigger("department", function () {
			   getAreabyDept();getEic();
			});
        } else {
            console.error("Error fetching departments:", xhr.statusText);
        }
    };

    xhr.onerror = function () {
        console.error("Request failed while fetching departments");
    };

    xhr.send();
}

function getAreabyDept(unitId,departmentId) {
	var principalEmployerSelect = document.getElementById("principalEmployer");
    var unitId = principalEmployerSelect.value; // Get the selected principal employer value
    var departmentSelect = document.getElementById("department");
    var departmentId = departmentSelect.value; 
    var xhr = new XMLHttpRequest();
    var url = contextPath + "/contractworkmen/getAllSubDepartments?unitId=" + unitId + "&departmentId=" + departmentId;
    console.log("Fetching subdepartment from URL:", url);
    xhr.open("GET", url, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            var subdepartment = JSON.parse(xhr.responseText);
            console.log("Areas:", subdepartment);
            var subdepartmentSelect = document.getElementById("subdepartment");

            // Clear existing options
            subdepartmentSelect.innerHTML = '<option value="">Please select Area</option>';

            // Populate the trade dropdown
            subdepartment.forEach(function (subdepartment) {
                var option = document.createElement("option");
                option.value = subdepartment.subDepartmentId;
                option.text = subdepartment.subDepartment;
                subdepartmentSelect.appendChild(option);
            });
			autoSelectAndTrigger("subdepartment", null, false);
        } else {
            console.error("Error fetching subdepartments:", xhr.statusText);
        }
    };

    xhr.onerror = function () {
        console.error("Request failed while fetching subdepartment");
    };

    xhr.send();
}

function initializeDatePicker() {
        $('.datetimepickerformat').datepicker({
            dateFormat: 'yy-mm-dd', // Set the date format
            changeMonth: true,      // Allow changing month via dropdown
            changeYear: true,       // Allow changing year via dropdown
            yearRange: "-100:+0",   // Set the year range from 100 years ago to the current year
            maxDate: 0              // Prevent selecting future dates
        });
    }
	function initializeDatePicker1() {
	    $('.datetimepickerformat1').datepicker({
	        dateFormat: 'yy-mm-dd', // Set the date format
	        changeMonth: true,      // Allow changing month via dropdown
	        changeYear: true,       // Allow changing year via dropdown
	        yearRange: "0:+100", 
	        minDate: 0,
			maxDate: +15               // Prevent selecting future dates
	    });
	} 
   function validateBasicData() {

    let isValid = true;
    let aadharCheckPassed = false;

    const aadharNumber = $("#aadharNumber").val().trim();

    if (aadharNumber === "" || aadharNumber.length !== 12 || isNaN(aadharNumber)) {
        $("#error-aadhar").show();

        isValid = false;
        aadharCheckPassed = false;

    } else {
        $.ajax({
            url: "/CWFM/contractworkmen/checkAadharExistsCreation",
            type: "GET",
            data: {
                aadharNumber: aadharNumber,
                gatePassId: $("#gatePassId").val(),
                transactionId: $("#transactionId").val()
            },

            async: false,
            success: function(response) {
                let status = response.status ? response.status.trim() : "";
                console.log("Submit Aadhaar validation status:", status);
                if (status !== "Invalid" && status !== "" && status !== "FOUND") {
                    $("#error-aadhar").text(status).show();
                    aadharCheckPassed = false;

                } else if (status === "Invalid") {
                    $("#error-aadhar").text("Invalid Aadhar Number").show();
                    aadharCheckPassed = false;

                } else if (status === "FOUND") {

                    /*
                     * VERY IMPORTANT:
                     * DO NOT CALL:
                     * loadCancelledAadharDetails(response);
                     * The user may have already modified the cancelled
                     * record details. We must preserve those changes.
                     */

                    $("#error-aadhar").hide();
                    aadharCheckPassed = true;

                } else {

                    $("#error-aadhar").hide();
                    aadharCheckPassed = true;
                }
            },
            error: function() {
                $("#error-aadhar").text("Unable to verify Aadhaar").show();
                aadharCheckPassed = false;
            }
        });
    }

    const firstName = $("#firstName").val().trim();
    const firstnameRegex = /^[A-Za-z\s]{2,}$/;
    if (!firstnameRegex.test(firstName)) {
        $("#error-firstName").show();
        isValid = false;

    } else {
        $("#error-firstName").hide();
    }

    const lastName = $("#lastName").val().trim();
    const lastnameRegex = /^[A-Za-z\s]{1,}$/;
    if (lastName !== "" && !lastnameRegex.test(lastName)) {
        $("#error-lastName").show();
        isValid = false;

    } else {
        $("#error-lastName").hide();
    }

    const dateOfBirth = $("#dateOfBirth").val().trim();
    if (dateOfBirth === "") {
        $("#error-dateOfBirth").show();
        isValid = false;

    } else {
        $("#error-dateOfBirth").hide();
    }

    const gender = $("#gender").val();
    if (gender === "") {
        $("#error-gender").show();
        isValid = false;

    } else {
        $("#error-gender").hide();
    }

    const relationName = $("#relationName").val().trim();
    const relationnameRegex = /^[A-Za-z\s]{2,}$/;
    if (relationName !== "" &&
        !relationnameRegex.test(relationName)) {
        $("#error-relationName").show();
        isValid = false;

    } else {
        $("#error-relationName").hide();
    }

    const mobileInput = $("#mobileNumber").val().trim();
	const mobileNumberRegex = /^[6-9]\d{9}$/;
	if (!mobileNumberRegex.test(mobileInput)) {
                 $("#error-mobileNumber").show();
        			isValid = false;
     }else{
		 $("#error-mobileNumber").hide();
	 }
	 

    const maritalInput = $("#maritalStatus").val();
    if (maritalInput === "") {
        $("#error-maritalStatus").show();
        isValid = false;

    } else {
        $("#error-maritalStatus").hide();
    }

    const workmenType = $("#workmenType").val();
    if (workmenType === "") {
        $("#error-workmenType").show();
        isValid = false;

    } else {
        $("#error-workmenType").hide();
    }

    const disability = $("#disability").val();
    if (disability === "") {
        $("#error-disability").show();
        isValid = false;

    } else {
        $("#error-disability").hide();
    }

    let pincodeCheckPassed = false;
    const address = $("#address").val().replace(/\s+/g, " ").trim();
    // Indian PIN code: 6 digits, first digit cannot be 0
    const pincodeRegex = /\b[1-9][0-9]{5}\b/;
    if (address === "") {
        $("#error-address").text("Address is required").show();
        isValid = false;
        pincodeCheckPassed = false;

    } else if (/[<>]/.test(address)) {
        $("#error-address").text("Invalid characters in address").show();
        isValid = false;
        pincodeCheckPassed = false;

    } else if (!pincodeRegex.test(address)) {
        $("#error-address").text("Address must contain the valid 6 digit pincode").show();
        isValid = false;
        pincodeCheckPassed = false;

    } else {
        const pincodeMatch = address.match(pincodeRegex);
        const pincode = pincodeMatch? pincodeMatch[0]: "";
        if (pincode === "") {
            $("#error-address").text("Address must contain the valid 6 digit pincode").show();
            isValid = false;
            pincodeCheckPassed = false;

        } else {
            /*
             * Check pincode exists in CMSPINCODEMASTER
             */
            $.ajax({
                url: "/CWFM/contractworkmen/checkPincodeExists",
                type: "GET",
                data: {
                    pincode: pincode
                },
                async: false,
                success: function(response) {
                    if (response &&
                        response.state !== null &&
                        response.state !== undefined &&
                        String(response.state).trim() !== "" &&
                        String(response.state).trim() !== "0") {
                        /*
                         * Pincode exists
                         */
                        $("#error-address").hide();
                        pincodeCheckPassed = true;
                    } else {
                        /*
                         * Pincode does not exist
                         */
                        $("#error-address").text("Address must contain the valid 6 digit pincode").show();
                        pincodeCheckPassed = false;
                        isValid = false;
                    }
                },
                error: function() {
                    /*
                     * Backend/database verification failed
                     */
                    $("#error-address").text("Address must contain the valid 6 digit pincode").show();
                    pincodeCheckPassed = false;
                    isValid = false;
                }
            });
        }
    }
    console.log("Basic data valid:", isValid);
    console.log("Aadhaar check passed:", aadharCheckPassed);
    console.log("Pincode check passed:", pincodeCheckPassed);

    return isValid && aadharCheckPassed && pincodeCheckPassed;
}

function validateEmploymentInformation(){
	let type = $("#gatePassType").val();
	let isValid = true;
    const principalEmp = $("#principalEmployer").val();
     if (principalEmp === "") {
        $("#error-principalEmployer").show();
        isValid = false;
    }else{
		$("#error-principalEmployer").hide();
	}
	const cont = $("#contractor").val();
     if (cont === "") {
        $("#error-contractor").show();
        isValid = false;
    }else{
		$("#error-contractor").hide();
	}
	const wo = $("#workorder").val();
     if (wo === "") {
        $("#error-workorder").show();
        isValid = false;
    }else{
		$("#error-workorder").hide();
	}
	const trade = $("#trade").val();
     if (trade === "") {
        $("#error-trade").show();
        isValid = false;
    }else{
		$("#error-trade").hide();
	}
	const skill = $("#skill").val();
     if (skill === "") {
        $("#error-skill").show();
        isValid = false;
    }else{
		$("#error-skill").hide();
	}
	/*const proficiency = $("#proficiency").val();
     if (proficiency === "") {
        $("#error-proficiency").show();
        isValid = false;
    }else{
		$("#error-proficiency").hide();
	}*/
	const dept = $("#department").val();
     if (dept === "") {
        $("#error-department").show();
        isValid = false;
    }else{
		$("#error-department").hide();
	}
	const subdept = $("#subdepartment").val();
     if (subdept === "") {
        $("#error-area").show();
        isValid = false;
    }else{
		$("#error-area").hide();
	}
	const eic = $("#eic").val();
     if (eic === "") {
        $("#error-eic").show();
        isValid = false;
    }else{
		$("#error-eic").hide();
	}
	/*if(type ==="regular" || type === "quick"){*/
	 /*const noj = $("#natureOfJob").val().trim();
	const nojRegex = /^(?=.*[A-Za-z]{2,})[A-Za-z\s]+$/;
    if (!nojRegex.test(noj)) {
        $("#error-natureOfJob").show();
        isValid = false;
    }else{
		 $("#error-natureOfJob").hide();
	}*/
	const wc = $("#wc").val();
     if (wc === "") {
        $("#error-wc").show();
        isValid = false;
    }else{
		$("#error-wc").hide();
	}
	//const ll = $("#ll").val();
    // if (ll === "") {
     //   $("#error-ll").show();
    //    isValid = false;
    //}else{
	//	$("#error-ll").hide();
	//}
	const ha = $("#hazardousArea").val();
     if (ha === "") {
        $("#error-hazardous").show();
        isValid = false;
    }else{
		$("#error-hazardous").hide();
	}
	/*const aa = $("#accessArea").val();
     if (aa === "") {
        $("#error-accessArea").show();
        isValid = false;
    }else{
		$("#error-accessArea").hide();
	}*/
	let uanCheckPassed = false;
	
	 const uan = $("#uanNumber").val().trim();
	 
	 const aadharNumber = $("#aadharNumber").val().trim();
	 const uanRegex = /^\d{12}$/;
   if (uan === "") {
        $("#error-uanNumber").hide();
        uanCheckPassed = true;
    }else {
    // ✅ Validate format
    if (!uanRegex.test(uan)) {
        $("#error-uanNumber").text("Please enter valid 12 digit UAN").show();
        isValid = false;
        uanCheckPassed = false;
    }
    // ✅ Allow 000000000000 without duplicate check
    else if (uan === "000000000000") {
        $("#error-uanNumber").hide();
        uanCheckPassed = true;
    }else{
	  // Check in backend if Aadhar exists
		         $.ajax({
		             url: "/CWFM/contractworkmen/checkUanExists",
		             type: "GET",
		             data: { uan: uan,aadharNumber : aadharNumber },
		             async: false, // NOTE: synchronous to block form submission
		             success: function (response) {
		                 if (response.exists) {
                             $("#error-uanNumber").text("UAN already exists with Aadhaar Number: " + response.otherAadhar).show();
							 uanCheckPassed = true;
                              isValid = false;
                           } else {
		                     $("#error-uanNumber").hide();
		                     uanCheckPassed = true;
		                 }
		             },
		             error: function () {
		                 $("#error-uanNumber").text("Error checking UAN").show();
		                 isValid = false;
		             }
		         });
		     }       
}
	const healthCheckDate = $("#healthCheckDate").val().trim();
    if (healthCheckDate === "") {
        $("#error-healthCheckDate").show();
        isValid = false;
    }else{
		 $("#error-healthCheckDate").hide();
	}
	let pfNumberCheckPassed = false;
	const pfNumber = $("#pfNumber").val().trim();
	const cleanedPf = pfNumber.trim().replace(/\s+/g, '').toLowerCase();
	if (cleanedPf === "") {
    $("#error-pfNumber").hide();
    pfNumberCheckPassed = true;
   }
	else if (cleanedPf === "newjoinee") {
	    // Special case → skip validation, do NOT show error
	    $("#error-pfNumber").hide();
	    pfNumberCheckPassed = true;
	}else {
	    const pfRegex = /^[A-Z]{2}[A-Z]{3}\d{7}\d{2}[A-Z0-9]\d{7}$/;
	    
		const cmpPfRegex = /^CMPPF[A-Z0-9]+$/i;

	    let isPfValid = false;

	    if (pfNumber.length === 22) {
	        if (pfRegex.test(pfNumber)) {
	            isPfValid = true;
	        } else {
	            $("#error-pfNumber").text("Invalid PF Number format. Expected 22-character structured PF number.").show();
	            isValid = false;
	        }
	    } else if (cmpPfRegex.test(pfNumber)) {
	        isPfValid = true;
	    } else {
	        $("#error-pfNumber").text("Invalid PF Number. It should either be 22 characters in PF format or start with CMPPF.").show();
	        isValid = false;
	    }

	    // Only proceed with AJAX call if PF format is valid
	    if (isPfValid) {
	        $.ajax({
	            url: "/CWFM/contractworkmen/checkpfNumberExists",
	            type: "GET",
	            data: { pfNumber: pfNumber, aadharNumber: aadharNumber },
	            async: false,
	            success: function (response) {
	                if (response.exists) {
	                    $("#error-pfNumber").text("PF Number already exists with Aadhaar Number: " + response.otherAadhar).show();
	                    isValid = false;
	                } else {
	                    $("#error-pfNumber").hide();
	                    pfNumberCheckPassed = true;
	                }
	            },
	            error: function () {
	                $("#error-pfNumber").text("Error checking PF Number").show();
	                isValid = false;
	            }
	        });
	    }
	}
    
	const selectedOption = $("#wc").find(":selected");
	//const licenceType = selectedOption.data("licencetype");
	const licenceType = selectedOption.attr("data-code");
	const esicNumber = $("#esicNumber").val().trim();

	if (licenceType === "ESIC") {
showEsic();
    if ( esicNumber === "") {
        $("#error-esicNumber").show();
        isValid = false;
    } else {
        $("#error-esicNumber").hide(); // ✅ hides properly
    }

} else {
    $("#error-esicNumber").hide(); // ✅ hides properly
}

if(type === "regular" || type=== "quick"){
	const doj = $("#doj").val().trim();
    if (doj === "") {
        $("#error-doj").show();
        isValid = false;
    }else{
		 $("#error-doj").hide();
	}
 }
	/*}*/
	return isValid;
}

function validateOtherInformation(){
	let isValid = true;
    const bg = $("#bloodGroup").val();
     if (bg === "") {
        $("#error-bloodGroup").show();
        isValid = false;
    }else{
		$("#error-bloodGroup").hide();
	}
	 const accom = $("#accommodation").val();
     if (accom === "") {
        $("#error-accommodation").show();
        isValid = false;
    }else{
		$("#error-accommodation").hide();
	}
	 const academic = $("#academic").val();
     if (academic === "") {
        $("#error-academic").show();
        isValid = false;
    }else{
		$("#error-academic").hide();
	}
	const tech = $("#technical").val();
     if (tech === "") {
        $("#error-technical").show();
        isValid = false;
    }else{
		$("#error-technical").hide();
	}
	const ifscInput = $("#ifscCode").val().trim();
    const ifscCodeRegex = /^[A-Z]{4}0[A-Z0-9]{6}$/;
    if (!(ifscCodeRegex.test(ifscInput) || ifscInput.toUpperCase() === "NEW JOINEE")) {
    $("#error-ifscCode").show();
    isValid = false;
      } else {
    $("#error-ifscCode").hide();
     }

	const accountInput = $("#accountNumber").val().trim();
    const accountNumberRegex = /^[0-9]{9,18}$/;
	if (!(accountNumberRegex.test(accountInput) || accountInput.toUpperCase() === "NEW JOINEE")) {
    $("#error-accountNumber").show();
    isValid = false;
    } else {
    $("#error-accountNumber").hide();
    }
	const emergencyName = $("#emergencyName").val().trim();
	const firstnameRegex = /^[A-Za-z\s]{2,}$/;
    if (!firstnameRegex.test(emergencyName)) {
        $("#error-emergencyName").show();
        isValid = false;
    }else{
		$("#error-emergencyName").hide();
	}
	const emergencyNoInput = $("#emergencyNumber").val().trim();
	const mobileNumberRegex = /^[6-9]\d{9}$/;
	if (!mobileNumberRegex.test(emergencyNoInput)) {
                 $("#error-emergencyNumber").show();
        			isValid = false;
     }else{
		 $("#error-emergencyNumber").hide();
	 }
	return isValid;
}

function validateWages(){
	let isValid = true;
    const wage = $("#wageCategory").val();
     if (wage === "") {
        $("#error-wageCategory").show();
        isValid = false;
    }else{
		$("#error-wageCategory").hide();
	}
	const bonus = $("#bonusPayout").val();
     if (bonus === "") {
        $("#error-bonusPayout").show();
        isValid = false;
    }else{
		$("#error-bonusPayout").hide();
	}
	const zone = $("#zone").val();
     if (zone === "") {
        $("#error-zone").show();
        isValid = false;
    }else{
		$("#error-zone").hide();
	}
	const allowanceRegex = /^[0-9]{1,6}(\.[0-9]{1,2})?$/;
	const basic = $("#basic").val().trim();
	const da = $("#da").val().trim();
	const hra = $("#hra").val().trim();
	const washing = $("#washingAllowance").val().trim();
	const other = $("#otherAllowance").val().trim();
	const uniform = $("#uniformAllowance").val().trim();
	if(basic === ""){
		//$("#basic").val("0.00"); 
		$("#error-basic").show();
		isValid = false;
	//not mandatory	
	}
	/*else 	if ( !allowanceRegex.test(basic) ) {
             $("#error-basic").show();
       		 isValid = false; 
			  
      }*/
      else{
		 $("#error-basic").hide();
	  }
	  if(da === ""){
		//$("#da").val("0.00"); 
		$("#error-da").show();
		isValid = false;
	  }/*else	  if ( !allowanceRegex.test(da) ) {
             $("#error-da").show();
       		 isValid = false;    
      }*/
      else{
		 $("#error-da").hide();
	  }
	  if(hra === ""){
		//$("#hra").val("0.00");
		  $("#error-hra").show();
		  isValid = false; 
	  }
	 /* else	  if ( !allowanceRegex.test(hra) ) {
             $("#error-hra").show();
       		 isValid = false;    
      }*/
      else{
		 $("#error-hra").hide();
	  }
	  if(washing === ""){
		//$("#washingAllowance").val("0.00"); 
		 $("#error-washingAllowance").show();
		 isValid = false;
	  }
	  /*else	  if ( !allowanceRegex.test(washing) ) {
             $("#error-washingAllowance").show();
       		 isValid = false;    
      }*/
      else{
		 $("#error-washingAllowance").hide();
	  }
	  if(other === ""){
		//$("#otherAllowance").val("0.00"); 
		 $("#error-otherAllowance").show();
		 isValid = false;
		  	  }
		/*  	  else	  if ( !allowanceRegex.test(other) ) {
             $("#error-otherAllowance").show();
       		 isValid = false;    
      }*/
      else{
		 $("#error-otherAllowance").hide();
	  }
	  if(uniform === ""){
		//$("#uniformAllowance").val("0.00"); 
		 $("#error-uniformAllowance").show();
		 isValid = false;
	  }
	/*  else	  if ( !allowanceRegex.test(uniform) ) {
             $("#error-uniformAllowance").show();
       		 isValid = false;    
      }*/
      else{
		 $("#error-uniformAllowance").hide();
	  }
	  
	return isValid;
}

function fileUpload(){
	let isValid = true;
        // Get the selected files
        var aadharFile = $("#aadharFile").prop("files")[0];
        var policeFile = $("#policeFile").prop("files")[0];
		var profilePic =$("imageFile").prop("files")[0];
		var appointmentFile = $("#appointmentFile").prop("files")[0];
        // Validate the files (optional)
        if (!validateFiles(aadharFile, policeFile,profilePic,appointmentFile)) {
            isValid=false; // Stop the upload if validation fails
        }

        // Create a FormData object
        var formData = new FormData();
        if (aadharFile) {
            formData.append("aadharFile", aadharFile);
        }
        if (policeFile) {
            formData.append("policeFile", policeFile);
        }
		if(profilePic){
			formData.append("profilePic",profilePic);
		}if(appointmentFile){
			formData.append("appointmentFile",appointmentFile);
		}

        // Submit the form data using AJAX
        $.ajax({
            url: "/CWFM/contractworkmen/uploadDocuments", // Your server-side upload handling URL
            type: "POST",
            data: formData,
            contentType: false, // Tell jQuery not to set contentType
            processData: false, // Tell jQuery not to process the data
            success: function(response) {
                // Display success message
                $("#uploadMessage").text("Documents uploaded successfully!").css("color", "green");
            },
            error: function(xhr, status, error) {
                // Display error message
                $("#uploadMessage").text("Error uploading documents: " + xhr.responseText).css("color", "red");
            }
        });
   return isValid;
}
function validateFiles(aadharFile, policeFile, profilePc,appointmentFile) {
    let valid = true;

    // Aadhar File - Mandatory & Size check
    if (!aadharFile) {
        $("#aadharError").text("Aadhar file is required").addClass("error-bold");
        valid = false;
    } else if (aadharFile.size > 5 * 1024 * 1024) {
        $("#aadharError").text("Aadhar file must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#aadharError").text("");
    }

    // Police Verification File - Mandatory & Size check
    if (!policeFile) {
        $("#policeError").text("Police verification file is required").addClass("error-bold");
        valid = false;
    } else if (policeFile.size > 5 * 1024 * 1024) {
        $("#policeError").text("Police file must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#policeError").text("");
    }
//appointment File - Mandatory & Size check
 if (!appointmentFile) {
        $("#appointmentError").text("Appointment Letter is required").addClass("error-bold");
        valid = false;
    } else if (appointmentFile.size > 5 * 1024 * 1024) {
        $("#appointmentError").text("Appointment file must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#appointmentError").text("");
    }
// Police Verification Date - Mandatory  check
const policeVerificationDate = $("#policeVerificationDate").val().trim();
    if (policeVerificationDate === "") {
        $("#error-policeVerificationDate").show();
        valid = false;
    }else {
        $("#error-policeVerificationDate").hide("");
    }
    
    // Profile Photo - Mandatory & Size check
    if (!profilePc) {
        $("#profilePcError").text("Profile photo is required").addClass("error-bold");
        valid = false;
    } else if (profilePc.size > 5 * 1024 * 1024) {
        $("#profilePcError").text("Photo/Image must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#profilePcError").text("");
    }

	const comments = $("#comments").val().trim();
		    if (comments === "") {
		        $("#error-comments").show();
		        valid = false;
		    }else{
				 $("#error-comments").hide();
			}
			// Accept Checkbox validation
			    if (!$("#acceptCheck").is(":checked")) {
			        $("#acceptError").show();
			        valid = false;
			    } else {
			        $("#acceptError").hide();
			    }
			    if (valid) {
    $("#docTabGlobalError").hide();
}		
    return valid;
}

    
  
	
	function redirectToWorkmenView() {
    var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
    if (selectedCheckboxes.length !== 1) {
        alert("Please select exactly one row to view.");
        return;
    }
    
    var selectedRow = selectedCheckboxes[0].closest('tr');
    var transactionId = selectedRow.querySelector('[name="selectedWOs"]').value;
    //var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
    var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed

         if (status.toLowerCase() == "draft") {
             alert("Status 'Draft' record can not View.");
            return;
           }
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            document.getElementById("mainContent").innerHTML = xhr.responseText;
             setDateRange();
        }
    };
    xhr.open("GET", "/CWFM/contractworkmen/view/" + transactionId, true);
    xhr.send();
}

 function preventEdit(event) {
        // Prevent further changes to the dropdown
        event.target.disabled = true;
    }
    
    function approveRejectGatePass(status,type){
		showLoader();
		let isValid=true;let gatePassType=1;
		if(type === "project"){
			gatePassType=12;
		}
		 const approvercomments = $("#approvercomments").val().trim();
    if (approvercomments === "" && status==5) {
        $("#error-approvercomments").show();
        alert("Comments Required in Documents");
        isValid = false;
        hideLoader();
    }else{
		$("#error-approvercomments").hide();
	}
	const Role= $("#roleName").val().trim();
	const onboardingDocType= $("#onboardingDocType").val();
	 if (onboardingDocType === "" && Role=='Security') {
        $("#error-onboardingDocType").show();
        alert("Onboarding Document Type is Required in Documents");
        isValid = false;
        hideLoader();
    }else{
		$("#error-onboardingDocType").hide();
	}
	//TRAINING DETAILS ARRAY
    let trainingDetailsList = [];

 if (Role === "Eic" || Role === "Safety") {

        $("#trainingBody tr").each(function () {

            const row = $(this);

            const trainingObj = {
                trainingType:row.find(".trainingType").val(),
                trainingName:row.find(".trainingName").val(),
                trainingFromDate:row.find(".trainingFromDate").val(),
                trainingToDate:row.find(".trainingToDate").val(),
                fromTime:row.find(".fromTime").val(),
                toTime:row.find(".toTime").val(),
                faculty:toTitleCase(row.find(".faculty").val()),
                marks:row.find(".marks").val(),
                efficency:row.find(".efficency").val(),
                nextTrainingDate:row.find(".nextTrainingDate").val(),
                remarks: toTitleCase(row.find(".remarks").val())
            };
             //OPTIONAL:SKIP EMPTY ROWS
            if (trainingObj.trainingType !== ""|| trainingObj.trainingName !== "") {
                trainingDetailsList.push(trainingObj);
            }
        });
    }



	if(isValid){
		const data = {
			approverId : $("#userId").val().trim(),
			comments : $("#approvercomments").val().trim(),
			status : status,
			transactionId : $("#transactionId").val().trim(),
			gatePassId : $("#gatePassId").val().trim(),
			approverRole : $("#roleName").val().trim(),
			roleId :$("#roleId").val().trim(),
			gatePassType : gatePassType,
			onboardingDocType:$("#onboardingDocType").val(),
			trainingDetailsList:trainingDetailsList
		};
			  const xhr = new XMLHttpRequest();
    xhr.open("POST", "/CWFM/contractworkmen/approveRejectGatePass", true); // Replace with your actual controller URL
    xhr.setRequestHeader("Content-Type", "application/json"); // Set content type for JSON
    xhr.onload = function() {
		hideLoader();
        if (xhr.status === 200) {
            // Handle successful response
            console.log("Data saved successfully:", xhr.responseText);
			sessionStorage.setItem("successMessage", "Gatepass approved/rejected successfully!");
            if(type=== "regular"){
                    loadCommonList('/contractworkmen/list', 'On-Boarding List');
                    //hideLoader();
                }else  if(type=== "quick"){
                    loadCommonList('/contractworkmen/quickOnboardingList', 'Quick Onboarding List');
                    //hideLoader();
                }else{
					loadCommonList('/contractworkmen/projectOnboardingList', 'Project Gatepass List');
					//hideLoader();
				}
        }			else if (xhr.status === 400) {  
							       const msg = xhr.responseText.trim();
							       console.error("Server validation failed: " + msg);
								   showLicenseError(msg);
								  // hideLoader();
							       //alertap(msg); // or show in UI better
							       //sessionStorage.setItem("errorMessage", msg);
								   return;
							   } 
		else {
            // Handle error response
            console.error("Error saving data:", xhr.statusText);
			sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass!");
			hideLoader();
        }
    };
    
    xhr.onerror = function() {
		//hideLoader();
        console.error("Request failed");
		sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass!");
		hideLoader();
    };
    
    // Send the data object as a JSON string
    xhr.send(JSON.stringify(data));
		}else{
			//error 
		}
		}//eofunc
		
		
		
		function trimIfPresent(val) {
		    return val ? val.trim() : val;
		}


    

	function submitGatePass(userId,type) {
		showLoader();
		// ✅ Clear all previous errors first
    $("#docTabGlobalError").hide().text("");
    $("label[id^='error-']").hide();
    
    let basicValid = true;
    let employmentValid = true;
    let otherValid = true;
    let wagesValid = true;
    let documentValid = true;
    let minimumwageValid = true;

    var aadharFile = $("#aadharFile").prop("files")[0];
    var policeFile = $("#policeFile").prop("files")[0];
	var profilePic = $("#imageFile").prop("files")[0];
	var appointmentFile = $("#appointmentFile").prop("files")[0];

    // Validate the files (optional)
    /*if (!validateFiles(aadharFile, policeFile,profilePic,appointmentFile)) {
        documentValid = false; // Stop the upload if validation fails
        hideLoader();
    }*/
	 // Check if already file exists in UI (from folder)
	var existingAadhar = $("#aadharFileName").text().trim();
	var existingPolice = $("#policeFileName").text().trim();
	var existingProfile = $("#imageFileName").text().trim();
	var existingAppointment = $("#appointmentFileName").text().trim();
    if (!validateBasicData()) {
        basicValid = false;
    }
	if(type=== "project"){
		if (!validateProjectEmploymentInformation()) {
		        employmentValid = false;
		         hideLoader();
		    }
		    if (!validateProjectFiles(aadharFile || existingAadhar,policeFile || existingPolice,profilePic || existingProfile,appointmentFile || existingAppointment)) {
             documentValid = false; // Stop the upload if validation fails
        hideLoader();
    }
	}else{
    if (!validateEmploymentInformation()) {
        employmentValid = false;
         hideLoader();
    }
	if (!validatePfForm11Requirement()) {
	        documentValid = false;
	         hideLoader();
	    }
	   

if (!validateFiles(aadharFile || existingAadhar,policeFile || existingPolice,profilePic || existingProfile,appointmentFile || existingAppointment)) {
    documentValid = false;
    hideLoader();
}
	    /*if (!validateFiles(aadharFile, policeFile,profilePic,appointmentFile)) {
        documentValid = false; // Stop the upload if validation fails
        hideLoader();
    }*/
	}
	if(type=== "regular"){
        if (!validateOtherInformation()) {
            otherValid = false;
             hideLoader();
        }

        if (!validateWages()) {
            wagesValid = false;
             hideLoader();
        }
        if(!validateMinimumWage()){
			minimumwageValid = false;
             hideLoader();
		}
    }else{
		otherValid = true;
		wagesValid = true;
		minimumwageValid=true;
		$("#uniformAllowance").val("0.00"); 
		$("#washingAllowance").val("0.00");  	
		$("#hra").val("0.00");  	
		$("#da").val("0.00"); 
		$("#basic").val("0.00"); 
		$("#otherAllowance").val("0.00"); 
	}
  
    console.log("basicValid: " + basicValid);
    console.log("employmentValid: " + employmentValid);
    console.log("otherValid: " + otherValid);
    console.log("wagesValid: " + wagesValid);
    console.log("documentValid: " + documentValid);

//TAB NAME ERROR MESSAGE LOGIC (YOUR REQUIREMENT)
    let errorTabs = [];

    if (!basicValid) errorTabs.push("Basic");
    if (!employmentValid) errorTabs.push("Employment");
    if (type === "regular" && !otherValid) errorTabs.push("Other");
    if (type === "regular" && !wagesValid) errorTabs.push("Wages");

    // If any tab has errors → show message in Documents tab
    if (errorTabs.length > 0) {

        let msg = "Please check errors in: " + errorTabs.join(", ") + " tab(s).";

        $("#docTabGlobalError")
            .text(msg)
            .show();

        // Optional: Jump to first error tab
        /*
        let firstTab = errorTabs[0];
        if (firstTab === "Basic") $("#basicTab").click();
        else if (firstTab === "Employment") $("#employmentTab").click();
        else if (firstTab === "Other") $("#otherTab").click();
        else if (firstTab === "Wages") $("#wagesTab").click();
        */

        hideLoader();
        return;
    }
    // ✅ Utility function for Capital Case
    function toCapitalCase(str) {
        return str
            .toLowerCase()
            .split(' ')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(' ');
    }

    // ✅ Capital case transformation
    const firstName = toCapitalCase($("#firstName").val().trim());
    const lastName = toCapitalCase($("#lastName").val().trim());
    const relationName = toCapitalCase($("#relationName").val().trim());
    const address = toCapitalCase($("#address").val().trim());
    const idMark = $("#idMark").val();
	let natureOfJob="";let emergencyName="";let pfApplicable="No";
	if(type=== "regular" || type==="quick"){
		 natureOfJob = toCapitalCase($("#natureOfJob").val().trim());
		    emergencyName = toCapitalCase($("#emergencyName").val().trim());
		    pfApplicable = $("#pfApplicable").is(":checked") ? "Yes" : "No";
		}
    if (basicValid && employmentValid && otherValid && wagesValid && minimumwageValid && documentValid) {
        const data = new FormData();
        const jsonData = {
			transactionId:trimIfPresent($("#transactionId").val()),
            aadhaarNumber: trimIfPresent($("#aadharNumber").val()),
            firstName: firstName,
            lastName: lastName,
            dateOfBirth:trimIfPresent( $("#dateOfBirth").val()),
            gender: $("#gender").val(),
            relationName: relationName,
            idMark: idMark,
            mobileNumber: trimIfPresent($("#mobileNumber").val()),
            maritalStatus: $("#maritalStatus").val(),
            principalEmployer: $("#principalEmployer").val(),
            contractor: $("#contractor").val(),
            workorder: $("#workorder").val(),
            trade: $("#trade").val(),
            skill: $("#skill").val(),
            department: $("#department").val(),
            subdepartment: $("#subdepartment").val(),
            eic: $("#eic").val(),
            natureOfJob: natureOfJob,
            wcEsicNo: $("#wc").val(),
			llNo:$("#ll").val(),
            hazardousArea: $("#hazardousArea").val(),
            accessArea: $("#accessArea").val(),
            uanNumber: trimIfPresent($("#uanNumber").val()),
            healthCheckDate: trimIfPresent($("#healthCheckDate").val()),
            pfNumber:trimIfPresent($("#pfNumber").val()),
			esicNumber:trimIfPresent($("#esicNumber").val()),
            bloodGroup: $("#bloodGroup").val(),
            accommodation: $("#accommodation").val(),
            academic: $("#academic").val(),
            technical: $("#technical").val(),
            ifscCode: trimIfPresent($("#ifscCode").val()),
            accountNumber: trimIfPresent($("#accountNumber").val()),
            emergencyName: emergencyName,
            emergencyNumber: trimIfPresent($("#emergencyNumber").val()),
            wageCategory: $("#wageCategory").val(),
            bonusPayout: $("#bonusPayout").val(),
            pfCap: $("#pfCap").val(),
            zone: $("#zone").val(),
            basic: trimIfPresent($("#basic").val()),
            da: trimIfPresent($("#da").val()),
            hra: trimIfPresent($("#hra").val()),
            washingAllowance: trimIfPresent($("#washingAllowance").val()),
            otherAllowance:trimIfPresent( $("#otherAllowance").val()),
            uniformAllowance: trimIfPresent($("#uniformAllowance").val()),
            userId: userId,
            gatePassAction: "save",
            comments: trimIfPresent($("#comments").val()),
			address: address,
			doj:$("#doj").val(),
			pfApplicable:pfApplicable,
            policeVerificationDate: trimIfPresent($("#policeVerificationDate").val()),
            disability:$("#disability").val(),
            workmenType:$("#workmenType").val(),
            proficiency: $("#proficiency").val(),
             unitId: $("#principalEmployer").val(),
			onboardingType:type,
        };

        const jsonString = JSON.stringify(jsonData);
		data.append("jsonData", jsonString);

        if (aadharFile) data.append("aadharFile", aadharFile);
        var existingProfile = $("#imageFileName").text().trim();

if (profilePic) {
    data.append("profilePic", profilePic);
} else if (existingProfile) {
    data.append("existingProfile", existingProfile); // ✅ send old name
}
        if (policeFile) data.append("policeFile", policeFile);
		//if(profilePic) data.append("profilePic",profilePic);
       if (appointmentFile) {
    data.append("appointmentFile", appointmentFile);
} else {
    data.append("appointmentFile", new Blob([], { type: "application/octet-stream" }), "empty.txt");
}


    	const additionalFields = document.querySelectorAll('.document-field');
        additionalFields.forEach((field) => {
            const docType = field.querySelector('select[name="documentType"]').value;
            const fileInput = field.querySelector('input[type="file"]');
            if (docType && fileInput.files[0]) {
                data.append('additionalFiles', fileInput.files[0]);
                data.append('documentTypes', docType);
            }
        });


let remainingDocTypes = [];

additionalFields.forEach((field) => {

    const docType = field.querySelector('select[name="documentType"]').value;
    const fileInput = field.querySelector('input[type="file"]');

    if (docType && docType.trim() !== "") {

        // ✅ Track ALL docTypes (even without file)
        remainingDocTypes.push(docType.trim().toLowerCase());

        // ✅ Only send file if newly selected
        if (fileInput.files[0]) {
            data.append('additionalFiles', fileInput.files[0]);
            data.append('documentTypes', docType);
        }
    }
});

// ✅ VERY IMPORTANT (missing in your code)
data.append("remainingDocTypes", JSON.stringify(remainingDocTypes));

        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/CWFM/contractworkmen/saveGatePass", true);

        xhr.onload = function () {
			hideLoader();
            if (xhr.status === 200) {
                console.log("Data saved successfully:", xhr.responseText);
				sessionStorage.setItem("successMessage", "Gatepass saved successfully!");
                if(type=== "regular"){
                    loadCommonList('/contractworkmen/list', 'On-Boarding List');
                    //hideLoader();
                }else if(type=== "quick"){
                    loadCommonList('/contractworkmen/quickOnboardingList', 'Quick Onboarding List');
                   // hideLoader();
                }else{
					loadCommonList('/contractworkmen/projectOnboardingList', 'Project Gatepass List');
					//hideLoader();
				}
            }else if (xhr.status === 400) {  
				       const msg = xhr.responseText.trim();
				       console.error("Server validation failed: " + msg);
					   showLicenseError(msg);
				       //alert(msg); // or show in UI better
				       //sessionStorage.setItem("errorMessage", msg);
					   return;
				   }
				   else {
				       console.error("Error saving data:", xhr.status, xhr.responseText);
				       sessionStorage.setItem("errorMessage", "Failed to save Gatepass!");
				   }
        };

        xhr.onerror = function () {
			//hideLoader();
            console.error("Request failed");
			sessionStorage.setItem("errorMessage", "Failed to save Gatepass!");
			 hideLoader();
        };

        xhr.send(data);
    } else {
        console.error("Validation failed for one or more fields.");
    }
}

function showLicenseError(msg) {
    const div = document.getElementById("licenseError");
    div.innerHTML = msg;
    div.style.display = "block";
}
function viewDoc(transactionId, userId, docType) {
    // Prepare data for secure encoding
    const data = { transactionId, userId, docType };

    // Encode as Base64 JSON (URL-safe)
    const encodedData = btoa(JSON.stringify(data));

    // Build masked endpoint URL
    const url = `/CWFM/contractworkmen/viewFile/${encodedData}`;

    // Fetch the file and open in a new tab
    fetch(url)
        .then(response => {
            if (!response.ok) throw new Error("File not found or server error");
            return response.blob();
        })
        .then(blob => {
            const blobUrl = window.URL.createObjectURL(blob);
            window.open(blobUrl, '_blank'); // ✅ Opens inline in a new tab
            setTimeout(() => URL.revokeObjectURL(blobUrl), 5000);
        })
        .catch(err => {
            console.error("Error opening file:", err);
            alert("Unable to open file.");
        });
}

function additionalDocUpload(selectedType = "", existingFileName = "") {

    const currentCount = $(".document-field").length;
    if (currentCount >= 7) {
        alert("Max 7 documents allowed");
        return;
    }

    divIndex++;

    const docOptions = [
        "Bank", "Id2", "Other", "Medical",
        "Education", "Training", "Form11"
    ];

    // ✅ GET ALREADY SELECTED VALUES
    const selectedValues = $(".document-field select").map(function () {
        return $(this).val();
    }).get();

    let optionsHtml = '<option value="">Select Document Type</option>';

    docOptions.forEach(opt => {
        // ✅ ALLOW if not selected OR same as current selectedType (for edit)
        if (!selectedValues.includes(opt) || opt === selectedType) {
            optionsHtml += `<option value="${opt}" ${opt === selectedType ? 'selected' : ''}>${opt}</option>`;
        }
    });

    const fileNameId = 'fileName-' + divIndex;

    const html = `
        <div class="document-field" id="document-field-${divIndex}" 
             style="margin-bottom:10px; display:flex; align-items:center;">

            <select name="documentType" 
                    onchange="updateDocTypeDropdowns()" 
                    style="margin-right: 10px;color:black;">
                ${optionsHtml}
            </select>

            <input type="file"
                   accept="application/pdf,image/jpeg,image/png"
                   onchange="displayFileName(this,'${fileNameId}')"
                   style="margin-right:10px;">

            <!-- ✅ EXISTING FILE NAME -->
            <span id="${fileNameId}" style="color:black;">
                ${existingFileName || ""}
            </span>

            <button type="button" onclick="removeDocument(${divIndex})" style="color:black;">
                Remove
            </button>
        </div>
    `;

    $("#additionalDoc").append(html);

    // ✅ VERY IMPORTANT
    updateDocTypeDropdowns();
}
function removeDocument(index) {
    $(`#document-field-${index}`).remove();
    updateDocTypeDropdowns(); // Update dropdowns after removal
}

function displayFileName(inputElement, displayId) {
    const displayElement = document.getElementById(displayId);
    if (inputElement.files.length > 0) {
        displayElement.textContent = inputElement.files[0].name;
    } else {
        displayElement.textContent = '';
    }
}

function updateDocTypeDropdowns() {
    const allOptions = [
        "Bank", "Id2", "Other", "Medical",
        "Education", "Training", "Form11"
    ];

    const selectedValues = $(".document-field select").map(function () {
        return $(this).val();
    }).get();

    $(".document-field select").each(function () {
        const currentSelect = $(this);
        const currentValue = currentSelect.val();

        currentSelect.empty().append('<option value="">Select Document Type</option>');

        allOptions.forEach(function (opt) {
            const isDisabled = selectedValues.includes(opt) && opt !== currentValue;
            const optionTag = $('<option>', {
                value: opt,
                text: opt,
                disabled: isDisabled,
                selected: opt === currentValue
            });
            currentSelect.append(optionTag);
        });
    });
}


function displayFileName1(inputId, displayId) {
    const fileInput = document.getElementById(inputId);
    const displayElement = document.getElementById(displayId);

    // Get the selected file's name
    if (fileInput.files.length > 0) {
        const fileName = fileInput.files[0].name;
        displayElement.textContent = fileName; // Display the file name
    } else {
        displayElement.textContent = ''; // Clear the display if no file is selected
    }
}

	function submitCancel(userId, gatePassType) {
showLoader();
    let isValid = true;

    const comments = $("#comments").val().trim();
    const reason = $("#reasonofOffboarding").val();

    // Validate comments
    if (comments === "") {
        $("#error-comments").show();
        isValid = false;
    } else {
        $("#error-comments").hide();
    }

    // Validate reasoning
    if (reason === "") {
        $("#error-reasonofOffboarding").show();
        alert("Reason of Offboarding Required in Reasoning Tab");
        hideLoader();
        isValid = false;
    } else {
        $("#error-reasonofOffboarding").hide();
    }

    if (!isValid) return;

    // Files
    var exitFile = $("#exitFile")[0].files[0];
    var fnfFile = $("#FNFFile")[0].files[0];
    var feedbackFile = $("#feedbackFormFile")[0].files[0];
    var rateManagerFile = $("#rateManagerFile")[0].files[0];
    var locFile = $("#LOCFile")[0].files[0];

    const formData = new FormData();

    // JSON Data
    const jsonData = {
        createdBy: userId,
        comments: comments,
        transactionId: $("#transactionId").val().trim(),
        gatePassId: $("#gatePassId").val().trim(),
        gatePassType: gatePassType,
        reasoning: reason
    };

    formData.append("jsonData", JSON.stringify(jsonData));

    if (exitFile) formData.append("exitFile", exitFile);
    if (fnfFile) formData.append("fnfFile", fnfFile);
    if (feedbackFile) formData.append("feedbackFile", feedbackFile);
    if (rateManagerFile) formData.append("rateManagerFile", rateManagerFile);
    if (locFile) formData.append("locFile", locFile);

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/CWFM/contractworkmen/cancelGatePassAction", true);

    // ❌ DO NOT SET Content-Type manually for FormData
    // xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onload = function () {
		hideLoader();
        if (xhr.status === 200) {
            console.log("Cancel Saved:", xhr.responseText);
            sessionStorage.setItem("successMessage", "Gatepass cancel request raised successfully!");
            loadCommonList('/contractworkmen/cancelFilter', 'Cancel List');
            hideLoader();
        } else {
            console.error("Error:", xhr.responseText);
            sessionStorage.setItem("errorMessage", "Failed to raise Gatepass cancel request!");
            hideLoader();
        }
    };

    xhr.onerror = function () {
        console.error("Request failed");
        sessionStorage.setItem("errorMessage", "Request failed!");
        hideLoader();
    };

    xhr.send(formData);
}

function approveRejectCancel(status,gatePassType){
	showLoader();
	let isValid=true;
	 const approvercomments = $("#approvercomments").val().trim();
   if (approvercomments === "" && status==5) {
       $("#error-approvercomments").show();
       alert("Comments Required in Documents");
       isValid = false;
       hideLoader();
   }else{
	$("#error-approvercomments").hide();
}
if(isValid){
	const data = {
		approverId : $("#userId").val().trim(),
		comments : $("#approvercomments").val().trim(),
		status : status,
		transactionId : $("#transactionId").val().trim(),
		gatePassId : $("#gatePassId").val().trim(),
		approverRole : $("#roleName").val().trim(),
		roleId :$("#roleId").val().trim(),
		gatePassType : gatePassType,
	};
		  const xhr = new XMLHttpRequest();
   xhr.open("POST", "/CWFM/contractworkmen/approveRejectGatePass", true); // Replace with your actual controller URL
   xhr.setRequestHeader("Content-Type", "application/json"); // Set content type for JSON
   xhr.onload = function() {
	   hideLoader();
       if (xhr.status === 200) {
           // Handle successful response
           console.log("Data saved successfully:", xhr.responseText);
		   sessionStorage.setItem("successMessage", "Gatepass cancel request approved/rejected successfully!");
         loadCommonList('/contractworkmen/cancelFilter', 'Cancel List');
         hideLoader();
       } else {
           // Handle error response
           console.error("Error saving data:", xhr.statusText);
		   sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass cancel request!");
		   hideLoader();
       }
   };
   
   xhr.onerror = function() {
	  // hideLoader();
       console.error("Request failed");
	   sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass cancel request!");
	    hideLoader();
   };
   
   // Send the data object as a JSON string
   xhr.send(JSON.stringify(data));
	}else{
		//error 
	}
	}//eofunc
	
		function submitBlock(userId, gatePassType) {
showLoader();
    let isValid = true;

    const comments = $("#comments").val().trim();
    const reason = $("#reasonofOffboarding").val();

    if (comments === "") {
        $("#error-comments").show();
        isValid = false;
    } else {
        $("#error-comments").hide();
    }

    if (reason === "") {
        $("#error-reasonofOffboarding").show();
        alert("Reason of Offboarding Required in Reasoning Tab");
        hideLoader();
        isValid = false;
    } else {
        $("#error-reasonofOffboarding").hide();
    }

    if (!isValid) return;

    // Files
    var attachmentOfReference = $("#attachmentOfReference")[0].files[0];

    const formData = new FormData();

    // JSON Data
    const jsonData = {
        createdBy: userId,
        comments: comments,
        transactionId: $("#transactionId").val().trim(),
        gatePassId: $("#gatePassId").val().trim(),
        gatePassType: gatePassType,
        reasoning: reason
    };

    formData.append("jsonData", JSON.stringify(jsonData));

    if (attachmentOfReference) formData.append("attachmentOfReference", attachmentOfReference);

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/CWFM/contractworkmen/gatePassAction", true);

    // ❌ DO NOT SET content-type manually
    // xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onload = function () {
		hideLoader();
        if (xhr.status === 200) {
            console.log("Saved:", xhr.responseText);
            sessionStorage.setItem("successMessage", "Gatepass block request raised successfully!");
            loadCommonList('/contractworkmen/blockListFilter', 'Block List');
            hideLoader();
        } else {
            console.error("Error:", xhr.responseText);
            sessionStorage.setItem("errorMessage", "Failed to raise Gatepass block request!");
            hideLoader();
        }
    };

    xhr.onerror = function () {
        console.error("Request failed");
        sessionStorage.setItem("errorMessage", "Request failed!");
        hideLoader();
    };

    xhr.send(formData);
}

	function approveRejectBlock(status,gatePassType){
		showLoader();
		let isValid=true;
		 const approvercomments = $("#approvercomments").val().trim();
	   if (approvercomments === "" && status==5 ) {
	       $("#error-approvercomments").show();
	       alert("Comments Required in Documents");
	       isValid = false;
	       hideLoader();
	   }else{
		$("#error-approvercomments").hide();
	}
	if(isValid){
		const data = {
			approverId : $("#userId").val().trim(),
			comments : $("#approvercomments").val().trim(),
			status : status,
			transactionId : $("#transactionId").val().trim(),
			gatePassId : $("#gatePassId").val().trim(),
			approverRole : $("#roleName").val().trim(),
			roleId :$("#roleId").val().trim(),
			gatePassType : gatePassType,
		};
			  const xhr = new XMLHttpRequest();
	   xhr.open("POST", "/CWFM/contractworkmen/approveRejectGatePass", true); // Replace with your actual controller URL
	   xhr.setRequestHeader("Content-Type", "application/json"); // Set content type for JSON
	   xhr.onload = function() {
		   hideLoader();
	       if (xhr.status === 200) {
	           // Handle successful response
	           console.log("Data saved successfully:", xhr.responseText);
			   sessionStorage.setItem("successMessage", "Gatepass block request approved/rejected successfully!");
	         loadCommonList('/contractworkmen/blockListFilter', 'Block List');
	         hideLoader();
	       } else {
	           // Handle error response
	           console.error("Error saving data:", xhr.statusText);
			   sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass block request!");
			   hideLoader();
	       }
	   };
	   
	   xhr.onerror = function() {
	       console.error("Request failed");
	       hideLoader();
	   };
	   
	   // Send the data object as a JSON string
	   xhr.send(JSON.stringify(data));
		}else{
			//error 
		}
		}//eofunc
		
				function submitUnblock(userId, gatePassType) {
showLoader();
    let isValid = true;

    const comments = $("#comments").val().trim();
    const reason = $("#reasonofOffboarding").val();

    // Validate comments
    if (comments === "") {
        $("#error-comments").show();
        isValid = false;
    } else {
        $("#error-comments").hide();
    }

    // Validate reason
    if (reason === "") {
        $("#error-reasonofOffboarding").show();
        alert("Reason of Offboarding Required in Reasoning Tab");
        hideLoader();
        isValid = false;
    } else {
        $("#error-reasonofOffboarding").hide();
    }

    if (!isValid) return;
    
     // 👇 Determine which type to check
    let checkTypeId = null;

    if (gatePassType == 5) {
        checkTypeId = 4;   // check block if doing unblock
    }
   const gatePassId = $("#gatePassId").val().trim();
console.log("gatePassId =", gatePassId);
console.log("checkTypeId =", checkTypeId);
    // 👇 Call validation first
    checkSameDayValidation(gatePassId, checkTypeId, function (isAllowed) {

        if (!isAllowed) {
        hideLoader();
        return;
    }


    // Files
     var attachmentOfReference = $("#attachmentOfReference")[0].files[0];

    const formData = new FormData();

    // JSON Payload
    const jsonData = {
        createdBy: userId,
        comments: comments,
        transactionId: $("#transactionId").val().trim(),
        gatePassId: gatePassId,
        gatePassType: gatePassType,
        reasoning: reason
    };

    formData.append("jsonData", JSON.stringify(jsonData));

    if (attachmentOfReference) formData.append("attachmentOfReference", attachmentOfReference);

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/CWFM/contractworkmen/gatePassAction", true);

    // ❌ DO NOT set Content-Type manually for FormData

    xhr.onload = function () {
		hideLoader();
        if (xhr.status === 200) {
            console.log("Unblock saved:", xhr.responseText);
            sessionStorage.setItem("successMessage", "Gatepass unblock request raised successfully!");
            loadCommonList('/contractworkmen/unblockListFilter', 'Unblock List');
            hideLoader();
        } else {
            console.error("Error:", xhr.responseText);
            sessionStorage.setItem("errorMessage", "Failed to raise Gatepass unblock request!");
            hideLoader();
        }
    };

    xhr.onerror = function () {
        console.error("Request failed");
        sessionStorage.setItem("errorMessage", "Request failed!");
        hideLoader();
    };

    xhr.send(formData);
    });
}

		function approveRejectUnblock(status,gatePassType){
			showLoader();
			let isValid=true;
			 const approvercomments = $("#approvercomments").val().trim();
		   if (approvercomments === "" && status==5) {
		       $("#error-approvercomments").show();
		       alert("Comments Required in Documents");
		       isValid = false;
		       hideLoader();
		   }else{
			$("#error-approvercomments").hide();
		}
		if(isValid){
			const data = {
				approverId : $("#userId").val().trim(),
				comments : $("#approvercomments").val().trim(),
				status : status,
				transactionId : $("#transactionId").val().trim(),
				gatePassId : $("#gatePassId").val().trim(),
				approverRole : $("#roleName").val().trim(),
				roleId :$("#roleId").val().trim(),
				gatePassType : gatePassType,
			};
				  const xhr = new XMLHttpRequest();
		   xhr.open("POST", "/CWFM/contractworkmen/approveRejectGatePass", true); // Replace with your actual controller URL
		   xhr.setRequestHeader("Content-Type", "application/json"); // Set content type for JSON
		   xhr.onload = function() {
			   hideLoader();
		       if (xhr.status === 200) {
		           // Handle successful response
		           console.log("Data saved successfully:", xhr.responseText);
				   sessionStorage.setItem("successMessage", "Gatepass unblock approved/rejected successfully!");
		         loadCommonList('/contractworkmen/unblockListFilter', 'Unblock List');
		         //hideLoader();
		       } else {
		           // Handle error response
		           console.error("Error saving data:", xhr.statusText);
				   sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass unblock request!");
				   //hideLoader();
		       }
		   };
		   
		   xhr.onerror = function() {
			  // hideLoader();
		       console.error("Request failed");
			   sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass unblock request!");
			   hideLoader();
		   };
		   
		   // Send the data object as a JSON string
		   xhr.send(JSON.stringify(data));
			}else{
				//error 
			}
			}//eofunc
			
	function submitBlack(userId, gatePassType) {
showLoader();
    let isValid = true;

    const comments = $("#comments").val().trim();
    const reason = $("#reasonofOffboarding").val();

    // Validation
    if (comments === "") {
        $("#error-comments").show();
        isValid = false;
    } else {
        $("#error-comments").hide();
    }

    if (reason === "") {
        $("#error-reasonofOffboarding").show();
        alert("Reason of Offboarding Required in Reasoning Tab");
        hideLoader();
        isValid = false;
    } else {
        $("#error-reasonofOffboarding").hide();
    }

    if (!isValid) return;

    // Files
    var attachmentOfReference = $("#attachmentOfReference")[0].files[0];

    const formData = new FormData();

    // JSON Data
    const jsonData = {
        createdBy: userId,
        comments: comments,
        transactionId: $("#transactionId").val().trim(),
        gatePassId: $("#gatePassId").val().trim(),
        gatePassType: gatePassType,
        reasoning: reason
    };

    formData.append("jsonData", JSON.stringify(jsonData));

    if (attachmentOfReference) formData.append("attachmentOfReference", attachmentOfReference);

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/CWFM/contractworkmen/gatePassAction", true);

    // ❌ DO NOT SET CONTENT TYPE manually
    // xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onload = function () {
		hideLoader();
        if (xhr.status === 200) {
            console.log("Saved:", xhr.responseText);
            sessionStorage.setItem("successMessage", "Gatepass blacklist request raised successfully!");
            loadCommonList('/contractworkmen/blackListFilter', 'Black List');
            hideLoader();
        } else {
            console.error("Error:", xhr.responseText);
            sessionStorage.setItem("errorMessage", "Failed to raise Gatepass blacklist request!");
            hideLoader();
        }
    };

    xhr.onerror = function () {
        console.error("Request failed");
        sessionStorage.setItem("errorMessage", "Request failed!");
        hideLoader();
    };

    xhr.send(formData);
}

			function approveRejectBlack(status,gatePassType){
				showLoader();
let isValid=true;
 const approvercomments = $("#approvercomments").val().trim();
					   if (approvercomments === "" && status==5 ) {
					       $("#error-approvercomments").show();
					       alert("Comments Required in Documents");
					       isValid = false;
					       hideLoader();
					   }else{
$("#error-approvercomments").hide();
					}
					if(isValid){
const data = {
	approverId : $("#userId").val().trim(),
	comments : $("#approvercomments").val().trim(),
	status : status,
	transactionId : $("#transactionId").val().trim(),
	gatePassId : $("#gatePassId").val().trim(),
	approverRole : $("#roleName").val().trim(),
	roleId:$("#roleId").val().trim(),
	gatePassType : gatePassType,
};
	  const xhr = new XMLHttpRequest();
					   xhr.open("POST", "/CWFM/contractworkmen/approveRejectGatePass", true); // Replace with your actual controller URL
					   xhr.setRequestHeader("Content-Type", "application/json"); // Set content type for JSON
					   xhr.onload = function() {
						   hideLoader();
					       if (xhr.status === 200) {
					           // Handle successful response
					           console.log("Data saved successfully:", xhr.responseText);
							   sessionStorage.setItem("successMessage", "Gatepass blacklist request approved/rejected successfully!");
					         loadCommonList('/contractworkmen/blackListFilter', 'Black List');
					         //hideLoader();
					       } else {
					           // Handle error response
					           console.error("Error saving data:", xhr.statusText);
					           hideLoader();
					       }
					   };
					   
					   xhr.onerror = function() {
						   //hideLoader();
					       console.error("Request failed");
						   sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass blacklist request!");
						   hideLoader();
					   };
					   
					   // Send the data object as a JSON string
					   xhr.send(JSON.stringify(data));
					   sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass blacklist request!");
					   //hideLoader();
}else{
	//error 
}
}//eofunc

				function submitDeblack(userId, gatePassType) {
showLoader();
    let isValid = true;

    const comments = $("#comments").val().trim();
    const reason = $("#reasonofOffboarding").val();

    // Validate comments
    if (comments === "") {
        $("#error-comments").show();
        isValid = false;
    } else {
        $("#error-comments").hide();
    }

    // Validate reasoning
    if (reason === "") {
        $("#error-reasonofOffboarding").show();
        alert("Reason of Offboarding Required in Reasoning Tab");
        hideLoader();
        isValid = false;
    } else {
        $("#error-reasonofOffboarding").hide();
    }

    if (!isValid) return;
    
    // 👇 Determine which type to check
    let checkTypeId = null;

    if (gatePassType == 7) {
        checkTypeId = 6;   // check block if doing unblock
    }
    
     const gatePassId = $("#gatePassId").val().trim();

console.log("gatePassId =", gatePassId);
console.log("checkTypeId =", checkTypeId);
    // 👇 Call validation first
    checkSameDayValidation(gatePassId, checkTypeId, function (isAllowed) {

        if (!isAllowed) {
        hideLoader();
        return;
    }


    // Files
    var attachmentOfReference = $("#attachmentOfReference")[0].files[0];

    const formData = new FormData();

    // JSON Payload
    const jsonData = {
        createdBy: userId,
        comments: comments,
        transactionId: $("#transactionId").val().trim(),
        gatePassId: gatePassId,
        gatePassType: gatePassType,
        reasoning: reason
    };

    formData.append("jsonData", JSON.stringify(jsonData));

    if (attachmentOfReference) formData.append("attachmentOfReference", attachmentOfReference);

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/CWFM/contractworkmen/gatePassAction", true);

    // ❌ DO NOT SET Content-Type manually for FormData
    // xhr.setRequestHeader("Content-Type", "application/json");

    xhr.onload = function () {
		hideLoader();
        if (xhr.status === 200) {
            console.log("Deblack saved:", xhr.responseText);
            sessionStorage.setItem("successMessage", "Gatepass deblacklist request raised successfully!");
            loadCommonList('/contractworkmen/deblackListFilter', 'Deblack List');
            hideLoader();
        } else {
            console.error("Error:", xhr.responseText);
            sessionStorage.setItem("errorMessage", "Failed to raise Gatepass deblacklist request!");
            hideLoader();
        }
    };

    xhr.onerror = function () {
        console.error("Request failed");
        sessionStorage.setItem("errorMessage", "Request failed!");
        hideLoader();
    };

    xhr.send(formData);
     });
}

function approveRejectDeblacklist(status,gatePassType){
	showLoader();
			let isValid=true;
			 const approvercomments = $("#approvercomments").val().trim();
		if (approvercomments === "" && status==5) {
		    $("#error-approvercomments").show();
		    alert("Comments Required in Documents");
		    isValid = false;
		    hideLoader();
		}else{
			$("#error-approvercomments").hide();
		}
		if(isValid){
			const data = {
				approverId : $("#userId").val().trim(),
				comments : $("#approvercomments").val().trim(),
				status : status,
				transactionId : $("#transactionId").val().trim(),
				gatePassId : $("#gatePassId").val().trim(),
				approverRole : $("#roleName").val().trim(),
				roleId :$("#roleId").val().trim(),
				gatePassType : gatePassType,
			};
				  const xhr = new XMLHttpRequest();
		xhr.open("POST", "/CWFM/contractworkmen/approveRejectGatePass", true); // Replace with your actual controller URL
		xhr.setRequestHeader("Content-Type", "application/json"); // Set content type for JSON
		xhr.onload = function() {
			hideLoader();
		    if (xhr.status === 200) {
		        // Handle successful response
		        console.log("Data saved successfully:", xhr.responseText);
				sessionStorage.setItem("successMessage", "Gatepass deblacklist request approved/rejected successfully!");
		      loadCommonList('/contractworkmen/deblackListFilter', 'Deblack List');
		      //hideLoader();
		    } else {
		        // Handle error response
		        console.error("Error saving data:", xhr.statusText);
				sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass deblacklist request!");
				//hideLoader();
		    }
		};
		
		xhr.onerror = function() {
			//hideLoader();
		    console.error("Request failed");
			sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass deblacklist request!");
			 hideLoader();
		};
		
		// Send the data object as a JSON string
		xhr.send(JSON.stringify(data));
			}else{
				//error 
			}
			}//eofunc
			function submitLostOrDamage(userId,gatePassType){
				showLoader();
let isValid=true;
 const comments = $("#comments").val().trim();
if (comments === "") {
    $("#error-comments").show();
    isValid = false;
}else{
$("#error-comments").hide();
}
if(isValid){
const data = {
	createdBy : userId,
	comments : $("#comments").val().trim(),
	transactionId : $("#transactionId").val().trim(),
	gatePassId : $("#gatePassId").val().trim(),
	gatePassType : gatePassType,
};
	  const xhr = new XMLHttpRequest();
xhr.open("POST", "/CWFM/contractworkmen/lostDamagegatePassAction", true); // Replace with your actual controller URL
xhr.setRequestHeader("Content-Type", "application/json"); // Set content type for JSON
xhr.onload = function() {
	hideLoader();
    if (xhr.status === 200) {
        // Handle successful response
        console.log("Data saved successfully:", xhr.responseText);
		sessionStorage.setItem("successMessage", "Gatepass lost or damage request raised successfully!");
	   loadCommonList('/contractworkmen/lostordamageFilter', 'Lost or Damage List');
	   //hideLoader();
    } else {
        // Handle error response
        console.error("Error saving data:", xhr.statusText);
		sessionStorage.setItem("errorMessage", "Failed to raise Gatepass lost or damage request!");
		//hideLoader();
    }
};

xhr.onerror = function() {
	//hideLoader();
    console.error("Request failed");
	sessionStorage.setItem("errorMessage", "Failed to raise Gatepass lost or damage request!");
	 hideLoader();
};

// Send the data object as a JSON string
xhr.send(JSON.stringify(data));
}else{
	//error 
}
}//eofunc
function redirectToWorkmenCancelView(mode) {
    var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
    if (selectedCheckboxes.length !== 1) {
        alert("Please select exactly one row to view.");
        return;
    }
    
    var selectedRow = selectedCheckboxes[0].closest('tr');
    var gatePassId = selectedRow.querySelector('[name="selectedWOs"]').value;
	var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
	if(mode === "add"){
	 												   var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed

	 												    if (gatePassType.toLowerCase() === "cancel" && (status.toLowerCase() === "approved" || status.toLowerCase() === "approval pending")) {
                                                       alert("Cancel request already created.");
                                                      return;
                                                   }}


    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };
    xhr.open("GET", "/CWFM/contractworkmen/cancelview/" + gatePassId+"/"+mode, true);
    xhr.send();
}
function redirectToWorkmenBlockView(mode) {
 var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
 if (selectedCheckboxes.length !== 1) {
     alert("Please select exactly one row to view.");
     return;
 }
 
 var selectedRow = selectedCheckboxes[0].closest('tr');
 var gatePassId = selectedRow.querySelector('[name="selectedWOs"]').value;
 var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
 												   var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed
												   if(mode === "add"){
 												   if (gatePassType.toLowerCase() === "block" && (status.toLowerCase() === "approved" || status.toLowerCase() === "approval pending")) {
                                                       alert("Block request already created.");
                                                      return;
                                                   }

												   }
 var xhr = new XMLHttpRequest();
 xhr.onreadystatechange = function() {
     if (xhr.readyState == 4 && xhr.status == 200) {
         document.getElementById("mainContent").innerHTML = xhr.responseText;
     }
 };
 xhr.open("GET", "/CWFM/contractworkmen/blockview/" + gatePassId+ "/" + mode, true);
 xhr.send();
 }
 function redirectToWorkmenUnblockView(mode) {
  var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
  if (selectedCheckboxes.length !== 1) {
      alert("Please select exactly one row to view.");
      return;
  }
  
  var selectedRow = selectedCheckboxes[0].closest('tr');
  var gatePassId = selectedRow.querySelector('[name="selectedWOs"]').value;
  var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
  												   var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed
												   if(mode === "add"){
  												    if (gatePassType.toLowerCase() === "unblock" && (status.toLowerCase() === "approval pending" || status.toLowerCase() === "approval pending")) {
                                                       alert("UnBlock request already created.");
                                                      return;
                                                   }}
  var xhr = new XMLHttpRequest();
  xhr.onreadystatechange = function() {
      if (xhr.readyState == 4 && xhr.status == 200) {
          document.getElementById("mainContent").innerHTML = xhr.responseText;
      }
  };
  xhr.open("GET", "/CWFM/contractworkmen/unblockview/" + gatePassId+"/"+mode, true);
  xhr.send();
  }
  function redirectToWorkmenBlackView(mode) {
    var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
    if (selectedCheckboxes.length !== 1) {
        alert("Please select exactly one row to view.");
        return;
    }
    
    var selectedRow = selectedCheckboxes[0].closest('tr');
    var gatePassId = selectedRow.querySelector('[name="selectedWOs"]').value;
	var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
													   var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed
													   if(mode === "add"){
													    if (gatePassType.toLowerCase() === "blacklist" && (status.toLowerCase() === "approved" || status.toLowerCase() === "approval pending")) {
                                                       alert("Blacklist request already created.");
                                                      return;
                                                   }}
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };
    xhr.open("GET", "/CWFM/contractworkmen/blackview/" + gatePassId+ "/" + mode, true);
    xhr.send();
    }
	function redirectToWorkmenDeblackView(mode) {
	  var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
	  if (selectedCheckboxes.length !== 1) {
	      alert("Please select exactly one row to view.");
	      return;
	  }
	  
	  var selectedRow = selectedCheckboxes[0].closest('tr');
	  var gatePassId = selectedRow.querySelector('[name="selectedWOs"]').value;
	  var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
	  												   var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed
													   if(mode === "add"){
	  												    if (gatePassType.toLowerCase() === "deblacklist" && (status.toLowerCase() === "approved" || status.toLowerCase() === "approval pending")) {
                                                       alert("DeBlack request already created.");
                                                      return;
                                                   }}
	  var xhr = new XMLHttpRequest();
	  xhr.onreadystatechange = function() {
	      if (xhr.readyState == 4 && xhr.status == 200) {
	          document.getElementById("mainContent").innerHTML = xhr.responseText;
	      }
	  };
	  xhr.open("GET", "/CWFM/contractworkmen/deblackview/" + gatePassId+"/"+mode, true);
	  xhr.send();
	  }
	  function redirectToWorkmenLostView(mode) {
	    var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
	    if (selectedCheckboxes.length !== 1) {
	        alert("Please select exactly one row to view.");
	        return;
	    }
	    
	    var selectedRow = selectedCheckboxes[0].closest('tr');
	    var gatePassId = selectedRow.querySelector('[name="selectedWOs"]').value;
		var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
														   var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed
														   if(mode === "add"){
														   if (gatePassType.toLowerCase() === "lost/damage" && (status.toLowerCase() === "approved" || status.toLowerCase() === "approval pending")) {
                                                       alert("Lost/Damage request already created.");
                                                      return;
                                                   }}
	    var xhr = new XMLHttpRequest();
	    xhr.onreadystatechange = function() {
	        if (xhr.readyState == 4 && xhr.status == 200) {
	            document.getElementById("mainContent").innerHTML = xhr.responseText;
	        }
	    };
	    xhr.open("GET", "/CWFM/contractworkmen/lostordamageview/" + gatePassId+"/"+mode, true);
	    xhr.send();
	    }	
function searchWorkmenWithGatePassId(){
 var gatePassId = $('#searchInput').val();

 $.ajax({
     url: '/CWFM/contractworkmen/getWorkmenDetailBasedOnId',
     type: 'POST',
     data: {
         gatePassId: gatePassId
     },
     success: function(response) {
         var tableBody = $('#workmenTable tbody');
         tableBody.empty();
			if (response.length > 0) {
             $.each(response, function(index, wo) {
                 var row = '<tr  >' +
	'<td  ><input type="checkbox" name="selectedWOs" value="' + wo.gatePassId + '"></td>'+
                           '<td  >' + wo.gatePassId + '</td>' +
                           '<td  >' + wo.firstName + '</td>' +
	  '<td  >'+ wo.lastName + '</td>' +	
	  '<td  >' + wo.gender + '</td>' +	
	  '<td  >' + wo.dateOfBirth + '</td>' +	
	  '<td  >' + wo.aadhaarNumber + '</td>' +	
	  '<td  >' + wo.contractorName + '</td>' +	
	  '<td  >' + wo.vendorCode + '</td>' +	
	  '<td  >' + wo.unitName + '</td>' +	
	  '<td  >' + wo.gatePassType + '</td>' +	
	  '<td  >' + wo.status + '</td>' +				                             
                           '</tr>';
                 tableBody.append(row);
             });
         } else {
             tableBody.append('<tr><td colspan="3">No resources found</td></tr>');
         }
     },
     error: function(xhr, status, error) {
         console.error("Error fetching data:", error);
     }
 });
}	


function previewImage(event, inputId, displayId) {
    const fileInput = document.getElementById(inputId);
    const displayElement = document.getElementById(displayId);

    // Display the file name
    if (fileInput.files.length > 0) {
        const fileName = fileInput.files[0].name;
        displayElement.textContent = fileName;
    } else {
        displayElement.textContent = '';
    }

    // Preview image from file
    const file = event.target.files[0];
    if (file && file.type.startsWith("image/")) {
        const reader = new FileReader();
        reader.onload = function(e) {
            const previewDiv = document.getElementById("preview");
            previewDiv.innerHTML = ""; // Clear previous content
            previewDiv.innerHTML = `<img src="${e.target.result}" alt="Image Preview" style="max-width: 100%; max-height: 100%;">`;
        };
        reader.readAsDataURL(file);
    }
}

        
        function exportCSVFormat() {
            var selectedRows = document.querySelectorAll('input[name="selectedWOs"]:checked');
            console.log("selectedRows.length"+selectedRows.length);
            if (selectedRows.length === 0) {
                alert("Please select at least one record to export.");
                return;
            }

            var csvContent = "data:text/csv;charset=utf-8,";
            csvContent += "GatePass Id,First Name,Last Name,Gender,Date of Birth,Aadhar Number,Contractor Name,Vendor Code,Unit Name,GatePass Type,Status\n"; // Add headers here
            selectedRows.forEach(function(row) {
                var rowData = row.parentNode.parentNode.querySelectorAll('td:nth-child(2), td:nth-child(3), td:nth-child(4), td:nth-child(5), td:nth-child(6),td:nth-child(7),td:nth-child(8),td:nth-child(9),td:nth-child(10),td:nth-child(11),td:nth-child(12)'); // Adjust column indices as needed
                var rowArray = [];
                rowData.forEach(function(cell) {
                    rowArray.push(cell.innerText);
                });
                csvContent += rowArray.join(",") + "\n";
            });
            var encodedUri = encodeURI(csvContent);
            var link = document.createElement("a");
            link.setAttribute("href", encodedUri);
            link.setAttribute("download", "QuickOnboarding.csv");
            document.body.appendChild(link);
            link.click();
        }
		function searchGatePassBasedOnPE(type) {
					    var principalEmployerId = $('#principalEmployerId').val();
					    
						var deptId=$("#deptId").val();
					    $.ajax({
					        url: '/CWFM/contractworkmen/quickOBList',
					        type: 'POST',
					        data: {
					            principalEmployerId: principalEmployerId,
								deptId:deptId,
								type:type
					        },
					        success: function(response) {
					            var tableBody = $('#workmenTable tbody');
								// 🔄 Clear previous DataTable and its config
								           if ($.fn.DataTable.isDataTable('#workmenTable')) {
								               $('#workmenTable').DataTable().destroy();
								           }
										   tableBody.empty();
					            if (Array.isArray(response) &&response.length > 0) {
					                $.each(response, function(index, wo) {
					                    var row = '<tr  >' +
												'<td  ><input type="checkbox" name="selectedWOs" value="' + wo.transactionId + '" class="bulk-check"  data-transaction="'+wo.transactionId+'"	data-gatepass="'+wo.gatePassId+'"  data-type="'+wo.gatePassTypeId+'"></td>'+
												//'<td  >' + wo.transactionId + '</td>' +
												
												 '<td>' +
        '<a href="#" class="transaction-link" ' +
        'onclick="redirectToWorkmenViewLink(\'' + wo.transactionId + '\', \'' + wo.status + '\'); return false;">' +
        wo.transactionId +
        '</a>' +
    '</td>' +
												 '<td  >' + wo.gatePassId + '</td>' +
					                              '<td  >' + wo.firstName+' ' +wo.lastName + '</td>' +
												 
												  
												  
												  '<td  >' + wo.aadhaarNumber + '</td>' +	
												  '<td  >' + wo.contractorName + '</td>' +	
												 
												  '<td  >' +wo.unitName + '</td>' +	
												  '<td  >' + wo.gatePassType + '</td>' +
												  '<td  >' + toCapitalCase(wo.onboardingType) + '</td>' +
												  '<td  >' + wo.status + '</td>' +				                             
					                              '</tr>';
					                    tableBody.append(row);
					                });
									
					            } 								

																	            // ✅ Always init after rows are drawn
																	            initWorkmenTable("workmenTable");
																	        },
					       
					        error: function(xhr, status, error) {
					            console.error("Error fetching data:", error);
					        }
					    });
					}
					function searchBlockList() {
										    var principalEmployerId = $('#principalEmployerId').val();
										    
											var deptId=$("#deptId").val();
										    $.ajax({
										        url: '/CWFM/contractworkmen/blockList',
										        type: 'POST',
										        data: {
										            principalEmployerId: principalEmployerId,
													deptId:deptId
										        },
										        success: function(response) {
										            var tableBody = $('#workmenTable tbody');
													// 🔄 Clear previous DataTable and its config
																					           if ($.fn.DataTable.isDataTable('#workmenTable')) {
																					               $('#workmenTable').DataTable().destroy();
																					           }
										            tableBody.empty();
										            if (Array.isArray(response) &&response.length > 0) {
										                $.each(response, function(index, wo) {
										                    var row = '<tr  >' +
'<td  ><input type="checkbox" name="selectedWOs" value="' + wo.gatePassId + '" class="bulk-check"  data-transaction="'+wo.transactionId+'"	data-gatepass="'+wo.gatePassId+'"  data-type="'+wo.gatePassTypeId+'"></td>'+
																	//'<td  >' + wo.transactionId + '</td>' +
										                              '<td>' + '<a href="#" class="transaction-link" ' + 'onclick="redirectToWorkmenBlockViewLink(\'' + wo.gatePassId + '\'); return false;">' + wo.transactionId + '</a>' + '</td>' +
										                              '<td  >' + wo.gatePassId + '</td>' +
										                              '<td  >' + wo.firstName + ' '+wo.lastName +'</td>' +
																	  
																	  
																	  
																	  '<td  >' + wo.aadhaarNumber + '</td>' +	
																	  '<td  >' + wo.contractorName + '</td>' +	
																	
																	  '<td  >' +wo.unitName + '</td>' +	
																	  '<td  >' + wo.gatePassType + '</td>' +
																	  '<td  >' + toCapitalCase(wo.onboardingType) + '</td>' +	
																	  '<td  >' + wo.status + '</td>' +				                             
										                              '</tr>';
										                    tableBody.append(row);
										                });
										            }
													// ✅ Always init after rows are drawn
													 initWorkmenTable("workmenTable");
													 
										        },
										        error: function(xhr, status, error) {
										            console.error("Error fetching data:", error);
										        }
										    });
										}
										function searchUnBlockList() {
										    var principalEmployerId = $('#principalEmployerId').val();
										    var deptId = $("#deptId").val();

										    $.ajax({
										        url: '/CWFM/contractworkmen/unblockList',
										        type: 'POST',
										        data: {
										            principalEmployerId: principalEmployerId,
										            deptId: deptId
										        },
										        success: function(response) {
										            var tableBody = $('#workmenTable tbody');

										            if ($.fn.DataTable.isDataTable('#workmenTable')) {
										                $('#workmenTable').DataTable().destroy();
										            }

										            tableBody.empty();

										            if (Array.isArray(response) && response.length > 0) {
										                $.each(response, function(index, wo) {
										                    var row = '<tr>' +
										                       '<td  ><input type="checkbox" name="selectedWOs" value="' + wo.gatePassId + '" class="bulk-check"  data-transaction="'+wo.transactionId+'"	data-gatepass="'+wo.gatePassId+'"  data-type="'+wo.gatePassTypeId+'"></td>'+
										                       // '<td>' + wo.transactionId + '</td>' +
										                       '<td>' + '<a href="#" class="transaction-link" ' + 'onclick="redirectToWorkmenUnblockViewLink(\'' + wo.gatePassId + '\'); return false;">' + wo.transactionId + '</a>' + '</td>' +
										                        '<td>' + wo.gatePassId + '</td>' +
										                        '<td>' + wo.firstName + ' ' + wo.lastName + '</td>' +
										                        '<td>' + wo.aadhaarNumber + '</td>' +
										                        '<td>' + wo.contractorName + '</td>' +
										                        '<td>' + wo.unitName + '</td>' +
										                        '<td>' + wo.gatePassType + '</td>' +
										                        '<td>' + toCapitalCase(wo.onboardingType) + '</td>' +
										                        '<td>' + wo.status + '</td>' +
										                        '</tr>';
										                    tableBody.append(row);
										                });
										            } 

										            // ✅ Always init after rows are drawn
										            initWorkmenTable("workmenTable");
										        },
										        error: function(xhr, status, error) {
										            console.error("Error fetching data:", error);
										        }
										    });
										}




										function searchBlackList() {
										var principalEmployerId = $('#principalEmployerId').val();
										var deptId=$("#deptId").val();
										$.ajax({
										    url: '/CWFM/contractworkmen/blackList',
										    type: 'POST',
										    data: {
										        principalEmployerId: principalEmployerId,
												deptId:deptId
										    },
										    success: function(response) {
										        var tableBody = $('#workmenTable tbody');
												if ($.fn.DataTable.isDataTable('#workmenTable')) {
																			               $('#workmenTable').DataTable().destroy();
																			           }
										        tableBody.empty();
										        if (Array.isArray(response) &&response.length > 0) {
										            $.each(response, function(index, wo) {
										                var row = '<tr  >' +
																'<td  ><input type="checkbox" name="selectedWOs" value="' + wo.gatePassId + '" class="bulk-check"  data-transaction="'+wo.transactionId+'"	data-gatepass="'+wo.gatePassId+'"  data-type="'+wo.gatePassTypeId+'"></td>'+
																//'<td  >' + wo.transactionId + '</td>' +
																'<td>' + '<a href="#" class="transaction-link" ' + 'onclick="redirectToWorkmenBlackViewLink(\'' + wo.gatePassId + '\'); return false;">' + wo.transactionId + '</a>' + '</td>' +
																'<td  >' + wo.gatePassId + '</td>' +
										                        '<td  >' + wo.firstName + ' '+ wo.lastName +'</td>' +
																'<td  >' + wo.aadhaarNumber + '</td>' +	
																'<td  >' + wo.contractorName + '</td>' +	
																'<td  >' +wo.unitName + '</td>' +	
																'<td  >' + wo.gatePassType + '</td>' +	
																'<td  >' + toCapitalCase(wo.onboardingType) + '</td>' +
																'<td  >' + wo.status + '</td>' +				                             
										                          '</tr>';
										                tableBody.append(row);
										            });
										        }
												// ✅ Always init after rows are drawn
												 initWorkmenTable("workmenTable");
												 },
										    error: function(xhr, status, error) {
										        console.error("Error fetching data:", error);
										    }
										});
										}
										
										function searchDeBlackList() {
										var principalEmployerId = $('#principalEmployerId').val();
										var deptId=$("#deptId").val();
										$.ajax({
										    url: '/CWFM/contractworkmen/deblackList',
										    type: 'POST',
										    data: {
										        principalEmployerId: principalEmployerId,
												deptId:deptId
										    },
										    success: function(response) {
										        var tableBody = $('#workmenTable tbody');
												if ($.fn.DataTable.isDataTable('#workmenTable')) {
													 $('#workmenTable').DataTable().destroy();
												}
										        tableBody.empty();
										        if (Array.isArray(response) &&response.length > 0) {
										            $.each(response, function(index, wo) {
										                var row = '<tr  >' +
																											'<td  ><input type="checkbox" name="selectedWOs" value="' + wo.gatePassId + '" class="bulk-check"  data-transaction="'+wo.transactionId+'"	data-gatepass="'+wo.gatePassId+'"  data-type="'+wo.gatePassTypeId+'"></td>'+
																											//'<td  >' + wo.transactionId + '</td>' +
																											'<td>' + '<a href="#" class="transaction-link" ' + 'onclick="redirectToWorkmenDeblackViewLink(\'' + wo.gatePassId + '\'); return false;">' + wo.transactionId + '</a>' + '</td>' +
																											'<td  >' + wo.gatePassId + '</td>' +
										                          '<td  >' + wo.firstName + ' '  + wo.lastName + '</td>' +
																
																											  
																
																'<td  >' + wo.aadhaarNumber + '</td>' +	
																'<td  >' + wo.contractorName + '</td>' +	
																 	
																'<td  >' +wo.unitName + '</td>' +	
																'<td  >' + wo.gatePassType + '</td>' +	
																'<td  >' + toCapitalCase(wo.onboardingType) + '</td>' +
																'<td  >' + wo.status + '</td>' +				                             
										                          '</tr>';
										                tableBody.append(row);
										            });
										        } 
												// ✅ Always init after rows are drawn
												 initWorkmenTable("workmenTable");
												 },
										    error: function(xhr, status, error) {
										        console.error("Error fetching data:", error);
										    }
										});
										}
										function searchCancel() {
										var principalEmployerId = $('#principalEmployerId').val();
										var deptId=$("#deptId").val();
										$.ajax({
										    url: '/CWFM/contractworkmen/cancel',
										    type: 'POST',
										    data: {
										        principalEmployerId: principalEmployerId,
												deptId:deptId
										    },
										    success: function(response) {
										        var tableBody = $('#workmenTable tbody');
												if ($.fn.DataTable.isDataTable('#workmenTable')) {
													 $('#workmenTable').DataTable().destroy();
												}
										        tableBody.empty();
										        if (Array.isArray(response) &&response.length > 0) {
										            $.each(response, function(index, wo) {
														 var onboardingTypeFormatted = wo.onboardingType? toCapitalCase(String(wo.onboardingType)): '';
										                var row = '<tr  >' +
																											'<td  ><input type="checkbox" name="selectedWOs" value="' + wo.gatePassId + '" class="bulk-check"  data-transaction="'+wo.transactionId+'"	data-gatepass="'+wo.gatePassId+'"  data-type="'+wo.gatePassTypeId+'"></td>'+
																											//'<td  >' + wo.transactionId + '</td>' +
																											'<td>' + '<a href="#" class="transaction-link" ' + 'onclick="redirectToWorkmenCancelViewLink(\'' + wo.gatePassId + '\'); return false;">' + wo.transactionId + '</a>' + '</td>' +
																											'<td  >' + wo.gatePassId + '</td>' +
										                          '<td  >' + wo.firstName + ' '+ wo.lastName +'</td>' +
																
																'<td  >' + wo.aadhaarNumber + '</td>' +	
																'<td  >' + wo.contractorName + '</td>' +	
														
																'<td  >' +wo.unitName + '</td>' +	
																'<td  >' + wo.gatePassType + '</td>' +
																'<td  >' +onboardingTypeFormatted + '</td>' +	
																'<td  >' + wo.status + '</td>' +				                             
										                          '</tr>';
										                tableBody.append(row);
										            });
										        }
												// ✅ Always init after rows are drawn
												 initWorkmenTable("workmenTable");
												 },
										    error: function(xhr, status, error) {
										        console.error("Error fetching data:", error);
										    }
										});
										}
										function searchLost() {
										var principalEmployerId = $('#principalEmployerId').val();
										var deptId=$("#deptId").val();
										$.ajax({
										    url: '/CWFM/contractworkmen/lostordamage',
										    type: 'POST',
										    data: {
										        principalEmployerId: principalEmployerId,
												deptId:deptId
										    },
										    success: function(response) {
										        var tableBody = $('#workmenTable tbody');
												if ($.fn.DataTable.isDataTable('#workmenTable')) {
													 $('#workmenTable').DataTable().destroy();
												}
										        tableBody.empty();
										        if (Array.isArray(response) &&response.length > 0) {
										            $.each(response, function(index, wo) {
										                var row = '<tr  >' +
																											'<td  ><input type="checkbox" name="selectedWOs" value="' + wo.gatePassId + '"></td>'+
																											//'<td  >' + wo.transactionId + '</td>' +
																											'<td>' + '<a href="#" class="transaction-link" ' + 'onclick="redirectToWorkmenLostViewLink(\'' + wo.gatePassId + '\'); return false;">' + wo.transactionId + '</a>' + '</td>' +
																											'<td  >' + wo.gatePassId + '</td>' +
										                          '<td  >' + wo.firstName +' '+ wo.lastName + '</td>' +
																
																'<td  >' + wo.aadhaarNumber + '</td>' +	
																'<td  >' + wo.contractorName + '</td>' +	
															
																'<td  >' +wo.unitName + '</td>' +	
																'<td  >' + wo.gatePassType + '</td>' +	
																'<td  >' + toCapitalCase(wo.onboardingType) + '</td>' +
																'<td  >' + wo.status + '</td>' +				                             
										                          '</tr>';
										                tableBody.append(row);
										            });
										        } 
												// ✅ Always init after rows are drawn
												 initWorkmenTable("workmenTable");
												 },
										    error: function(xhr, status, error) {
										        console.error("Error fetching data:", error);
										    }
										});
										}
										
										function getEic() {
											 var principalEmployerSelect = document.getElementById("principalEmployer");
										    var unitId = principalEmployerSelect.value; // Get the selected principal employer value
										    var deptSelect = document.getElementById("department");
										    var deptId = deptSelect.value; // Get the selected contractor value
										    
										 if (!unitId) {
										        alert("Please select a Principal Employer.");
										        return;
										    }
										    
										  if(!deptId){
											 alert("Please select a Department.");
										        return;
										  }

										    getEicList(unitId, deptId);

										    
										}
										function getEicList(unitId,deptId) {
										    var xhr = new XMLHttpRequest();
										    var url = contextPath + "/contractworkmen/getAllEic?unitId=" + unitId + "&deptId=" + deptId;
										    //alert("URL: " + url);
										    xhr.open("GET", url, true);

										    xhr.onload = function() {
										        if (xhr.status === 200) {
										            // Parse the response as a JSON array of workorder objects
										            var eics = JSON.parse(xhr.responseText);
										            console.log("Response:", eics);
										            
										            // Find the workorder select element
										            var eicSelect = document.getElementById("eic");
										            
										            // Clear existing options
										            eicSelect.innerHTML = '<option value="">Please select EIC</option>';
										            
										            // Populate the dropdown with the new list of workorders
										            eics.forEach(function(eiclist) {
										                var option = document.createElement("option");
										                option.value = eiclist.userId;
										                option.text = eiclist.fullName;
										                eicSelect.appendChild(option);
										            });
													autoSelectAndTrigger("eic", null, false);
										        } else {
										            console.error("Error:", xhr.statusText);
										        }
										    };

										    xhr.onerror = function() {
										        console.error("Request failed");
										    };

										    xhr.send();
										}
								/*function draftGatePass(userId,type) {
									showLoader();
									 var aadharFile = $("#aadharFile").prop("files")[0];
                                     var policeFile = $("#policeFile").prop("files")[0];
	                                 var profilePic = $("#imageFile").prop("files")[0];
	                                 var appointmentFile = $("#appointmentFile").prop("files")[0];
											    // ✅ Utility function for Capital Case
                                                   function toCapitalCase(str) {
                                                     return str
                                                    .toLowerCase()
                                                    .split(' ')
                                                    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
                                                    .join(' ');
                                                   }
                                         // ✅ Capital case transformation
                                          const firstName = toCapitalCase($("#firstName").val().trim());
                                          const lastName = toCapitalCase($("#lastName").val().trim());
                                          const relationName = toCapitalCase($("#relationName").val().trim());
										 
                                          const natureOfJob = toCapitalCase($("#natureOfJob").val().trim());
                                          const emergencyName = toCapitalCase($("#emergencyName").val().trim());
									
                                          const address = toCapitalCase($("#address").val().trim());
                                          const idMark = $("#idMark").val();
                                          const pfApplicable = $("#pfApplicable").is(":checked") ? "Yes" : "No";
										  const data = new FormData();
										
										        const jsonData = {
													transactionId:$("#transactionId").val().trim(),
										            aadhaarNumber: $("#aadharNumber").val().trim(),
										            firstName: firstName,
										            lastName: lastName,
										            dateOfBirth: $("#dateOfBirth").val().trim(),
										            gender: $("#gender").val(),
										            relationName: relationName,
										            idMark: idMark,
										            mobileNumber: $("#mobileNumber").val().trim(),
										            maritalStatus: $("#maritalStatus").val(),
										            principalEmployer: $("#principalEmployer").val(),
										            contractor: $("#contractor").val(),
										            workorder: $("#workorder").val(),
										            trade: $("#trade").val(),
										            skill: $("#skill").val(),
										            department: $("#department").val(),
										            subdepartment: $("#subdepartment").val(),
										            eic: $("#eic").val(),
										            natureOfJob: natureOfJob,
										            wcEsicNo: $("#wc").val(),
													llNo:$("#ll").val(),
										            hazardousArea: $("#hazardousArea").val(),
										            accessArea: $("#accessArea").val(),
										            uanNumber: $("#uanNumber").val().trim(),
										            healthCheckDate: $("#healthCheckDate").val().trim(),
										            pfNumber:$("#pfNumber").val().trim(),
			                                        esicNumber:$("#esicNumber").val().trim(),
										            bloodGroup: $("#bloodGroup").val(),
										            accommodation: $("#accommodation").val(),
										            academic: $("#academic").val(),
										            technical: $("#technical").val(),
										            ifscCode: $("#ifscCode").val().trim(),
										            accountNumber: $("#accountNumber").val().trim(),
										            emergencyName: emergencyName,
										            emergencyNumber: $("#emergencyNumber").val().trim(),
										            wageCategory: $("#wageCategory").val(),
										            bonusPayout: $("#bonusPayout").val(),
										            pfCap: $("#pfCap").val(),
										            zone: $("#zone").val(),
										            basic: $("#basic").val().trim(),
										            da: $("#da").val().trim(),
										            hra: $("#hra").val().trim(),
										            washingAllowance: $("#washingAllowance").val().trim(),
										            otherAllowance: $("#otherAllowance").val().trim(),
										            uniformAllowance: $("#uniformAllowance").val().trim(),
										            userId: userId,
										            gatePassAction: "save",
										            comments: $("#comments").val().trim(),
													address:address,
													doj:$("#doj").val(),
													pfApplicable:pfApplicable,
                                                    policeVerificationDate: $("#policeVerificationDate").val().trim(),
			                                        disability:$("#disability").val(),
                                                    workmenType:$("#workmenType").val(),
                                                    proficiency: $("#proficiency").val(),
                                                    unitId: $("#principalEmployer").val(),
													onboardingType:type,
										        };

										        // Serialize the JSON object to a string
												const jsonString = JSON.stringify(jsonData);

												// Append the JSON data to FormData
												data.append("jsonData", jsonString);
												 // ✅ AADHAR
                                                 if (aadharFile) {
                                                    data.append("aadharFile", aadharFile);
                                                  }

                                               // ✅ POLICE
                                               if (policeFile) {
                                                   data.append("policeFile", policeFile);
                                                }

                                              // ✅ PROFILE
                                                if (profilePic) {
                                                   data.append("profilePic", profilePic);
                                                 }

                                                // ✅ APPOINTMENT
                                              if (appointmentFile) {
                                                  data.append("appointmentFile", appointmentFile);
                                                }
                                           const additionalFields = document.querySelectorAll('.document-field');
                                             additionalFields.forEach((field) => {
                                             const docType = field.querySelector('select[name="documentType"]').value;
                                             const fileInput = field.querySelector('input[type="file"]');
                                               if (docType && fileInput.files[0]) {
                                                data.append('additionalFiles', fileInput.files[0]);
                                                data.append('documentTypes', docType);
                                                }
                                              });
                                             // ✅ COLLECT REMAINING DOC TYPES
                                          const remainingDocTypes = [];

                                            document.querySelectorAll('.document-field').forEach(field => {
                                              const docType = field.querySelector('select[name="documentType"]').value;

                                                   if (docType && docType.trim() !== "") {
                                                   remainingDocTypes.push(docType.trim().toLowerCase());
                                                     }
                                                    });

                                                   console.log("Remaining Doc Types:", remainingDocTypes);

                                          data.append("remainingDocTypes", JSON.stringify(remainingDocTypes));
                                                
										   const xhr = new XMLHttpRequest();
										        xhr.open("POST", "/CWFM/contractworkmen/draftGatePass", true);

										        xhr.onload = function () {
													hideLoader();
													if (xhr.status === 200) {
													               console.log("Data saved successfully:", xhr.responseText);
																sessionStorage.setItem("successMessage", "Gatepass drafted successfully!");
													               if(type=== "regular"){
													                   loadCommonList('/contractworkmen/list', 'On-Boarding List');
													                   //hideLoader();
													               }else if(type=== "quick"){
													                   loadCommonList('/contractworkmen/quickOnboardingList', 'Quick Onboarding List');
													                  // hideLoader();
													               }else{
																	loadCommonList('/contractworkmen/projectOnboardingList', 'Project Gatepass List');
																	//hideLoader();
																}
													           }else if (xhr.status === 400) {  
																       const msg = xhr.responseText.trim();
																       console.error("Server validation failed: " + msg);
																	   showLicenseError(msg);
																       //alert(msg); // or show in UI better
																       //sessionStorage.setItem("errorMessage", msg);
																	   return;
																   }
																   else {
																       console.error("Error saving data:", xhr.status, xhr.responseText);
																       sessionStorage.setItem("errorMessage", "Failed to draft Gatepass!");
																   }
																     };

													       xhr.onerror = function () {
															//hideLoader();
													           console.error("Request failed");
															sessionStorage.setItem("errorMessage", "Failed to draft Gatepass!");
															 hideLoader();
													       };

													       xhr.send(data);
										    } 
											*/
												
												/*function redirectToWorkmenEdit() {
											    var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
											    if (selectedCheckboxes.length !== 1) {
											        alert("Please select exactly one row to view.");
											        return;
											    }
											    
											    var selectedRow = selectedCheckboxes[0].closest('tr');
											    var transactionId = selectedRow.querySelector('[name="selectedWOs"]').value;
												var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
												   var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed

												   if (gatePassType.toLowerCase() !== "create" || status.toLowerCase() !== "draft") {
												       alert("Edit is only allowed when Gate Pass Type is 'Create' and Status is 'Draft'.");
												       return;
												   }
											    var xhr = new XMLHttpRequest();
											    xhr.onreadystatechange = function() {
											        if (xhr.readyState == 4 && xhr.status == 200) {
											            document.getElementById("mainContent").innerHTML = xhr.responseText;
														setDateRange();
											        }
											    };
											    xhr.open("GET", "/CWFM/contractworkmen/getDraftDetails/" + transactionId, true);
											    xhr.send();
											}*/
											
											function draftGatePass(userId, type) {

											    showLoader();

											    function getVal(id) {
											        var el = $("#" + id);

											        if (el.length === 0) {
											            console.warn("Missing field id:", id);
											            return "";
											        }

											        var val = el.val();
											        return val == null ? "" : val.toString().trim();
											    }

											    function getSelectVal(id) {
											        var el = $("#" + id);

											        if (el.length === 0) {
											            console.warn("Missing select id:", id);
											            return "";
											        }

											        var val = el.val();
											        return val == null ? "" : val;
											    }

											    function toCapitalCase(str) {
											        str = str == null ? "" : str.toString().trim();

											        if (str === "") {
											            return "";
											        }

											        return str
											            .toLowerCase()
											            .split(/\s+/)
											            .map(function (word) {
											                return word.charAt(0).toUpperCase() + word.slice(1);
											            })
											            .join(" ");
											    }

											    var aadharFile = $("#aadharFile").prop("files") ? $("#aadharFile").prop("files")[0] : null;
											    var policeFile = $("#policeFile").prop("files") ? $("#policeFile").prop("files")[0] : null;
											    var profilePic = $("#imageFile").prop("files") ? $("#imageFile").prop("files")[0] : null;
											    var appointmentFile = $("#appointmentFile").prop("files") ? $("#appointmentFile").prop("files")[0] : null;

											    var firstName = toCapitalCase(getVal("firstName"));
											    var lastName = toCapitalCase(getVal("lastName"));
											    var relationName = toCapitalCase(getVal("relationName"));
											    var natureOfJob = toCapitalCase(getVal("natureOfJob"));
											    var emergencyName = toCapitalCase(getVal("emergencyName"));
											    var address = toCapitalCase(getVal("address"));

											    var pfApplicable = $("#pfApplicable").is(":checked") ? "Yes" : "No";

											    var data = new FormData();

											    var jsonData = {
											        transactionId: getVal("transactionId"),
											        aadhaarNumber: getVal("aadharNumber"),
											        firstName: firstName,
											        lastName: lastName,
											        dateOfBirth: getVal("dateOfBirth"),
											        gender: getSelectVal("gender"),
											        relationName: relationName,
											        idMark: getVal("idMark"),
											        mobileNumber: getVal("mobileNumber"),
											        maritalStatus: getSelectVal("maritalStatus"),
											        principalEmployer: getSelectVal("principalEmployer"),
											        contractor: getSelectVal("contractor"),
											        workorder: getSelectVal("workorder"),
											        trade: getSelectVal("trade"),
											        skill: getSelectVal("skill"),
											        department: getSelectVal("department"),
											        subdepartment: getSelectVal("subdepartment"),
											        eic: getSelectVal("eic"),
											        natureOfJob: natureOfJob,
											        wcEsicNo: getSelectVal("wc"),
											        llNo: getSelectVal("ll"),
											        hazardousArea: getSelectVal("hazardousArea"),
											        accessArea: getSelectVal("accessArea"),
											        uanNumber: getVal("uanNumber"),
											        healthCheckDate: getVal("healthCheckDate"),
											        pfNumber: getVal("pfNumber"),
											        esicNumber: getVal("esicNumber"),
											        bloodGroup: getSelectVal("bloodGroup"),
											        accommodation: getSelectVal("accommodation"),
											        academic: getSelectVal("academic"),
											        technical: getSelectVal("technical"),
											        ifscCode: getVal("ifscCode"),
											        accountNumber: getVal("accountNumber"),
											        emergencyName: emergencyName,
											        emergencyNumber: getVal("emergencyNumber"),
											        wageCategory: getSelectVal("wageCategory"),
											        bonusPayout: getSelectVal("bonusPayout"),
											        pfCap: getSelectVal("pfCap"),
											        zone: getSelectVal("zone"),
											        basic: getVal("basic"),
											        da: getVal("da"),
											        hra: getVal("hra"),
											        washingAllowance: getVal("washingAllowance"),
											        otherAllowance: getVal("otherAllowance"),
											        uniformAllowance: getVal("uniformAllowance"),
											        userId: userId,
											        gatePassAction: "save",
											        comments: getVal("comments"),
											        address: address,
											        doj: getVal("doj"),
											        pfApplicable: pfApplicable,
											        policeVerificationDate: getVal("policeVerificationDate"),
											        disability: getSelectVal("disability"),
											        workmenType: getSelectVal("workmenType"),
											        proficiency: getSelectVal("proficiency"),
											        unitId: getSelectVal("principalEmployer"),
											        onboardingType: type
											    };

											    data.append("jsonData", JSON.stringify(jsonData));

											    if (aadharFile) {
											        data.append("aadharFile", aadharFile);
											    }

											    if (policeFile) {
											        data.append("policeFile", policeFile);
											    }

											    if (profilePic) {
											        data.append("profilePic", profilePic);
											    }

											    if (appointmentFile) {
											        data.append("appointmentFile", appointmentFile);
											    }

											    var additionalFields = document.querySelectorAll(".document-field");

											    additionalFields.forEach(function (field) {
											        var docSelect = field.querySelector('select[name="documentType"]');
											        var fileInput = field.querySelector('input[type="file"]');

											        var docType = docSelect && docSelect.value ? docSelect.value.trim() : "";

											        if (docType !== "" && fileInput && fileInput.files && fileInput.files[0]) {
											            data.append("additionalFiles", fileInput.files[0]);
											            data.append("documentTypes", docType);
											        }
											    });

											    var remainingDocTypes = [];

											    additionalFields.forEach(function (field) {
											        var docSelect = field.querySelector('select[name="documentType"]');
											        var docType = docSelect && docSelect.value ? docSelect.value.trim() : "";

											        if (docType !== "") {
											            remainingDocTypes.push(docType.toLowerCase());
											        }
											    });

											    console.log("Remaining Doc Types:", remainingDocTypes);

											    data.append("remainingDocTypes", JSON.stringify(remainingDocTypes));

											    var xhr = new XMLHttpRequest();

											    xhr.open("POST", "/CWFM/contractworkmen/draftGatePass", true);

											    xhr.onload = function () {

											        hideLoader();

											        if (xhr.status === 200) {

											            console.log("Data saved successfully:", xhr.responseText);
											            sessionStorage.setItem("successMessage", "Gatepass drafted successfully!");

											            if (type === "regular") {
											                loadCommonList("/contractworkmen/list", "On-Boarding List");
											            } else if (type === "quick") {
											                loadCommonList("/contractworkmen/quickOnboardingList", "Quick Onboarding List");
											            } else {
											                loadCommonList("/contractworkmen/projectOnboardingList", "Project Gatepass List");
											            }

											        } else if (xhr.status === 400) {

											            var msg = xhr.responseText ? xhr.responseText.trim() : "Validation failed.";
											            console.error("Server validation failed: " + msg);
											            showLicenseError(msg);
											            return;

											        } else {

											            console.error("Error saving data:", xhr.status, xhr.responseText);
											            sessionStorage.setItem("errorMessage", "Failed to draft Gatepass!");
											        }
											    };

											    xhr.onerror = function () {
											        hideLoader();
											        console.error("Request failed");
											        sessionStorage.setItem("errorMessage", "Failed to draft Gatepass!");
											    };

											    xhr.send(data);
											}
						function redirectToWorkmenEdit(mode) {

    var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');

    if (selectedCheckboxes.length !== 1) {
        alert("Please select exactly one row to view.");
        return;
    }

    var selectedRow = selectedCheckboxes[0].closest('tr');
    var transactionId = selectedRow.querySelector('[name="selectedWOs"]').value;

    var gatePassType = selectedRow.cells[7].innerText.trim();
    var status = selectedRow.cells[9].innerText.trim();

    if ( status.toLowerCase() !== "draft") {
        alert("Edit is only allowed when Status is 'Draft'.");
        return;
    }

    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState == 4 && xhr.status == 200) {

            // ✅ Load JSP
            document.getElementById("mainContent").innerHTML = xhr.responseText;

            setDateRange();
			initializeAutoSelects();

            // 🔥 FORCE EXECUTION OF JSP SCRIPTS
           setTimeout(function () {

    const scripts = document
        .getElementById("mainContent")
        .getElementsByTagName("script");

    for (let i = 0; i < scripts.length; i++) {
        try {
            eval(scripts[i].innerText);
        } catch (e) {
            console.error("Script error:", e);
        }
    }

                // ✅ LOAD PROFILE PREVIEW
                loadProfilePreview(window.profileFileFromJsp,window.userIdFromJsp,window.transactionIdFromJsp);
                // ✅ NOW VARIABLE WILL EXIST
                if (window.additionalFileMapFromJsp) {
                    renderAdditionalDocuments(window.additionalFileMapFromJsp);
                } 
               /* else {
                    console.log("Map still not found");
                }*/

            }, 200);
        }
    };

 xhr.open("GET", "/CWFM/contractworkmen/getDraftDetails/" + transactionId + "/" + mode, true);
    xhr.send();
}
											function searchRenew() {
											var principalEmployerId = $('#principalEmployerId').val();

																	var deptId=$("#deptId").val();
											$.ajax({
											    url: '/CWFM/contractworkmen/renewList',
											    type: 'POST',
											    data: {
											        principalEmployerId: principalEmployerId,
																			deptId:deptId
											    },
											    success: function(response) {
											        var tableBody = $('#workmenTable tbody');
													  if ($.fn.DataTable.isDataTable('#workmenTable')) {
														$('#workmenTable').DataTable().destroy();
													}
											        tableBody.empty();
											        if (Array.isArray(response) &&response.length > 0) {
											            $.each(response, function(index, wo) {
											                var row = '<tr  >' +
																							'<td  ><input type="checkbox" name="selectedWOs" value="' + wo.gatePassId + '" class="bulk-check"  data-transaction="'+wo.transactionId+'"	data-gatepass="'+wo.gatePassId+'"  data-type="'+wo.gatePassTypeId+'"></td>'+
																							//'<td  >' + wo.transactionId + '</td>' +
																							'<td>' + '<a href="#" class="transaction-link" ' + 'onclick="redirectToWorkmenRenewViewLink(\'' + wo.gatePassId + '\'); return false;">' + wo.transactionId + '</a>' + '</td>' +
																							 '<td  >' + wo.gatePassId + '</td>' +
											                          '<td  >' + wo.firstName + ' ' + wo.lastName +'</td>' +
																							 
																							  '<td  >' + wo.aadhaarNumber + '</td>' +	
																							  '<td  >' + wo.contractorName + '</td>' +	
																							
																							  '<td  >' +wo.unitName + '</td>' +	
																							  '<td  >' + wo.gatePassType + '</td>' +
																							  '<td  >' + toCapitalCase(wo.onboardingType) + '</td>' +	
																							  '<td  >' + wo.status + '</td>' +				                             
											                          '</tr>';
											                tableBody.append(row);
											            });
											        } 
													// ✅ Always init after rows are drawn
														initWorkmenTable("workmenTable");
											    },
											    error: function(xhr, status, error) {
											        console.error("Error fetching data:", error);
											    }
											});
																}
																
function redirectToWorkmenRenewEdit() {
	var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
	if (selectedCheckboxes.length !== 1) {
			alert("Please select exactly one row to view.");
			return;
	}
																 
	var selectedRow = selectedCheckboxes[0].closest('tr');
	var gatePassId = selectedRow.querySelector('[name="selectedWOs"]').value;
	var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
	   var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed

	   if (!(status.toLowerCase() === "approved" && 
	        (gatePassType.toLowerCase() === "create" || gatePassType.toLowerCase() === "project" || gatePassType.toLowerCase() === "renew"))) {
	       
	       alert("Edit is only allowed when Gate Pass Type is 'Create / Project' and Status is 'Approved'.");
	       return;
	   }
	var xhr = new XMLHttpRequest();
	xhr.onreadystatechange = function() {
	if (xhr.readyState == 4 && xhr.status == 200) {
			document.getElementById("mainContent").innerHTML = xhr.responseText;
			setDateRange();
	}
	};
	xhr.open("GET", "/CWFM/contractworkmen/renew/" + gatePassId, true);
	xhr.send();
}
function renewGatePass(userId) {
	showLoader();
	
	// ✅ Clear all previous errors first
    $("#docTabGlobalError").hide().text("");
    $("label[id^='error-']").hide();
    
    let basicValid = true;
    let employmentValid = true;
    let otherValid = true;
    let wagesValid = true;
    let documentValid = true;
    let minimumwageValid = true;
    
    var aadharFile = $("#aadharFile").prop("files")[0];
    var policeFile = $("#policeFile").prop("files")[0];
	var profilePic = $("#imageFile").prop("files")[0];
	var appointmentFile = $("#appointmentFile").prop("files")[0];
    // Validate the files (optional)
    if (!validateFiles(aadharFile, policeFile,profilePic,appointmentFile)) {
        documentValid = false; // Stop the upload if validation fails
        hideLoader();
    }

    if (!validateBasicData()) {
        basicValid = false;
        hideLoader();
    }

    if (!validateEmploymentInformation()) {
        employmentValid = false;
        hideLoader();
    }
    if (!validatePfForm11Requirement()) {
	        documentValid = false;
	         hideLoader();
	    }
    if (!validateOtherInformation()) {
        otherValid = false;
        hideLoader();
    }

    if (!validateWages()) {
        wagesValid = false;
        hideLoader();
    }
  if(!validateMinimumWage()){
			minimumwageValid = false;
             hideLoader();
		}
    console.log("basicValid: " + basicValid);
    console.log("employmentValid: " + employmentValid);
    console.log("otherValid: " + otherValid);
    console.log("wagesValid: " + wagesValid);
    console.log("documentValid: " + documentValid);
    
    //TAB NAME ERROR MESSAGE LOGIC (YOUR REQUIREMENT)
    let errorTabs = [];

    if (!basicValid) errorTabs.push("Basic");
    if (!employmentValid) errorTabs.push("Employment");
    if (!otherValid) errorTabs.push("Other");
    if (!wagesValid) errorTabs.push("Wages");

    // If any tab has errors → show message in Documents tab
    if (errorTabs.length > 0) {

        let msg = "Please check errors in: " + errorTabs.join(", ") + " tab(s).";

        $("#docTabGlobalError")
            .text(msg)
            .show();

        hideLoader();
        return;
    }
    
 const pfApplicable = $("#pfApplicable").is(":checked") ? "Yes" : "No";
    if (basicValid && employmentValid && otherValid && wagesValid && minimumwageValid && documentValid) {
        const data = new FormData();
        const jsonData = {
			transactionId:$("#transactionId").val().trim(),
			gatePassId:$("#gatePassId").val().trim(),
            aadhaarNumber: $("#aadharNumber").val().trim(),
            firstName: $("#firstName").val().trim(),
            lastName: $("#lastName").val().trim(),
            dateOfBirth: $("#dateOfBirth").val().trim(),
            gender: $("#gender").val(),
            relationName: $("#relationName").val().trim(),
            idMark: $("#idMark").val(),
            mobileNumber: $("#mobileNumber").val().trim(),
            maritalStatus: $("#maritalStatus").val(),
            principalEmployer: $("#principalEmployer").val(),
            contractor: $("#contractor").val(),
            workorder: $("#workorder").val(),
            trade: $("#trade").val(),
            skill: $("#skill").val(),
            department: $("#department").val(),
            subdepartment: $("#subdepartment").val(),
            eic: $("#eic").val(),
            natureOfJob: $("#natureOfJob").val().trim(),
            wcEsicNo: $("#wc").val(),
			llNo:$("#ll").val(),
            hazardousArea: $("#hazardousArea").val(),
            accessArea: $("#accessArea").val(),
            uanNumber: $("#uanNumber").val().trim(),
            healthCheckDate: $("#healthCheckDate").val().trim(),
            pfNumber:$("#pfNumber").val(),
			esicNumber:$("#esicNumber").val(),
            bloodGroup: $("#bloodGroup").val(),
            accommodation: $("#accommodation").val(),
            academic: $("#academic").val(),
            technical: $("#technical").val(),
            ifscCode: $("#ifscCode").val().trim(),
            accountNumber: $("#accountNumber").val().trim(),
            emergencyName: $("#emergencyName").val().trim(),
            emergencyNumber: $("#emergencyNumber").val().trim(),
            wageCategory: $("#wageCategory").val(),
            bonusPayout: $("#bonusPayout").val(),
            pfCap: $("#pfCap").val(),
            zone: $("#zone").val(),
            basic: $("#basic").val().trim(),
            da: $("#da").val().trim(),
            hra: $("#hra").val().trim(),
            washingAllowance: $("#washingAllowance").val().trim(),
            otherAllowance: $("#otherAllowance").val().trim(),
            uniformAllowance: $("#uniformAllowance").val().trim(),
            userId: userId,
            gatePassAction: "save",
            comments: $("#comments").val().trim(),
			address:$("#address").val().trim(),
			doj:$("#doj").val(),
			pfApplicable:pfApplicable,
            policeVerificationDate: $("#policeVerificationDate").val().trim(),
            disability:$("#disability").val(),
            workmenType:$("#workmenType").val(),
            proficiency: $("#proficiency").val(),
            unitId: $("#principalEmployer").val(),
        };

        // Serialize the JSON object to a string
		const jsonString = JSON.stringify(jsonData);

		// Append the JSON data to FormData
		data.append("jsonData", jsonString);

        // Append the files to the FormData
        if (aadharFile) {
            data.append("aadharFile", aadharFile);
        }
        if (policeFile) {
            data.append("policeFile", policeFile);
        }
		
		
		if(profilePic){
			data.append("profilePic",profilePic);
		}
		if(appointmentFile){
			data.append("appointmentFile",appointmentFile);
		}
		
    	const additionalFields = document.querySelectorAll('.document-field');
    additionalFields.forEach((field, index) => {
        const docType = field.querySelector('select[name="documentType"]').value;
        const fileInput = field.querySelector('input[type="file"]');

        if (docType && fileInput.files[0]) {
            data.append('additionalFiles', fileInput.files[0]);
            data.append('documentTypes', docType);
        }
    });
        
		/*// Log FormData content
for (const [key, value] of data.entries()) {
    console.log(key, value instanceof File ? value.name : value); // Log filename if it's a File
}*/
        // Send the data to the server using AJAX
        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/CWFM/contractworkmen/renewGatePass", true);

        xhr.onload = function () {
			hideLoader();
            if (xhr.status === 200) {
                console.log("Data saved successfully:", xhr.responseText);
				sessionStorage.setItem("successMessage", "Gatepass renew request raised successfully!");
                loadCommonList('/contractworkmen/renewFilter', 'Renew List');
				//hideLoader();
            } else if (xhr.status === 400) {  
				       const msg = xhr.responseText.trim();
				       console.error("Server validation failed: " + msg);
					   showLicenseError(msg);
				       //alert(msg); // or show in UI better
				       //sessionStorage.setItem("errorMessage", msg);
					   return;
				   }
				   else {
                console.error("Error saving data:", xhr.status, xhr.responseText);
				sessionStorage.setItem("errorMessage", "Failed to raise Gatepass renew request!");
            }
        };

        xhr.onerror = function () {
            console.error("Request failed");
			sessionStorage.setItem("errorMessage", "Failed to raise Gatepass renew request!");
			hideLoader();
        };

        // Send the FormData object
        xhr.send(data);
    } else {
        console.error("Validation failed for one or more fields.");
    }
}

function approveRejectRenew(status,gatePassType){
	showLoader();
	let isValid=true;
	 const approvercomments = $("#approvercomments").val().trim();
   if (approvercomments === "" && status==5) {
       $("#error-approvercomments").show();
       alert("Comments Required in Documents");
       isValid = false;
       hideLoader();
   }else{
	$("#error-approvercomments").hide();
}
const Role= $("#roleName").val().trim();
	const onboardingDocType= $("#onboardingDocType").val();
	 if (onboardingDocType === "" && Role=='Security') {
        $("#error-onboardingDocType").show();
        alert("Onboarding Document Type is Required in Documents");
        isValid = false;
        hideLoader();
    }else{
		$("#error-onboardingDocType").hide();
	}
	
if(isValid){
	const data = {
		approverId : $("#userId").val().trim(),
		comments : $("#approvercomments").val().trim(),
		status : status,
		transactionId : $("#transactionId").val().trim(),
		gatePassId : $("#gatePassId").val().trim(),
		approverRole : $("#roleName").val().trim(),
		roleId :$("#roleId").val().trim(),
		gatePassType : gatePassType,
		onboardingDocType:$("#onboardingDocType").val(),
	};
		  const xhr = new XMLHttpRequest();
   xhr.open("POST", "/CWFM/contractworkmen/approveRejectGatePass", true); // Replace with your actual controller URL
   xhr.setRequestHeader("Content-Type", "application/json"); // Set content type for JSON
   xhr.onload = function() {
	   hideLoader();
       if (xhr.status === 200) {
           // Handle successful response
           console.log("Data saved successfully:", xhr.responseText);
		   sessionStorage.setItem("successMessage", "Gatepass renew request approved/rejected successfully!");
         loadCommonList('/contractworkmen/renewFilter', 'Renew List');
         //hideLoader();
       } 		 else if (xhr.status === 400) {  
		 							       const msg = xhr.responseText.trim();
		 							       console.error("Server validation failed: " + msg);
		 								   showLicenseError(msg);
		 							       //alert(msg); // or show in UI better
		 							       //sessionStorage.setItem("errorMessage", msg);
		 								   //hideLoader();
		 								   return;
		 							   }
	   else {
           // Handle error response
           console.error("Error saving data:", xhr.statusText);
		   sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass renew request!");
		   //hideLoader();
       }
   };
   
   xhr.onerror = function() {
	   //hideLoader();
       console.error("Request failed");
	   sessionStorage.setItem("errorMessage", "Failed to approve/reject Gatepass renew request!");
	    hideLoader();
   };
   
   // Send the data object as a JSON string
   xhr.send(JSON.stringify(data));
	}else{
		//error 
	}
	}//eofunc
	

		function redirectToWorkmenRenewView() {
	    var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
	    if (selectedCheckboxes.length !== 1) {
	        alert("Please select exactly one row to view.");
	        return;
	    }
	    
	    var selectedRow = selectedCheckboxes[0].closest('tr');
	    var gatePassId = selectedRow.querySelector('[name="selectedWOs"]').value;

	    var xhr = new XMLHttpRequest();
	    xhr.onreadystatechange = function() {
	        if (xhr.readyState == 4 && xhr.status == 200) {
	            document.getElementById("mainContent").innerHTML = xhr.responseText;
	        }
	    };
	    xhr.open("GET", "/CWFM/contractworkmen/renewview/" + gatePassId, true);
	    xhr.send();
	}

	
	

	function generateOtp() {
	    const aadhaarNumber = document.getElementById("aadharNumber").value;

	    // Simple validation
	    if (!aadhaarNumber || aadhaarNumber.length !== 12 || !/^\d{12}$/.test(aadhaarNumber)) {
	        document.getElementById("otpError").innerText = "Please enter a valid 12-digit Aadhaar number.";
	        document.getElementById("otpMessage").innerText = "";
	        return;
	    }

	    var xhr = new XMLHttpRequest();
	    var url = "/CWFM/contractworkmen/generateOtp"; // Your mapped Spring Controller URL

	    xhr.open("POST", url, true);
	    xhr.setRequestHeader("Content-Type", "application/json");

	    xhr.onload = function () {
	        var response;
	        try {
	            response = JSON.parse(xhr.responseText);
	        } catch (e) {
	            document.getElementById("otpError").innerText = "Invalid response from server.";
	            document.getElementById("otpMessage").innerText = "";
	            return;
	        }

	        if (xhr.status === 200 || xhr.status === 422) {
	            if (response.success) {
	                document.getElementById("otpMessage").innerText = response.message;
	                document.getElementById("otpError").innerText = "";
	            } else {
	                document.getElementById("otpError").innerText = response.message +" "+response.status;
	                document.getElementById("otpMessage").innerText = "";
	            }
	        } else {
	            document.getElementById("otpError").innerText = "Error: " + (response.message || xhr.statusText);
	            document.getElementById("otpMessage").innerText = "";
	        }
	    };

	    xhr.onerror = function () {
	        document.getElementById("otpError").innerText = "Request failed.";
	        document.getElementById("otpMessage").innerText = "";
	    };

	    var data = JSON.stringify({ aadhaarNumber: aadhaarNumber });
	    xhr.send(data);
	}
	
	function verifyOtp() {
	    const otp = document.getElementById("otp").value;

	    if (!otp || otp.length < 6 || !/^\d+$/.test(otp)) {
	        document.getElementById("otpError").innerText = "Please enter a valid numeric OTP.";
	        document.getElementById("otpMessage").innerText = "";
	        return;
	    }

	    const data = {
	        otp: otp,
	        clientId: "aadhaar_v2_vtzQlmkayhLKnPYgkEmy"
	    };

	    const xhr = new XMLHttpRequest();
	    xhr.open("POST", "/CWFM/contractworkmen/verifyOtp", true);
	    xhr.setRequestHeader("Content-Type", "application/json");

	    xhr.onload = function () {
	        let response;
	        try {
	            response = JSON.parse(xhr.responseText);
	        } catch (e) {
	            document.getElementById("otpError").innerText = "Invalid server response.";
	            return;
	        }

	        if (xhr.status === 200 && response.success) {
	            document.getElementById("otpMessage").innerText = "OTP Verified Successfully.";
	            document.getElementById("otpError").innerText = "";

	            // Populate form fields
	            document.getElementById("firstName").value = response.fullName?.split(" ")[0] || "";
	            document.getElementById("lastName").value = response.fullName?.split(" ").slice(1).join(" ") || "";
	            document.getElementById("dateOfBirth").value = response.dob || "";
	            document.getElementById("gender").value = response.gender || "";
	            document.getElementById("address").value = response.address || "";
	        } else {
	            document.getElementById("otpError").innerText = response.message || "OTP verification failed.";
	        }
	    };

	    xhr.onerror = function () {
	        document.getElementById("otpError").innerText = "Request failed.";
	    };

	    xhr.send(JSON.stringify(data));
	}

function formatToTwoDecimalPlaces(input) {
  let value = parseFloat(input.value);
  if (isNaN(value)) {
    input.value = '0.00';
  } else {
    input.value = value.toFixed(2);
  }
}
function redirectToWorkmenQuickAdd() {
console.log("redirectToWorkmenQuickAdd called");
    // Fetch the content of add.jsp using AJAX
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            // Update the mainContent element with the fetched content
            document.getElementById("mainContent").innerHTML = xhr.responseText;
			setDateRange();
			initializeAutoSelects();
        }
    };
    xhr.open("GET", "/CWFM/contractworkmen/quickOnboardingCreation", true);
    xhr.send();
}
function redirectToWorkmenAdd(){
	// Fetch the content of add.jsp using AJAX
	    var xhr = new XMLHttpRequest();
	    xhr.onreadystatechange = function() {
	        if (xhr.readyState == 4 && xhr.status == 200) {
	            // Update the mainContent element with the fetched content
	            document.getElementById("mainContent").innerHTML = xhr.responseText;
				setDateRange();
				initializeAutoSelects();
	        }
	    };
	    xhr.open("GET", "/CWFM/contractworkmen/addQuickOB", true);
	    xhr.send();
}
function setDateRange() {
	const today = new Date();

	// Person must be between 18 and 70 years old
	const maxDate = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate()); // youngest allowed DOB
	const minDate = new Date(today.getFullYear() - 70, today.getMonth(), today.getDate()); // oldest allowed DOB

	$(".datetimepickerformat").datepicker({
	    dateFormat: 'yy-mm-dd',
	    changeMonth: true,
	    changeYear: true,
	    yearRange: `${minDate.getFullYear()}:${maxDate.getFullYear()}`, // dynamic range
	    minDate: minDate,
	    maxDate: maxDate,
	    defaultDate: maxDate // 👈 ensures calendar opens at the 18-year-old boundary
	});

    $('.datetimepickerformat1').datepicker({//date of joiing
        dateFormat: 'yy-mm-dd', // Set the date format
        changeMonth: true,      // Allow changing month via dropdown
        changeYear: true,       // Allow changing year via dropdown
        yearRange: "0:+100", 
        minDate: -15,
		maxDate: +15             // Prevent selecting future dates
    });
    const sixMonthsAgo = new Date();
    sixMonthsAgo.setMonth(today.getMonth() - 6);

    $(".datetimepickerformat2").datepicker({//health check date
        dateFormat: 'yy-mm-dd',
        changeMonth: true,
        changeYear: true,
        minDate: sixMonthsAgo,
        maxDate: today,
        yearRange: `${sixMonthsAgo.getFullYear()}:${today.getFullYear()}`
    });
   
const oneYearAgo = new Date();
oneYearAgo.setFullYear(today.getFullYear() - 1);
   $(".datetimepickerformat3").datepicker({
    dateFormat: 'yy-mm-dd',
    changeMonth: true,
    changeYear: true,
    minDate: oneYearAgo,
    maxDate: today,
    yearRange: `${today.getFullYear() - 1}:${today.getFullYear()}`
});
$(".grantdatetimepicker").datepicker({
    dateFormat: 'yy-mm-dd',
    changeMonth: true,
    changeYear: true,
    yearRange: "1900:+0",   // from 1900 to current year
    maxDate: 0,              // ❌ cannot select future date
    defaultDate: 0           // today selected by default
});
$(".expirydatetimepicker").datepicker({
    dateFormat: 'yy-mm-dd',
    changeMonth: true,
    changeYear: true,
    yearRange: "+0:+50",     // from current year to 50 years future
    minDate: 1,               // ❌ cannot select today or past
    defaultDate: +1           // tomorrow selected by default
});
$(".pfapplydatetimepicker").datepicker({
    dateFormat: 'yy-mm-dd',
    changeMonth: true,
    changeYear: true,
    yearRange: "1900:+0",   // from 1900 to current year
    maxDate: -1,              // ❌ cannot select future date
    defaultDate: 0           // today selected by default
});
$(".compliancedatetimepicker").datepicker({
    dateFormat: 'yy-mm-dd',
    changeMonth: true,
    changeYear: true,
    yearRange: "1900:+50",   // past 1900 to 50 years in future
    minDate: null,            // allow past
    maxDate: null,            // allow future
    defaultDate: null
});
$(".contractFromdatetimepicker").datepicker({
    dateFormat: 'yy-mm-dd',
    changeMonth: true,
    changeYear: true,
    yearRange: "1900:+50",

    minDate: 0,   // ✅ today
    maxDate: null, // ✅ no upper limit (future allowed)

    defaultDate: 0 // optional → sets today as default
});


const threeYearsAgo = new Date();
threeYearsAgo.setFullYear(today.getFullYear() - 3);

const threeYearsLater = new Date();
threeYearsLater.setFullYear(today.getFullYear() + 3);

$(".datetimepickerActiveWorkmenformat").datepicker({
    dateFormat: "yy-mm-dd",
    changeMonth: true,
    changeYear: true,
    minDate: threeYearsAgo,
    maxDate: threeYearsLater,
    yearRange: (today.getFullYear() - 3) + ":" + (today.getFullYear() + 3)
});

}

function validatePfForm11Requirement() {
    const pfApplicable = document.getElementById("pfApplicable").checked;
    const form11ErrorContainer = document.getElementById("form11-error-message");
    let form11Present = false;

    if (!pfApplicable) {
        const additionalFields = document.querySelectorAll('.document-field');

        additionalFields.forEach((field) => {
            const docType = field.querySelector('select[name="documentType"]')?.value;
            const file = field.querySelector('input[type="file"]')?.files[0];

            if (docType === "Form11" && file) {
                form11Present = true;
            }
        });

        if (!form11Present) {
            form11ErrorContainer.textContent = "Form11 document is mandatory since PF is not applicable.";
            form11ErrorContainer.style.display = "block";
            return false;
        }
    }

    // Clear error if condition passes or PF is applicable
    form11ErrorContainer.textContent = "";
    form11ErrorContainer.style.display = "none";
    return true;
}



  
  


var cameraStream = null;

function previewImage(event, inputId, fileNameSpanId) {
    const input = document.getElementById(inputId);
    const preview = document.getElementById("preview");
    const fileNameSpan = document.getElementById(fileNameSpanId);

    preview.innerHTML = "";
    fileNameSpan.textContent = "";

    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    fileNameSpan.textContent = file.name;

    if (inputId === "mobileCameraInput") {
        const dt = new DataTransfer();
        dt.items.add(file);
        document.getElementById("imageFile").files = dt.files;
    }

    if (!file.type.startsWith("image/")) return;

    const reader = new FileReader();
    reader.onload = function (e) {
        const img = document.createElement("img");
        img.src = e.target.result;
        img.style.maxWidth = "100%";
        img.style.maxHeight = "100%";
        preview.appendChild(img);
    };
    reader.readAsDataURL(file);
}

function isMobileDevice() {
    return /Android|iPhone|iPad|iPod/i.test(navigator.userAgent);
}

function openCamera() {

    if (isMobileDevice()) {
        document.getElementById("mobileCameraInput").click();
        return;
    }

    navigator.mediaDevices.getUserMedia({ video: true })
        .then(stream => {
            cameraStream = stream;
            const video = document.getElementById("webcam");
            video.srcObject = stream;

            video.onloadedmetadata = () => {   
                video.play();
                video.style.display = "block";
                document.getElementById("cameraButtons").style.display = "block";
            };
        })
        .catch(() => {
            alert("Camera not available or permission denied");
        });
}
function captureImage() {
    const video = document.getElementById("webcam");
    const canvas = document.getElementById("canvas");

    if (video.videoWidth === 0) {
        alert("Camera still loading... Try again.");
        return;
    }

    const ctx = canvas.getContext("2d");

    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;

    ctx.drawImage(video, 0, 0, canvas.width, canvas.height);

    canvas.toBlob(blob => {
        const file = new File([blob], "camera-image.jpg", { type: "image/jpeg" });

        const dt = new DataTransfer();
        dt.items.add(file);
        document.getElementById("imageFile").files = dt.files;

        previewImage(
            { target: document.getElementById("imageFile") },
            "imageFile",
            "imageFileName"
        );

        closeCamera();
    }, "image/jpeg", 0.95);
}

function closeCamera() {
    if (cameraStream) {
        cameraStream.getTracks().forEach(track => track.stop());
        cameraStream = null;
    }
    document.getElementById("webcam").style.display = "none";
    document.getElementById("cameraButtons").style.display = "none";
}



  
  function goBackToonboardingList() {
    	 loadCommonList('/contractworkmen/list', 'On-Boarding List');
    }
    function goBackToquickonboardingList() {
    	 loadCommonList('/contractworkmen/quickOnboardingList', 'Quick Onboarding List');
    }

    
	/*function initWorkmenTable(tablename) {
	    const selector = '#' + tablename;

	    if ($.fn.DataTable.isDataTable(selector)) {
	        $(selector).DataTable().destroy();
	    }

	    $(selector).DataTable({
	        paging: true,
	        searching: true,
	        ordering: true
	    });
	}
*/
function initWorkmenTable(tableId) {
    var selector = '#' + tableId;

    if ($.fn.DataTable.isDataTable(selector)) {
        $(selector).DataTable().destroy();
    }

    $(selector).DataTable({
        paging: true,
        searching: true,
        ordering: true,
        lengthChange: true,
        info: true,
        pageLength: 10,
        language: {
            emptyTable: "No records found",
            zeroRecords: "No matching records found"
        },
        dom: '<"top"f>rt<"bottom"lip><"clear">'
    });
}
   function searchGatePassStatus() {
    const transactionId = $('#transactionId').val().trim();
    const gatepassId = $('#gatePassId').val().trim();

   if (!transactionId && !gatepassId) {
    alert("Please enter Transaction ID or Gate Pass ID");
    return;
   }

    $.ajax({
        url: '/CWFM/entrypassstatus/statusList',
        type: 'POST',
        data: {
            transactionId: transactionId,
            gatepassId: gatepassId
        },
        success: function (response) {
            const tableBody = $('#workmenTable tbody');
            tableBody.empty();

            if (response.length > 0) {
                response.forEach(function (wo) {
                    const row = `<tr>
                        <td><input type="checkbox" name="selectedWOs" value="${wo.transactionId || ''}"></td>
                        <td>${wo.transactionId || ''}</td>
                        <td>${wo.firstName || ''} ${wo.lastName || ''}</td>
                        <td>${wo.lastName || ''}</td>
                        <td>${wo.aadhaarNumber || ''}</td>
                        <td>${wo.approvedBy || ''}</td>
                        <td>${wo.pendingWith || ''}</td>
                    </tr>`;
                    tableBody.append(row);
                });
            } else {
                tableBody.append('<tr><td colspan="8">No data found</td></tr>');
            }
        },
        error: function (xhr, status, error) {
            console.error("Error fetching data:", error);
        }
    });
}

 function searchGatePassHistory() {
    const aadharNumber = $('#aadharNumber').val().trim();

    $.ajax({
        url: '/CWFM/entrypassstatus/history',
        type: 'POST',
        data: {
            aadharNumber: aadharNumber
        },
        success: function (response) {
            const tableBody = $('#workmenTable tbody');
            tableBody.empty();

            if (response.length > 0) {
                response.forEach(function (wo) {
                    const row = `<tr>
                        <td> <input type="checkbox" name="selectedWOs" value="${wo.transactionId}"></td>
                        <td>${wo.transactionId || ''}</td>
                         <td>${wo.gatePassId || ''}</td>
                        <td>${wo.firstName || ''} ${wo.lastName || ''}</td>
                        <td>${wo.lastName || ''}</td>
                         <td>${wo.gatePassType || ''}</td>
                         <td>${wo.status || ''}</td>
                        <td>${wo.unitName || ''}</td>
                       
                    </tr>`;
                    tableBody.append(row);
                });
            } else {
                tableBody.append('<tr><td colspan="8">No data found</td></tr>');
            }
        },
        error: function (xhr, status, error) {
            console.error("Error fetching data:", error);
        }
    });
}  

function loadDepartments(unitId) {
    if (!unitId) {
        $("#deptId").html("<option value=''>Select Department</option>");
        return;
    }

    $.ajax({
        url: "/CWFM/contractworkmen/getAllDepartments",
        type: "GET",
        data: { unitId: unitId },
        success: function (departments) {
            var deptSelect = $("#deptId");
            deptSelect.empty();
            deptSelect.append("<option value=''>Select Department</option>");
            $.each(departments, function (i, dept) {
                deptSelect.append(
                    "<option value='" + dept.departmentId + "'>" + dept.department + "</option>"
                );
            });
        },
        error: function () {
            alert("Error loading departments!");
        }
    });
}
function verifyOtpDemo() {
	    const aadharNo = document.getElementById("aadharNumber").value;


	    
	    
	        if (aadharNo === '992158496671') {
	           
	            document.getElementById("firstName").value =  "Shashikant";
	            document.getElementById("lastName").value = "Shukla";
	            document.getElementById("dateOfBirth").value = "2002-07-07";
	            document.getElementById("gender").value = "11";
	            document.getElementById("address").value =  "Anakapalli rebaka";
				document.getElementById("mobileNumber").value =  "9876543210";
	        } else if(aadharNo === '550188039490'){
								document.getElementById("firstName").value =  "Nivetha";
					            document.getElementById("lastName").value = "Mohansingh";
					            document.getElementById("dateOfBirth").value = "1991-07-07";
					            document.getElementById("gender").value = "12";
					            document.getElementById("address").value =  "SVA anada nilaya,ist cross immadihalli road, nagondanahalli,Bengaluru-560066";
								document.getElementById("mobileNumber").value =  "9876543111";
	          }							else if(aadharNo === '880188039490'){
															document.getElementById("firstName").value =  "Hemalatha";
												            document.getElementById("lastName").value = "Karanam";
												            document.getElementById("dateOfBirth").value = "1991-08-07";
												            document.getElementById("gender").value = "12";
												            document.getElementById("address").value =  "Ardente Office One, nagondanahalli,Bengaluru-560066";
															document.getElementById("mobileNumber").value =  "8876543111";
								          }
	   

	  
	}
	
	function generateToken(){
		const aadhaarNumber = document.getElementById("aadharNumber").value;
		const transactionId= document.getElementById("transactionId").value;
		let valid=false;
			    // Simple validation
			    if (!aadhaarNumber || aadhaarNumber.length !== 12 || !/^\d{12}$/.test(aadhaarNumber)) {
			        document.getElementById("otpError").innerText = "Please enter a valid 12-digit Aadhaar number.";
			        document.getElementById("otpMessage").innerText = "";
			        return;
			    }else{
					valid = aadharDuplicateDBCheck(aadhaarNumber);
				}				
if(valid){
			    var xhr = new XMLHttpRequest();
			    var url = "/CWFM/contractworkmen/generateToken"; // Your mapped Spring Controller URL

			    xhr.open("POST", url, true);
			    xhr.setRequestHeader("Content-Type", "application/json");

			    xhr.onload = function () {
			        var response;
			        try {
			            response = JSON.parse(xhr.responseText);
			        } catch (e) {
			            document.getElementById("otpError").innerText = "Invalid response from server.";
			            document.getElementById("otpMessage").innerText = "";
			            return;
			        }

			        if (xhr.status === 200 || xhr.status === 422) {
						if (response.url) {
							    openDigiModal(response.token);

						}

			           
						 else {
			                document.getElementById("otpError").innerText = response.message +" "+response.status;
			                document.getElementById("otpMessage").innerText = "";
			            }
			        } else {
			            document.getElementById("otpError").innerText = "Error: " + (response.message || xhr.statusText);
			            document.getElementById("otpMessage").innerText = "";
			        }
			    };

			    xhr.onerror = function () {
			        document.getElementById("otpError").innerText = "Request failed.";
			        document.getElementById("otpMessage").innerText = "";
			    };

			    var data = JSON.stringify({ aadhaarNumber: aadhaarNumber });
			    xhr.send(data);
				}
	}
	
	function openDigiModal(token) {
	    const modal = document.getElementById("digiModal");
	    const container = document.getElementById("digilocker-sdk-button");

	    if (modal) {
	        modal.style.display = "block";
	    }

	    // 🔑 Clear old buttons before SDK re-renders
	    if (container) {
	        container.innerHTML = "";
	        container.style.display = "block"; // reset if hidden on success
	    }

	    // Initialize Digi SDK inside modal with token
	    window.DigiboostSdk({
	        gateway: "production", // sandbox or production
	        token: token, // directly from response
	        onSuccess: handleSuccess,
	        onFailure: handleFailure,
	        selector: "#digilocker-sdk-button",
	        style: {
	            width: "100%",
	            margin: "0px"
	        }
	    });
	}

	function closeModal() {
	    const modal = document.getElementById("digiModal");
	    const container = document.getElementById("digilocker-sdk-button");

	    if (modal) {
	        modal.style.display = "none";
	    }

	    // 🔑 Reset container on close too
	    if (container) {
	        container.innerHTML = "";
	        container.style.display = "block";
	    }
	}


	function handleSuccess(data) {
			//Do something here for success case
			console.log("Verification successful:", data);
	        document.getElementById("status").innerHTML = 
	            '<div class="success"> Verification completed successfully!</div>';
	            document.getElementById("digilocker-sdk-button").style.display = "none";
	        
	        //window.location.href = "/CWFM/contractworkmen/digiClientId?id=" + encodeURIComponent(data.client_id);
			
			var xhr = new XMLHttpRequest();
					    var url = "/CWFM/contractworkmen/digiClientId?id=" + encodeURIComponent(data.client_id);

					    xhr.open("GET", url, true);
					    xhr.setRequestHeader("Content-Type", "application/json");

					    xhr.onload = function () {
					        var response;
					        try {
					            response = JSON.parse(xhr.responseText);
					        } catch (e) {
					            document.getElementById("otpError").innerText = "Invalid response from server.";
					            document.getElementById("otpMessage").innerText = "";
					            return;
					        }

					        if (xhr.status === 200 || xhr.status === 422) {
								if (response.data) {
									  console.log(response.data.aadhaar_xml_data);
									  closeModal();
									  let firstname = response.data.aadhaar_xml_data.full_name ?.split(" ")[0] || "";
									  let lastname = response.data.aadhaar_xml_data.full_name?.split(" ").slice(1).join(" ") || "";
									  let dob = response.data.aadhaar_xml_data.dob|| "";
									 // let fathername = response.data.aadhaar_xml_data.care_of|| "";
									 let careOf = response.data.aadhaar_xml_data.care_of || "";
									 let match = careOf.match(/^\s*([A-Za-z]\/O:)?\s*(.*)$/i);
									 let relation = "";
									 let fathername = "";
									 if (match) {
									     relation = (match[1] || "").trim();  // e.g. "S/O:" or ""
									     fathername = (match[2] || "").trim(); // e.g. "Sankar Singh"
									 }
									  let g = response.data.aadhaar_xml_data.gender|| "";
									  let gender;
									  if(g === "F"){
										gender = "12";
									  }else{
										gender = "11";
									  }
									  let address= response.data.aadhaar_xml_data.full_address|| ""+" "+response.data.aadhaar_xml_data.zip|| "";
									  document.getElementById("firstName").value = firstname;
									   document.getElementById("lastName").value =lastname;
									  document.getElementById("dateOfBirth").value =dob;
									  	document.getElementById("gender").value = gender
									  document.getElementById("address").value =  address;
									  document.getElementById("relationName").value =  fathername;
									  
									  document.getElementById("aadharNumber").readOnly = false;
									  document.getElementById("firstName").readOnly = false;
									         
											 document.getElementById("dateOfBirth").disabled = false;

									         document.getElementById("gender").disabled = false;   // if it's a <select>
									         document.getElementById("address").readOnly = false;
									        
											 document.querySelector('button[onclick="generateToken()"]').disabled = true;
								}

					           
								 else {
					                document.getElementById("otpError").innerText = response.message +" "+response.status;
					                document.getElementById("otpMessage").innerText = "";
					            }
					        } else {
					            document.getElementById("otpError").innerText = "Error: " + (response.message || xhr.statusText);
					            document.getElementById("otpMessage").innerText = "";
					        }
					    };

					    xhr.onerror = function () {
					        document.getElementById("otpError").innerText = "Request failed.";
					        document.getElementById("otpMessage").innerText = "";
					    };

					   
					    xhr.send();	
			
		}

		function handleFailure() {
			//Do something here for failure case
			console.log("Verification failed:", error);
	        document.getElementById("status").innerHTML = 
	            '<div class="error"> Verification failed or was cancelled</div>';
		}
		

		
		function aadharDuplicateDBCheck(aadharNumber) {
		    let aadharCheckPassed = false;

			$.ajax({
				     url: "/CWFM/contractworkmen/checkAadharExistsCreation",
				     type: "GET",
				     data: {
				         aadharNumber: aadharNumber,
				         gatePassId: $("#gatePassId").val(),        // NULL for draft
				         transactionId: $("#transactionId").val()   // always present for draft/renewal
				     },
				     async: false,
				     success: function (response) {

						let status = response.status ? response.status.trim() : '';
						if ( status !== "Invalid" && status !== "" && status !== "FOUND") {
						    $("#error-aadhar").text(status).show();
						    aadharCheckPassed = false;
						} else if (status === "Invalid") {
						    $("#error-aadhar").text("Invalid Aadhar Number").show();
						    aadharCheckPassed = false;
						} else if (status === "FOUND") {

                          $("#error-aadhar").hide();

                          $("#firstName").val(response.firstName || "");
                          $("#lastName").val(response.lastName || "");
                          $("#relationName").val(response.relativeName || "");
                          $("#dateOfBirth").val(response.dob || "");
                          $("#gender").val(response.gender || "");
                          $("#mobileNumber").val(response.mobileNumber || "");
                          $("#maritalStatus").val(response.maritalStatus || "");
                          $("#disability").val(response.disability || "");
                          $("#workmenType").val(response.workmenType || "");
                          $("#address").val(response.address || "");

                           aadharCheckPassed = true;
                      }else {
						    $("#error-aadhar").hide();
						    aadharCheckPassed = true;
						}

				     },
				     error: function () {
				         $("#error-aadhar").text("Unable to verify Aadhaar").show();
				         aadharCheckPassed = false;
				     }
				 });

		    return aadharCheckPassed;  // ✅ return after ajax finishes
		}
		function downloadPreviousFile(transactionId, fileName) {
  if (!transactionId || !fileName) {
    alert("Missing transaction or file name.");
    return;
  }

  const url = `/CWFM/contractworkmen/downloadPreviousDoc?transactionId=${encodeURIComponent(transactionId)}&fileName=${encodeURIComponent(fileName)}`;

  fetch(url)
    .then(response => {
      if (!response.ok) {
        throw new Error("File not found or server error");
      }
      return response.blob();  // get file as blob
    })
    .then(blob => {
      const blobUrl = URL.createObjectURL(blob); // create secure blob link
      window.open(blobUrl, '_blank');            // open in new tab

      // optional cleanup after a short delay
      setTimeout(() => URL.revokeObjectURL(blobUrl), 30000);
    })
    .catch(err => {
      console.error("Error opening file:", err);
      alert("Unable to open file.");
    });
}
function onWcChange(selectElement) {

    // handle empty selection safely
    if (!selectElement || selectElement.selectedIndex === 0) {
        hideEsic();
        return;
    }


	const selectedOption = selectElement.options[selectElement.selectedIndex];
	    const licenceType = selectedOption.getAttribute("data-code");
    console.log("Licence Type:", licenceType);

    if (licenceType === "ESIC") {
        showEsic();
    } else {
        hideEsic();
    }
}

function showEsic() {
    document.getElementById("esicNumber").required = true;
    document.getElementById("esicNumberSection").style.display = "";
    document.getElementById("esicRequiredStar").style.display = "";
	//document.getElementById("error-esicNumber").style.display = "";
	$("#error-esicNumber").hide();
	
}

function hideEsic() {
    document.getElementById("esicNumber").required = false;
    document.getElementById("esicNumber").value = "";
    document.getElementById("error-esicNumber").style.display = "none";
    document.getElementById("esicNumberSection").style.display = "none";
    document.getElementById("esicRequiredStar").style.display = "none";
}


function searchGatePassReportBasedOnPE() {
					    var principalEmployerId = $('#principalEmployerId').val();
					    
						
					    $.ajax({
					        url: '/CWFM/entryPassStatus/report',
					        type: 'POST',
					        data: {
					            principalEmployerId: principalEmployerId
					        },
					        success: function(response) {
					            var tableBody = $('#workmenTable tbody');
								// 🔄 Clear previous DataTable and its config
								           if ($.fn.DataTable.isDataTable('#workmenTable')) {
								               $('#workmenTable').DataTable().destroy();
								           }
										   tableBody.empty();
					            if (Array.isArray(response) &&response.length > 0) {
					                $.each(response, function(index, wo) {
										var entryPassTypeFormatted = wo.entryPassType? toCapitalCase(String(wo.entryPassType)): '';
					                    var row = '<tr  >' +
												'<td  ><input type="checkbox" name="selectedUnitIds" value="' + wo.transactionId + '"></td>'+
												'<td  >' + wo.transactionId + '</td>' +
												 '<td  >' + wo.entryPassNo + '</td>' +
					                             /* '<td  >' + wo.contractWorkmenCode+'</td>' +*/
												   '<td  >' +wo.firstName + '</td>' +
												  '<td  >' + wo.lastName + '</td>' +	
												  '<td  >' + wo.department + '</td>' +	
												  '<td  >' +wo.vendorCode + '</td>' +	
												  '<td  >' + wo.vendorName + '</td>' +
												  '<td  >' + wo.workOrder + '</td>' +
												  '<td  >' + wo.eicNumber + '</td>' +
												  '<td  >' + wo.entryPassAction + '</td>' +
												  '<td  >' + entryPassTypeFormatted + '</td>' +
												  '<td  >' + wo.lastApprover + '</td>' +
												  '<td  >' + wo.nextApprover + '</td>' +
												  '<td  >' + wo.status + '</td>' +				                             
					                              '</tr>';
					                    tableBody.append(row);
					                });
									
					            } 								

																	            // ✅ Always init after rows are drawn
																	            initWorkmenTable("workmenTable");
																	           //reinitializeDataTable('#workmenTable');
																	        },
																	        
					       
					        error: function(xhr, status, error) {
					            console.error("Error fetching data:", error);
					        }
					    });
					}
					function toggleSelectAll() {
					           var selectAllCheckbox = document.getElementById('selectAllCheckbox');
					           var checkboxes = document.querySelectorAll('input[name="selectedUnitIds"]');
					           checkboxes.forEach(function(checkbox) {
					               checkbox.checked = selectAllCheckbox.checked;
					           });
					       }
						   
						   function exportToEntryPassStatusCSV() {
						       var selectedRows = document.querySelectorAll('input[name="selectedUnitIds"]:checked');

						       if (selectedRows.length === 0) {
						           alert("Please select at least one record to export.");
						           return;
						       }

						       var csvContent = "data:text/csv;charset=utf-8,";
						       csvContent += "Transaction ID,Entry Pass No,Contract Workmen Code,First Name,Last Name,Department,Vendor Code,Vendor Name,Workorder,Eic Number,Entry Pass Action,Entry Pass Type,Last Approver,Next Approver,Status\n";

						       selectedRows.forEach(function (checkbox) {
						           var row = checkbox.closest("tr");

						           var rowData = row.querySelectorAll(
						               "td:nth-child(2), td:nth-child(3), td:nth-child(4), td:nth-child(5), td:nth-child(6), td:nth-child(7), td:nth-child(8), td:nth-child(9), td:nth-child(10), td:nth-child(11), td:nth-child(12), td:nth-child(13), td:nth-child(14), td:nth-child(15), td:nth-child(16)"
						           );

						           var rowArray = [];
						           rowData.forEach(function (cell) {
						               rowArray.push('"' + cell.innerText.replace(/"/g, '""') + '"');
						           });

						           csvContent += rowArray.join(",") + "\n";
						       });

						       var encodedUri = encodeURI(csvContent);
						       var link = document.createElement("a");
						       link.setAttribute("href", encodedUri);
						       link.setAttribute("download", "EntryPassStatus.csv");
						       document.body.appendChild(link);
						       link.click();
						       document.body.removeChild(link);
						   }
						   
						   function redirectToWorkmenProjectAdd() {
						   console.log("redirectToWorkmenProjectAdd called");
						       // Fetch the content of add.jsp using AJAX
						       var xhr = new XMLHttpRequest();
						       xhr.onreadystatechange = function() {
						           if (xhr.readyState == 4 && xhr.status == 200) {
						               // Update the mainContent element with the fetched content
						               document.getElementById("mainContent").innerHTML = xhr.responseText;
						   			setDateRange();
									initializeAutoSelects();									
						           }
						       };
						       xhr.open("GET", "/CWFM/contractworkmen/projectOnboardingCreation", true);
						       xhr.send();
						   }
						   function goBackToProjectOnboardingList() {
						      	 loadCommonList('/contractworkmen/projectOnboardingList', 'Project Gatepass List');
						      }
							  function validateProjectEmploymentInformation(){
							  	let isValid = true;
							      const principalEmp = $("#principalEmployer").val();
							       if (principalEmp === "") {
							          $("#error-principalEmployer").show();
							          isValid = false;
							      }else{
							  		$("#error-principalEmployer").hide();
							  	}
							  	const cont = $("#contractor").val();
							       if (cont === "") {
							          $("#error-contractor").show();
							          isValid = false;
							      }else{
							  		$("#error-contractor").hide();
							  	}
							  	const wo = $("#workorder").val();
							       if (wo === "") {
							          $("#error-workorder").show();
							          isValid = false;
							      }else{
							  		$("#error-workorder").hide();
							  	}
							  	const trade = $("#trade").val();
							       if (trade === "") {
							          $("#error-trade").show();
							          isValid = false;
							      }else{
							  		$("#error-trade").hide();
							  	}
							  	const skill = $("#skill").val();
							       if (skill === "") {
							          $("#error-skill").show();
							          isValid = false;
							      }else{
							  		$("#error-skill").hide();
							  	}
							  	/*const proficiency = $("#proficiency").val();
                                  if (proficiency === "") {
                                   $("#error-proficiency").show();
                                    isValid = false;
                                    }else{
	                              	$("#error-proficiency").hide();
	                             }*/
							  	const dept = $("#department").val();
							       if (dept === "") {
							          $("#error-department").show();
							          isValid = false;
							      }else{
							  		$("#error-department").hide();
							  	}
							  	const subdept = $("#subdepartment").val();
							       if (subdept === "") {
							          $("#error-area").show();
							          isValid = false;
							      }else{
							  		$("#error-area").hide();
							  	}
							  	const eic = $("#eic").val();
							       if (eic === "") {
							          $("#error-eic").show();
							          isValid = false;
							      }else{
							  		$("#error-eic").hide();
							  	}
							  	 
								const wc = $("#wc").val();
								     if (wc === "") {
								        $("#error-wc").show();
								        isValid = false;
								    }else{
										$("#error-wc").hide();
									}
									
									const selectedOption = $("#wc").find(":selected");
									//const licenceType = selectedOption.data("licencetype");
									const licenceType = selectedOption.attr("data-code");
									const esicNumber = $("#esicNumber").val().trim();

									if (licenceType === "ESIC") {
								showEsic();
								    if ( esicNumber === "") {
								        $("#error-esicNumber").show();
								        isValid = false;
								    } else {
								        $("#error-esicNumber").hide(); // ✅ hides properly
								    }

								} else {
								    $("#error-esicNumber").hide(); // ✅ hides properly
								}
							  	return isValid;
							  }	
/*function aadharValidation(){
	let aadharCheckPassed = false;
	    const aadharNumber = $("#aadharNumber").val().trim();

		//const transactionId=$("#transactionId").val().trim();
	    if (aadharNumber === "" || aadharNumber.length !== 12 || isNaN(aadharNumber)) {

	        $("#error-aadhar").show();
	        isValid = false;
	    }else{
			 // $("#error-aadhar").hide();
			 $.ajax({
			 				     url: "/CWFM/contractworkmen/checkAadharExistsCreation",
			 				     type: "GET",
			 				     data: {
			 				         aadharNumber: aadharNumber,
			 				         gatePassId: $("#gatePassId").val(),        // NULL for draft
			 				         transactionId: $("#transactionId").val()   // always present for draft/renewal
			 				     },
			 				     async: false,
			 				     success: function (response) {

			 						let status = response.status ? response.status.trim() : '';
			 						if ( status !== "Invalid" && status !== "" && status !== "FOUND") {
			 						    $("#error-aadhar").text(status).show();
			 						    aadharCheckPassed = false;
			 						} else if (status === "Invalid") {
			 						    $("#error-aadhar").text("Invalid Aadhar Number").show();
			 						    aadharCheckPassed = false;
			 						} else if (status === "FOUND") {

                          $("#error-aadhar").hide();

                          $("#firstName").val(response.firstName || "");
                          $("#lastName").val(response.lastName || "");
                          $("#relationName").val(response.relativeName || "");
                          $("#dateOfBirth").val(response.dob || "");
                          $("#gender").val(response.gender || "");
                          $("#mobileNumber").val(response.mobileNumber || "");
                          $("#maritalStatus").val(response.maritalStatus || "");
                          $("#disability").val(response.disability || "");
                          $("#workmenType").val(response.workmenType || "");
                          $("#address").val(response.address || "");

                           aadharCheckPassed = true;
                      }else {
			 						    $("#error-aadhar").hide();
			 						    aadharCheckPassed = true;
			 						}

			 				     },
			 				     error: function () {
			 				         $("#error-aadhar").text("Unable to verify Aadhaar").show();
			 				         aadharCheckPassed = false;
			 				     }
			 				 });


			     }
				 return aadharCheckPassed;
}*/

function aadharValidation() {

    let aadharCheckPassed = false;

    const aadharNumber = $("#aadharNumber").val().trim();
    // EMPTY / INVALID LENGTH / NON-NUMERIC AADHAAR
    if (aadharNumber === "" || aadharNumber.length !== 12 || isNaN(aadharNumber)) {
        $("#error-aadhar").text("Invalid Aadhar Number").show();
        // New/invalid Aadhaar -> fields editable
        unlockAadharFields();
        isValid = false;

    } else {
        $.ajax({
            url: "/CWFM/contractworkmen/checkAadharExistsCreation",
            type: "GET",
            data: {
                aadharNumber: aadharNumber,
                gatePassId: $("#gatePassId").val(),
                transactionId: $("#transactionId").val()
            },
            async: false,
            success: function (response) {
                let status = response.status ? response.status.trim(): '';
                // SOME OTHER VALIDATION ERROR FROM BACKEND
                if (status !== "Invalid" && status !== "" && status !== "FOUND") {
                    $("#error-aadhar").text(status).show();
                    // Keep fields editable
                    unlockAadharFields();
                    aadharCheckPassed = false;
                }
                // INVALID AADHAAR
                else if (status === "Invalid") {
                    $("#error-aadhar").text("Invalid Aadhar Number").show();
                    unlockAadharFields();
                    aadharCheckPassed = false;
                }
                // EXISTING / CANCELLED AADHAAR FOUND
                else if (status === "FOUND") {
                    $("#error-aadhar").hide();
                    // POPULATE DETAILS FROM EXISTING RECORD
                    $("#firstName").val(response.firstName || "");
                    $("#lastName").val(response.lastName || "");
                    $("#relationName").val(response.relativeName || "");
                    $("#dateOfBirth").val(response.dob || "");
                    $("#gender").val(response.gender || "");
                    $("#mobileNumber").val(response.mobileNumber || "");
                    $("#maritalStatus").val(response.maritalStatus || "");
                    $("#disability").val(response.disability || "");
                    $("#workmenType").val(response.workmenType || "");
                    $("#address").val(response.address || "");
                    // LOCK ONLY FIRST NAME, DOB AND GENDER
                    lockExistingAadharFields();
                    aadharCheckPassed = true;
                }
                // NEW AADHAAR
                else {
                    $("#error-aadhar").hide();
                    // New Aadhaar -> everything editable
                    unlockAadharFields();
                    aadharCheckPassed = true;
                }
            },
            error: function () {
                $("#error-aadhar").text("Unable to verify Aadhaar").show();
                // Verification failed -> don't lock fields
                unlockAadharFields();
                aadharCheckPassed = false;
            }
        });
    }
    return aadharCheckPassed;
}

function showLoader() {
    document.getElementById("loaderOverlay").style.display = "flex";
}

function hideLoader() {
    document.getElementById("loaderOverlay").style.display = "none";
}



function uploadAadhaarFile() {

    showLoader();

    const fileInput = document.getElementById("aadhaarFile");

	
    if (!fileInput.files.length) {
        hideLoader();
        alert("Please select Aadhaar file");
        return;
    }

    const formData = new FormData();
    formData.append("file", fileInput.files[0]);

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/CWFM/contractworkmen/aadhaarOCRValidate", true);

    xhr.onload = function () {

        let response;
        try {
            response = JSON.parse(xhr.responseText);
        } catch (e) {
            hideLoader();
            document.getElementById("otpError").innerText = "Invalid server response";
            return;
        }

        if ((xhr.status === 200 || xhr.status === 422) && response.success) {

            if (!response.data || !response.data.ocr_fields) {
                hideLoader();
                document.getElementById("otpError").innerText = "No OCR data found";
                return;
            }

            // ✅ Read existing values (important merge fix)
            let firstname = document.getElementById("firstName").value || "";
            let lastname = document.getElementById("lastName").value || "";
            let dob = document.getElementById("dateOfBirth").value || "";
            let gender = document.getElementById("gender").value || "";
            let fathername = document.getElementById("relationName").value || "";
            let address = document.getElementById("address").value || "";
            let aadharNumber = document.getElementById("aadharNumber").value || "";

            // ✅ Loop OCR blocks (front/back/combined safe)
            response.data.ocr_fields.forEach(ocr => {

                const type = ocr.document_type;

                // FRONT SIDE
                if (type === "aadhaar_front_bottom") {

                    const fullName = ocr.full_name?.value;
                    if (fullName) {
                        firstname = fullName.split(" ")[0] || firstname;
                        lastname = fullName.split(" ").slice(1).join(" ") || lastname;
                    }

                    if (ocr.dob?.value) dob = ocr.dob.value;

                    let g = ocr.gender?.value;
                    if (g) gender = (g === "F") ? "12" : "11";
                }

                // BACK SIDE
                if (type === "aadhaar_back") {

                    if (ocr.care_of?.value)
                        fathername = ocr.care_of.value;

                    if (ocr.address?.value || ocr.zip?.value)
                        address = (ocr.address?.value || "") + " " + (ocr.zip?.value || "");
                }

                // Aadhaar number (common)
                if (ocr.aadhaar_number?.value)
                    aadharNumber = ocr.aadhaar_number.value;
            });

            // ✅ Populate merged values
            document.getElementById("aadharNumber").value = aadharNumber;
            document.getElementById("firstName").value = firstname;
            document.getElementById("lastName").value = lastname;
            document.getElementById("dateOfBirth").value = dob;
            document.getElementById("gender").value = gender;
            document.getElementById("address").value = address;
            document.getElementById("relationName").value = fathername;

            // ✅ Lock fields
            document.getElementById("aadharNumber").readOnly = false;
            document.getElementById("firstName").readOnly = false;
            document.getElementById("dateOfBirth").disabled = false;
            document.getElementById("gender").disabled = false;
            document.getElementById("address").readOnly = false;

            

            hideLoader();

        } else {
            hideLoader();
            document.getElementById("otpError").innerText =
                response.message || "Validation failed";
            document.getElementById("otpMessage").innerText = "";
        }
    };

    xhr.onerror = function () {
        hideLoader();
        document.getElementById("otpError").innerText = "Request failed.";
        document.getElementById("otpMessage").innerText = "";
    };

    xhr.send(formData);
}

function showSelectedFileName() {
    const fileInput = document.getElementById("aadhaarFile");
    const label = document.getElementById("selectedFileName");

    if (fileInput.files.length > 0) {
        label.innerText = "Selected: " + fileInput.files[0].name;
    } else {
        label.innerText = "";
    }
}
function toggleAll(source) {
    document.querySelectorAll(".bulk-check").forEach(cb => {
        cb.checked = source.checked;
    });
}
function bulkApprove(status) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
	const commentsVal = ($("#approvercomments").val() || "").trim();
    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/bulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.text())
    .then(msg => {
        hideLoader();
        sessionStorage.setItem("successMessage", msg);
        loadCommonList('/contractworkmen/blockListFilter', 'Block List');
    })
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}
/*function bulkApprove(status) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
    const commentsVal = ($("#approvercomments").val() || "").trim();

    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/bulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.json())
.then(data => {

    hideLoader();

    let successMsg = "";
    let errorMsg = "";

    if (data.successList && data.successList.length > 0) {
        successMsg = data.successList.join("\n");
    }

    if (data.errorList && data.errorList.length > 0) {
        errorMsg = data.errorList.join("\n");
    }

    // ✅ Case 1: Errors present → show in RED
    if (errorMsg) {

        $("#errorMessageBox").text(errorMsg).show();   // RED UI
        $("#successMessageBox").hide();

    }

    // ✅ Case 2: Only success → show in GREEN
    if (successMsg && !errorMsg) {

        sessionStorage.setItem("successMessage", successMsg);

    }

    // ✅ Case 3: Mixed (both success + error)
    if (successMsg && errorMsg) {

        sessionStorage.setItem("successMessage", successMsg);

        $("#errorMessageBox").text(errorMsg).show();   // show errors also

    }

    loadCommonList('/contractworkmen/blockListFilter', 'Block List');

})
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}*/
function validateProjectFiles(aadharFile, policeFile, profilePc,appointmentFile) {
    let valid = true;

    // Aadhar File - Mandatory & Size check
    if (!aadharFile) {
        $("#aadharError").text("Aadhar file is required").addClass("error-bold");
        valid = false;
    } else if (aadharFile.size > 5 * 1024 * 1024) {
        $("#aadharError").text("Aadhar file must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#aadharError").text("");
    }

    // Police Verification File - Mandatory & Size check
    if (!policeFile) {
        $("#policeError").text("Police verification file is required").addClass("error-bold");
        valid = false;
    } else if (policeFile.size > 5 * 1024 * 1024) {
        $("#policeError").text("Police file must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#policeError").text("");
    }
// ✅ Appointment File - OPTIONAL (only size validation)
   if (appointmentFile) {
        if (appointmentFile.size > 5 * 1024 * 1024) {
            $("#appointmentError").text("Appointment must be less than 5MB").addClass("error-bold");
            valid = false;
        } else {
            $("#appointmentError").text("");
        }
    } else {
        $("#appointmentError").text(""); // clear error if not uploaded
    }
    
// Police Verification Date - Mandatory  check
const policeVerificationDate = $("#policeVerificationDate").val().trim();
    if (policeVerificationDate === "") {
        $("#error-policeVerificationDate").show();
        valid = false;
    }else {
        $("#error-policeVerificationDate").hide("");
    }
    
    // Profile Photo - Mandatory & Size check
    if (!profilePc) {
        $("#profilePcError").text("Profile photo is required").addClass("error-bold");
        valid = false;
    } else if (profilePc.size > 5 * 1024 * 1024) {
        $("#profilePcError").text("Photo/Image must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#profilePcError").text("");
    }

	const comments = $("#comments").val().trim();
		    if (comments === "") {
		        $("#error-comments").show();
		        valid = false;
		    }else{
				 $("#error-comments").hide();
			}
			// Accept Checkbox validation
			    if (!$("#acceptCheck").is(":checked")) {
			        $("#acceptError").show();
			        valid = false;
			    } else {
			        $("#acceptError").hide();
			    }	
			    if (valid) {
    $("#docTabGlobalError").hide();
}			
    return valid;
}
function blackListBulkApprove(status) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
	const commentsVal = ($("#approvercomments").val() || "").trim();
    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/bulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.text())
    .then(msg => {
        hideLoader();
        sessionStorage.setItem("successMessage", msg);
         loadCommonList('/contractworkmen/blackListFilter', 'Black List');
    })
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}
function cancelBulkApprove(status) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
	const commentsVal = ($("#approvercomments").val() || "").trim();
    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/bulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.text())
    .then(msg => {
        hideLoader();
        sessionStorage.setItem("successMessage", msg);
         loadCommonList('/contractworkmen/cancelFilter', 'Cancel List');
    })
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}
function unblockBulkApprove(status) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
	const commentsVal = ($("#approvercomments").val() || "").trim();
    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/bulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.text())
    .then(msg => {
        hideLoader();
        sessionStorage.setItem("successMessage", msg);
         loadCommonList('/contractworkmen/unblockListFilter', 'Unblock List');
    })
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}
function deblackListBulkApprove(status) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
	const commentsVal = ($("#approvercomments").val() || "").trim();
    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/bulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.text())
    .then(msg => {
        hideLoader();
        sessionStorage.setItem("successMessage", msg);
         loadCommonList('/contractworkmen/deblackListFilter', 'Deblack List');
    })
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}
/*function renewBulkApprove(status) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
	const commentsVal = ($("#approvercomments").val() || "").trim();
    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/bulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.text())
    .then(msg => {
        hideLoader();
        sessionStorage.setItem("successMessage", msg);
          loadCommonList('/contractworkmen/renewFilter', 'Renew List');
    })
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}*/
/*function createBulkApprove(status,type) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
	const commentsVal = ($("#approvercomments").val() || "").trim();
    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/bulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.text())
    .then(msg => {
        hideLoader();
        sessionStorage.setItem("successMessage", msg);
        if(type=== "regular"){
                    loadCommonList('/contractworkmen/list', 'On-Boarding List');
                    //hideLoader();
                }else if(type=== "quick"){
                    loadCommonList('/contractworkmen/quickOnboardingList', 'Quick Onboarding List');
                   // hideLoader();
                }else{
					loadCommonList('/contractworkmen/projectOnboardingList', 'Project Gatepass List');
					//hideLoader();
				}
    })
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}*/
function renewBulkApprove(status) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
    const commentsVal = ($("#approvercomments").val() || "").trim();

    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/createBulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.json())
    .then(data => {

        hideLoader();

       
        if (data.errorList && data.errorList.length > 0) {
            sessionStorage.setItem("bulkErrorMessages", JSON.stringify(data.errorList));
          } else {
           sessionStorage.removeItem("bulkErrorMessages"); // important for success case
         }

        // 🔄 Reload list
        loadCommonList('/contractworkmen/renewFilter', 'Renew List');

        // ✅ 🔥 IMPORTANT: Re-render AFTER reload (no document.ready)
        setTimeout(function () {
            renderBulkErrors();
        }, 800); // wait for DOM reload

    })
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}
function createBulkApprove(status, type) {

    const selected = document.querySelectorAll(".bulk-check:checked");

    if (selected.length === 0) {
        alert("Please select at least one record");
        return;
    }

    const records = [];
    const commentsVal = ($("#approvercomments").val() || "").trim();

    selected.forEach(cb => {
        records.push({
            transactionId: cb.dataset.transaction,
            gatePassId: cb.dataset.gatepass,
            gatePassType: cb.dataset.type,
            approverId: $("#userId").val(),
            approverRole: $("#roleName").val(),
            roleId: $("#roleId").val(),
            comments: commentsVal,
            status: status
        });
    });

    showLoader();

    fetch("/CWFM/contractworkmen/createBulkApproveGatePass", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(records)
    })
    .then(res => res.json())
    .then(data => {

        hideLoader();

        // ✅ Store ONLY errors
        /*if (data.errorList && data.errorList.length > 0) {
            sessionStorage.setItem("bulkErrorMessages", JSON.stringify(data.errorList));
        } else {
            sessionStorage.removeItem("bulkErrorMessages");
        }*/
        if (data.errorList && data.errorList.length > 0) {
            sessionStorage.setItem("bulkErrorMessages", JSON.stringify(data.errorList));
          } else {
           sessionStorage.removeItem("bulkErrorMessages"); // important for success case
         }

        // 🔄 Reload list
        if (type === "regular") {
            loadCommonList('/contractworkmen/list', 'On-Boarding List');
        } else if (type === "quick") {
            loadCommonList('/contractworkmen/quickOnboardingList', 'Quick Onboarding List');
        } else {
            loadCommonList('/contractworkmen/projectOnboardingList', 'Project Gatepass List');
        }

        // ✅ 🔥 IMPORTANT: Re-render AFTER reload (no document.ready)
        setTimeout(function () {
            renderBulkErrors();
        }, 800); // wait for DOM reload

    })
    .catch(err => {
        hideLoader();
        alert("Bulk approval failed");
        console.error(err);
    });
}
/*function renderBulkErrors() {

    // Remove old error table if already exists
    $("#bulkErrorContainer").remove();

    const errorData = sessionStorage.getItem("bulkErrorMessages");

    if (!errorData) return;

    const errors = JSON.parse(errorData);

    if (!errors || errors.length === 0) return;

    // ✅ Create container
    let html = `
        <div id="bulkErrorContainer" style="margin-bottom:15px;">
            <table class="table table-bordered" style="background:#ffe6e6; width:100%;">
                <thead>
                    <tr style="background:#f5c6cb; color:#721c24;">
                        <th style="width:200px;">TransactionId</th>
                        <th>Error</th>
                    </tr>
                </thead>
                <tbody>
    `;

    // ✅ Fill rows
    errors.forEach(err => {

        let transactionId = "-";
        let message = err;

        const match = err.match(/TransactionId\s*(\d+)\s*:\s*(.*)/i);

        if (match) {
            transactionId = match[1];
            message = match[2];
        }

        html += `
            <tr>
                <td style="color:red; font-weight:bold;">${transactionId}</td>
                <td style="color:red;">${message}</td>
            </tr>
        `;
    });

    html += `
                </tbody>
            </table>
        </div>
    `;

    // ✅ Inject ABOVE main table (change selector if needed)
    $("#workmenTable").before(html);
}*/
function renderBulkErrors() {

    // Remove old containers
    $("#bulkErrorContainer").remove();
    $("#bulkSuccessMsg").remove();

    const errorData = sessionStorage.getItem("bulkErrorMessages");

    // ✅ NO ERRORS → SUCCESS MESSAGE
    if (!errorData) {
        $("#workmenTable").before(
            `<div style="color:green; font-weight:bold;" id="bulkSuccessMsg">
                Bulk approved successfully
            </div>`
        );
        return;
    }

    const errors = JSON.parse(errorData);

    if (!errors || errors.length === 0) {
        $("#workmenTable").before(
            `<div style="color:green; font-weight:bold;" id="bulkSuccessMsg">
                Bulk approved successfully
            </div>`
        );
        return;
    }

    // ✅ ERROR TABLE WITH SCROLL
    let html = `
        <div id="bulkErrorContainer" style="margin-bottom:15px;">

            <div style="
                max-height:200px;
                overflow-y:auto;
                overflow-x:hidden;
                border:1px solid #ddd;
                border-radius:5px;
            ">

                <table class="table table-bordered" 
                       style="width:100%; margin-bottom:0; table-layout:fixed;">

                    <thead style="background:#f8f9fa; position:sticky; top:0; z-index:1;">
                        <tr>
                            <th style="width:200px;">Transaction Id</th>
                            <th>Error</th>
                        </tr>
                    </thead>

                    <tbody>
    `;

    errors.forEach(err => {

        let transactionId = "-";
        let message = err;

        const match = err.match(/TransactionId\s*(\d+)\s*:\s*(.*)/i);

        if (match) {
            transactionId = match[1];
            message = match[2];
        }

        html += `
            <tr>
                <td style="color:red; font-weight:bold; word-break:break-word;">
                    ${transactionId}
                </td>
                <td style="color:red; word-break:break-word;">
                    ${message}
                </td>
            </tr>
        `;
    });

    html += `
                    </tbody>
                </table>
            </div>
        </div>
    `;

    $("#workmenTable").before(html);
}
function validateMinimumWage() {

    let isValid = true;

    $("#MinimumWageError").hide().text("");

    const principalEmp = $("#principalEmployer").val();
    const zone = $("#zone option:selected").text().trim();
    const skill = $("#skill").val();
    const wageType = $("#wageCategory option:selected").text().trim().toLowerCase();

    const selectedOption = $("#wc").find(":selected");
    const licenceType = selectedOption.attr("data-code");

    // ENTERED WAGE VALUES
    const basic = parseFloat($("#basic").val()) || 0;
    const da = parseFloat($("#da").val()) || 0;
    const hra = parseFloat($("#hra").val()) || 0;
    const washing = parseFloat($("#washingAllowance").val()) || 0;
    const other = parseFloat($("#otherAllowance").val()) || 0;
    const uniform = parseFloat($("#uniformAllowance").val()) || 0;

    // Basic + DA
    const basicDa = basic + da;

    // Total entered wage
    const enteredWage = basic + da + hra + washing + other + uniform;

    // 50% of entered wage
    const fiftyPercentWage = enteredWage * 0.50;

    // AJAX - GET STATE MINIMUM WAGE
    $.ajax({
        url: "/CWFM/contractworkmen/getMinimumWageDetails",
        type: "GET",
        async: false,
        data: {
            principalEmployer: principalEmp,
            zone: zone,
            skill: skill
        },

        success: function(response) {
            // MESSAGES
            const minWageMsg = $("#msg-minimumWage").text();
            const esicMonthlyMsg = $("#msg-esicMonthly").text();
            const esicDailyMsg = $("#msg-esicDaily").text();
            const notFoundMsg = $("#msg-minimumWageNotFound").text();

            // Message for Basic + DA 50% validation
            const basicDa50PercentMsg = $("#msg-basicDa50Percent").text();

            // Message for ESIC Daily Basic + DA validation
            const esicDailyBasicDaMsg = $("#msg-esicDailyBasicDa").text();
            // MINIMUM WAGE NOT CONFIGURED
            if (!response || response.basic == null) {

                $("#MinimumWageError").text(notFoundMsg).show();
                isValid = false;
                return;
            }
            
            // STATE MINIMUM WAGE
            const stateBasic = parseFloat(response.basic) || 0;
            const stateDa = parseFloat(response.da) || 0;
            const stateOther = parseFloat(response.otherAllowance) || 0;

            // Daily minimum wage
            const stateMinimum = stateBasic + stateDa + stateOther;

            // Monthly minimum wage
            const monthlyStateMinimum = stateMinimum * 26;

            // ESIC MONTHLY
            if (licenceType === "ESIC" && wageType === "monthly") {
                // 1. Basic + DA <= 21000
                const esicMonthlyBasicDaValid = basicDa <= 21000;
                if (!esicMonthlyBasicDaValid) {
                    $("#MinimumWageError").text(esicMonthlyMsg).show();
                    isValid = false;
                    return;
                }
                
                // 2. Entered Wage >= Monthly State Minimum
                const monthlyMinimumWageValid = enteredWage >= monthlyStateMinimum;
                if (!monthlyMinimumWageValid) {
                    $("#MinimumWageError").text(minWageMsg).show();
                    isValid = false;
                    return;
                }
                
                // 3. Basic + DA >= 50% of Entered Wage
                const basicDaFiftyPercentValid = basicDa >= fiftyPercentWage;
                if (!basicDaFiftyPercentValid) {
                    $("#MinimumWageError").text(basicDa50PercentMsg).show();
                    isValid = false;
                    return;
                }
            }

            // WC MONTHLY
            if (licenceType === "WC" && wageType === "monthly") {
                // 1. Entered Wage >= Monthly State Minimum
                const monthlyMinimumWageValid = enteredWage >= monthlyStateMinimum;
                if (!monthlyMinimumWageValid) {
                    $("#MinimumWageError").text(minWageMsg).show();
                    isValid = false;
                    return;
                }
                
                // 2. Basic + DA >= 50% of Entered Wage
                const basicDaFiftyPercentValid = basicDa >= fiftyPercentWage;

                if (!basicDaFiftyPercentValid) {
                    $("#MinimumWageError").text(basicDa50PercentMsg).show();
                    isValid = false;
                    return;
                }
            }

            // ESIC DAILY
            if (licenceType === "ESIC" && wageType === "daily") {
                // 1. Basic + DA * 26 <= 21000
                const esicDailyBasicDaValid = (basicDa * 26) <= 21000;
                if (!esicDailyBasicDaValid) {
                    const monthlyBasicDa = basicDa * 26;
                    $("#MinimumWageError").text(esicDailyBasicDaMsg).show();
                    isValid = false;
                    return;
                }

                // 2. Entered Wage >= Daily State Minimum
                const dailyMinimumWageValid = enteredWage >= stateMinimum;
                if (!dailyMinimumWageValid) {
                    $("#MinimumWageError").text(minWageMsg).show();
                    isValid = false;
                    return;
                }

                // 3. Basic + DA >= 50% of Entered Wage
                const basicDaFiftyPercentValid = basicDa >= fiftyPercentWage;
                if (!basicDaFiftyPercentValid) {
                    $("#MinimumWageError").text(basicDa50PercentMsg).show();
                    isValid = false;
                    return;
                }
            }

            // WC DAILY
            if (licenceType === "WC" && wageType === "daily") {
                // 1. Entered Wage >= Daily State Minimum
                const dailyMinimumWageValid = enteredWage >= stateMinimum;
                if (!dailyMinimumWageValid) {
                    $("#MinimumWageError").text(minWageMsg).show();
                    isValid = false;
                    return;
                }

                // 2. Basic + DA >= 50% of Entered Wage
                const basicDaFiftyPercentValid = basicDa >= fiftyPercentWage;
                if (!basicDaFiftyPercentValid) {
                    $("#MinimumWageError").text(basicDa50PercentMsg).show();
                    isValid = false;
                    return;
                }
            }

        },

        error: function() {
            alert("Error while fetching wage details");
            isValid = false;
        }
    });
    return isValid;
}
function getZones(unitId) {
    var xhr = new XMLHttpRequest();
    var url = contextPath + "/contractworkmen/getAllZonesBasedOnUnitId?unitId=" + unitId;
    console.log("Fetching Zones from URL:", url);
    xhr.open("GET", url, true);

    xhr.onload = function () {
        if (xhr.status === 200) {
            var zone = JSON.parse(xhr.responseText);
            console.log("Zones:", zone);
            var zoneSelect = document.getElementById("zone");

            // Clear existing options
            zoneSelect.innerHTML = '<option value="">Please select Zone</option>';

            // Populate the trade dropdown
            zone.forEach(function (zone) {
                var option = document.createElement("option");
                option.value = zone.zoneId;
                option.text = zone.zoneName;
                zoneSelect.appendChild(option);
            });
			autoSelectAndTrigger("zone", null, false);
        } else {
            console.error("Error fetching zones:", xhr.statusText);
        }
    };

    xhr.onerror = function () {
        console.error("Request failed while fetching zones");
    };

    xhr.send();
}
function openFile(inputId) {
    document.getElementById(inputId).click();
}

function renderAdditionalDocuments(map) {

    console.log("Rendering Additional Docs:", map);

    $("#additionalDoc").empty();

    if (map && Object.keys(map).length > 0) {

        Object.entries(map).forEach(([key, value]) => {

            let formattedType =
                key.charAt(0).toUpperCase() + key.slice(1);

            additionalDocUpload(formattedType, value);
        });
    }
}
function loadProfilePreview(profileFileName, userId, transactionId) {

    console.log("Profile:", profileFileName);

    if (!profileFileName || profileFileName === "null" || profileFileName === "") {
        return;
    }

    // ✅ ENCODE FILE NAME (fix for spaces/special chars)
    var imagePath = "/CWFM/contractworkmen/getProfileImage?userId=" + userId +
                    "&transactionId=" + transactionId +
                    "&fileName=" + encodeURIComponent(profileFileName);

    let previewDiv = document.getElementById("preview");

    if (previewDiv) {
        previewDiv.innerHTML =
            '<img src="' + imagePath + '" style="width:100%; height:100%; object-fit:cover;" />';
    }
}
function uploadChallan(reconType) {
    var fileInput = reconType === 'PF' ? $("#pfFile")[0] : $("#esicFile")[0];

    if (!fileInput.files || fileInput.files.length === 0) {
        showMessage("Please select " + reconType + " challan file.", "danger",reconType);
        return;
    }

    var formData = new FormData();
    formData.append("reconType", reconType);
    formData.append("file", fileInput.files[0]);

    $.ajax({
        url: "/CWFM/contractor/reconciliation/upload",
        type: "POST",
        data: formData,
        processData: false,
        contentType: false,
        success: function(response) {
            if (response.status === "success") {
                var data = response.data;
                bindSummary(reconType, data);
                bindMismatch(reconType, data.mismatchList);

                if (data.status === "VERIFIED") {
                    showMessage(reconType + " reconciliation verified successfully.", "success",reconType);
                } else {
                    showMessage(reconType + " reconciliation completed with mismatches.", "warning",reconType);
                }
            } else {
                showMessage(response.message || "Error while processing reconciliation.", "danger",reconType);
            }
        },
        error: function() {
            showMessage("Error while uploading " + reconType + " challan.", "danger",reconType);
        }
    });
}

function bindSummary(type, data) {
    if (type === 'PF') {
        $("#pfSummary").show();
        $("#pfStatus").text(nullSafe(data.status));
        $("#pfTotal").text(nullSafe(data.totalCount));
        $("#pfVerified").text(nullSafe(data.verifiedCount));
        $("#pfUnverified").text(nullSafe(data.unverifiedCount));
    } else {
        $("#esicSummary").show();
        $("#esicStatus").text(nullSafe(data.status));
        $("#esicTotal").text(nullSafe(data.totalCount));
        $("#esicVerified").text(nullSafe(data.verifiedCount));
        $("#esicUnverified").text(nullSafe(data.unverifiedCount));
    }
}

function bindMismatch(type, list) {
    var bodyId = type === 'PF' ? "#pfMismatchBody" : "#esicMismatchBody";
    var sectionId = type === 'PF' ? "#pfMismatchSection" : "#esicMismatchSection";
    var html = "";

    if (list && list.length > 0) {
        for (var i = 0; i < list.length; i++) {
            var m = list[i];
            html += "<tr>"
                 + "<td>" + nullSafe(m.gatePassId) + "</td>"
                 + "<td>" + nullSafe(m.workmenName) + "</td>"
                 + "<td>" + nullSafe(m.dbNumber) + "</td>"
                 + "<td>" + nullSafe(m.docNumber) + "</td>"
                 + "<td>" + nullSafe(m.dbAmount) + "</td>"
                 + "<td>" + nullSafe(m.docAmount) + "</td>"
                 + "<td>" + nullSafe(m.mismatchReason) + "</td>"
                 + "</tr>";
        }
        $(bodyId).html(html);
        $(sectionId).show();
    } else {
        $(bodyId).html("");
        $(sectionId).hide();
    }
}

var messageTimers = {};

function showMessage(message, type, reconType) {

    var targetDiv = reconType === 'PF' ? "#messageDiv" : "#esicmessageDiv";
    var $div = $(targetDiv);

    // Clear previous timer
    if (messageTimers[targetDiv]) {
        clearTimeout(messageTimers[targetDiv]);
    }

    $div
        .stop(true, true)
        .removeClass()
        .addClass("alert alert-" + type)
        .html(message)
        .fadeIn();

    // Store new timer
    messageTimers[targetDiv] = setTimeout(function () {
        $div.fadeOut(500);
    }, 3000);
}

function nullSafe(val) {
    return (val === null || val === undefined) ? "" : val;
}
function getReconciliationData() {
    var contractorId = document.getElementById("contractor").value;

    if (!contractorId) return;

    var contextPath = "/CWFM";

    fetch(contextPath + "/contractor/getReconciliationData", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded"
        },
        body: "contractorId=" + contractorId
    })
    .then(response => response.json())
    .then(data => {

        // 🔥 PF TABLE UPDATE
        var pfTable = $('#pfTable').DataTable();
        pfTable.clear();

        if (data.pfList && data.pfList.length > 0) {
            data.pfList.forEach(function(w) {
                pfTable.row.add([
                    w.gatePassId || "",
                    w.workmenName || "",
                    w.uanNumber || "",
                    w.pfNumber || "",
                    w.pfAmount || ""
                ]);
            });
        }

        pfTable.draw();

        // 🔥 ESIC TABLE UPDATE
        var esicTable = $('#esicTable').DataTable();
        esicTable.clear();

        if (data.esicList && data.esicList.length > 0) {
            data.esicList.forEach(function(w) {
                esicTable.row.add([
                    w.gatePassId || "",
                    w.workmenName || "",
                    w.esicNumber || "",
                    w.esicAmount || ""
                ]);
            });
        }

        esicTable.draw();

    })
    .catch(error => console.error("Error:", error));
}

function initializeAutoSelects() {
	const userAccount = $("#loggedInUserAccount").val();
    autoSelectAndTrigger("principalEmployer", function (unitId) {
        getContractorsAndTrades(unitId, userAccount);
    });
	autoSelectAndTrigger("accessArea", null, false);
	
	autoSelectAndTrigger("workmenType", null, false);
}

function autoSelectAndTrigger(selectId, callback, triggerChange = true) {
    const $select = $("#" + selectId);
    const options = $select.find("option[value!='']");

    if (options.length === 1 && !$select.val()) {
        const value = options.first().val();
        $select.val(value);

        if (callback) {
            callback(value);
        }

        if (triggerChange) {
            $select.trigger("change");
        }
    }
}
function redirectToWorkmenViewLink(transactionId, status) {

    if (!transactionId || transactionId.trim() === '') {
        alert("Invalid Transaction Id");
        return;
    }

    if (status && status.toLowerCase() === "draft") {
        alert("Status 'Draft' record cannot be viewed.");
        return;
    }

    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState === 4) {

            if (xhr.status === 200) {

                document.getElementById("mainContent").innerHTML = xhr.responseText;
 setDateRange();
            } else {

                alert("Failed to load view page.");
            }
        }
    };

    xhr.open("GET", "/CWFM/contractworkmen/view/" + encodeURIComponent(transactionId), true);

    xhr.send();
}
function redirectToWorkmenBlackViewLink(gatepassId) {

 var mode = "view";
 
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState == 4 && xhr.status == 200) {

            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };

      xhr.open("GET", "/CWFM/contractworkmen/blackview/" + gatepassId+ "/" + mode, true);

    xhr.send();
}
function redirectToWorkmenBlockViewLink(gatePassId) {

 var mode = "view";
 
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState == 4 && xhr.status == 200) {

            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };

      xhr.open("GET", "/CWFM/contractworkmen/blockview/" + gatePassId+ "/" + mode, true);

    xhr.send();
}
function redirectToWorkmenCancelViewLink(gatePassId) {

 var mode = "view";
 
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState == 4 && xhr.status == 200) {

            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };

       xhr.open("GET", "/CWFM/contractworkmen/cancelview/" + gatePassId+"/"+mode, true);

    xhr.send();
}
function redirectToWorkmenDeblackViewLink(gatePassId) {

 var mode = "view";
 
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState == 4 && xhr.status == 200) {

            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };

      xhr.open("GET", "/CWFM/contractworkmen/deblackview/" + gatePassId+"/"+mode, true);

    xhr.send();
}
function redirectToWorkmenUnblockViewLink(gatePassId) {

 var mode = "view";
 
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState == 4 && xhr.status == 200) {

            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };

       xhr.open("GET", "/CWFM/contractworkmen/unblockview/" + gatePassId+"/"+mode, true);

    xhr.send();
}
function redirectToWorkmenLostViewLink(gatePassId) {

 var mode = "view";
 
    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState == 4 && xhr.status == 200) {

            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };

       xhr.open("GET", "/CWFM/contractworkmen/lostordamageview/" + gatePassId+"/"+mode, true);

    xhr.send();
}
function redirectToWorkmenRenewViewLink(gatePassId) {

    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState == 4 && xhr.status == 200) {

            document.getElementById("mainContent").innerHTML = xhr.responseText;
        }
    };

       xhr.open("GET", "/CWFM/contractworkmen/renewview/" + gatePassId, true);

    xhr.send();
}

document.addEventListener('click', function (e) {

    // ADD ROW
    if (e.target.matches('button.addTrainingRowNew')) {
        e.preventDefault();
        e.stopImmediatePropagation();
        setTimeout(() => {
            addContTrainingRowNew();
            if (typeof setDateRange === "function") {
                setDateRange();
            }
        }, 0);
    }

    // DELETE ROW
    else if (e.target.matches('button.removeTrainingRowNew')) {
        e.preventDefault();
        e.stopImmediatePropagation();
        setTimeout(() => {
            deleteContTrainingRowNew(e.target);
        }, 0);
    }
});

/* ADD TRAINING ROW */
function addContTrainingRowNew() {

    const tbody = document.getElementById("trainingBody");

    const row = document.createElement("tr");

    row.innerHTML = `
        <td><button type="button" class="btn btn-success addTrainingRowNew" style="color:blue;background-color:white;">+</button></td>
        <td><button type="button" class="btn btn-danger removeTrainingRowNew" style="color:blue;background-color:white;">-</button></td>
        <td></td>
        <td></td>
        <td><input type="text" class="form-control trainingFromDate expirydatetimepicker" name="trainingFromDate" autocomplete="off"/></td>
        <td><input type="text" class="form-control trainingToDate expirydatetimepicker" name="trainingToDate" autocomplete="off"/></td>
        <td><input type="text" class="form-control fromTime" name="fromTime" autocomplete="off"/></td>
        <td><input type="text" class="form-control toTime" name="toTime" autocomplete="off"/></td>
        <td><input type="text" class="form-control faculty" name="faculty" autocomplete="off"/></td>
        <td><input type="number" class="form-control marks" name="marks" min="0" max="100" autocomplete="off"/></td>
        <td>
          <select class="form-control efficency" name="efficency">
                <option value="1">1</option>
                <option value="2">2</option>
                <option value="3">3</option>
                <option value="4">4</option>
                <option value="5">5</option>
            </select>
        </td>
        <td><input type="text" class="form-control nextTrainingDate expirydatetimepicker" name="nextTrainingDate" autocomplete="off"/></td>
        <td><input type="text" class="form-control remarks" name="remarks" autocomplete="off"/></td>
    `;
    /* CLONE TRAINING TYPE DROPDOWN */
   const trainingTypeDropdown =
    document.querySelector('#trainingBody tr:first-child select.trainingType');

  if (trainingTypeDropdown) {

    const clonedTypeDropdown = trainingTypeDropdown.cloneNode(true);
    clonedTypeDropdown.classList.add("trainingType");
    clonedTypeDropdown.name = "trainingType";
    /* RESET DROPDOWN */
    clonedTypeDropdown.selectedIndex = 0;
    Array.from(clonedTypeDropdown.options).forEach(option => {
        option.removeAttribute("selected");
    });
    clonedTypeDropdown.options[0].selected = true;
    row.cells[2].appendChild(clonedTypeDropdown);
 }
    /* CLONE TRAINING NAME DROPDOWN */
    const trainingNameDropdown =
    document.querySelector('#trainingBody tr:first-child select.trainingName');

 if (trainingNameDropdown) {
    const clonedNameDropdown = trainingNameDropdown.cloneNode(true);
    clonedNameDropdown.classList.add("trainingName");
    clonedNameDropdown.name = "trainingName";
    /* RESET DROPDOWN */
    clonedNameDropdown.selectedIndex = 0;
    Array.from(clonedNameDropdown.options).forEach(option => {
        option.removeAttribute("selected");
    });
    clonedNameDropdown.options[0].selected = true;
    row.cells[3].appendChild(clonedNameDropdown);
 }
    /* APPEND ROW */
    tbody.appendChild(row);
}

/* DELETE TRAINING ROW */
function deleteContTrainingRowNew(buttonElement) {

    const row = buttonElement.closest('tr');
    const tbody = document.getElementById("trainingBody");
    const dataRows = Array.from(
        tbody.querySelectorAll('tr')
    ).filter(r => r.querySelector('button.removeTrainingRowNew'));
    console.log(dataRows.length);
    if (dataRows.length > 1) {
        row.remove();
    } else {
        alert("At least one row must be present.");
    }
}

function checkSameDayValidation(gatePassId, gatePassTypeId, callback) {

    $.ajax({
        type: "POST",
        url: "/CWFM/contractworkmen/checkSameDayAction",
        data: {
            gatePassId: gatePassId,
            gatePassTypeId: gatePassTypeId
        },
        success: function(response) {

            if (response === true || response === "true") {

                if (gatePassTypeId == 4) {
                    alert("Block action happened today. Unblock is not allowed on the same day.");
                }
                else if (gatePassTypeId == 6) {
                    alert("Blacklist action happened today. Deblacklist is not allowed on the same day.");
                }

                hideLoader();
                callback(false);
                return;
            }

            callback(true);
        },
        error: function(xhr) {

            hideLoader();

            console.log("Status:", xhr.status);
            console.log("Response:", xhr.responseText);

            alert("Validation check failed.");
            callback(false);
        }
    });
}
function redirectToWorkmenFullTimeContractorAdd(){
	// Fetch the content of add.jsp using AJAX
	    var xhr = new XMLHttpRequest();
	    xhr.onreadystatechange = function() {
	        if (xhr.readyState == 4 && xhr.status == 200) {
	            // Update the mainContent element with the fetched content
	            document.getElementById("mainContent").innerHTML = xhr.responseText;
				setDateRange();
				initializeAutoSelects();
	        }
	    };
	    xhr.open("GET", "/CWFM/contractworkmen/fullTimeContractorGatepass", true);
	    xhr.send();
}

function saveFullTimeContractorGatePass(userId) {
	showLoader();
	
	// ✅ Clear all previous errors first
    $("#docTabGlobalError").hide().text("");
    $("label[id^='error-']").hide();
    
    let basicValid = true;
    let employmentValid = true;
    let otherValid = true;
    let documentValid = true;
    
    var aadharFile = $("#aadharFile").prop("files")[0];
    var policeFile = $("#policeFile").prop("files")[0];
	var profilePic = $("#imageFile").prop("files")[0];
	var appointmentFile = $("#appointmentFile").prop("files")[0];
    // Validate the files (optional)
   // if (!validateFullTimeContractorFiles(aadharFile, policeFile,profilePic,appointmentFile)) {
   //     documentValid = false; // Stop the upload if validation fails
   //     hideLoader();
   // }
    if (!validateBasicData()) {
        basicValid = false;
        hideLoader();
    }
    if (!validateFullTimeContractorEmploymentInformation()) {
        employmentValid = false;
        hideLoader();
    }
    if (!validateOtherInformation()) {
        otherValid = false;
        hideLoader();
    }
    console.log("basicValid: " + basicValid);
    console.log("employmentValid: " + employmentValid);
    console.log("otherValid: " + otherValid);
    console.log("documentValid: " + documentValid);
    
    //TAB NAME ERROR MESSAGE LOGIC (YOUR REQUIREMENT)
    let errorTabs = [];

    if (!basicValid) errorTabs.push("Basic");
    if (!employmentValid) errorTabs.push("Employment");
    if (!otherValid) errorTabs.push("Other");

    // If any tab has errors → show message in Documents tab
    if (errorTabs.length > 0) {

        let msg = "Please check errors in: " + errorTabs.join(", ") + " tab(s).";

        $("#docTabGlobalError")
            .text(msg)
            .show();

        hideLoader();
        return;
    }
       // ✅ Utility function for Capital Case
    function toCapitalCase(str) {
        return str
            .toLowerCase()
            .split(' ')
            .map(word => word.charAt(0).toUpperCase() + word.slice(1))
            .join(' ');
    }
    const firstName = toCapitalCase($("#firstName").val().trim());
    const lastName = toCapitalCase($("#lastName").val().trim());
    const relationName = toCapitalCase($("#relationName").val().trim());
    
 const pfApplicable = $("#pfApplicable").is(":checked") ? "Yes" : "No";
    if (basicValid && employmentValid && otherValid && documentValid) {
        const data = new FormData();
        const jsonData = {
			transactionId:$("#transactionId").val().trim(),
			gatePassId:$("#gatePassId").val().trim(),
            aadhaarNumber: $("#aadharNumber").val().trim(),
            firstName: firstName,
            lastName: lastName,
            dateOfBirth: $("#dateOfBirth").val().trim(),
            gender: $("#gender").val(),
            relationName: relationName,
            idMark: $("#idMark").val(),
            mobileNumber: $("#mobileNumber").val().trim(),
            maritalStatus: $("#maritalStatus").val(),
            principalEmployer: $("#principalEmployer").val(),
            contractor: $("#contractor").val(),
            department: $("#department").val(),
            subdepartment: $("#subdepartment").val(),
            bloodGroup: $("#bloodGroup").val(),
            accommodation: $("#accommodation").val(),
            academic: $("#academic").val(),
            technical: $("#technical").val(),
            ifscCode: $("#ifscCode").val().trim(),
            accountNumber: $("#accountNumber").val().trim(),
            emergencyName: $("#emergencyName").val().trim(),
            emergencyNumber: $("#emergencyNumber").val().trim(),
            userId: userId,
            gatePassAction: "save",
            comments: $("#comments").val().trim(),
			address:$("#address").val().trim(),
			doj:$("#doj").val(),
            policeVerificationDate: $("#policeVerificationDate").val().trim(),
            disability:$("#disability").val(),
            workmenType:$("#workmenType").val(),
            unitId: $("#principalEmployer").val(),
        };

        // Serialize the JSON object to a string
		const jsonString = JSON.stringify(jsonData);

		// Append the JSON data to FormData
		data.append("jsonData", jsonString);

        // Append the files to the FormData
        if (aadharFile) {
            data.append("aadharFile", aadharFile);
        }
        if (policeFile) {
            data.append("policeFile", policeFile);
        }
		if(profilePic){
			data.append("profilePic",profilePic);
		}
		if(appointmentFile){
			data.append("appointmentFile",appointmentFile);
		}
		
    	const additionalFields = document.querySelectorAll('.document-field');
        additionalFields.forEach((field, index) => {
        const docType = field.querySelector('select[name="documentType"]').value;
        const fileInput = field.querySelector('input[type="file"]');

        if (docType && fileInput.files[0]) {
            data.append('additionalFiles', fileInput.files[0]);
            data.append('documentTypes', docType);
        }
    });
        
		/*// Log FormData content
for (const [key, value] of data.entries()) {
    console.log(key, value instanceof File ? value.name : value); // Log filename if it's a File
}*/
        // Send the data to the server using AJAX
        const xhr = new XMLHttpRequest();
        xhr.open("POST", "/CWFM/contractworkmen/saveFullTimeContractorGatePass", true);

        xhr.onload = function () {
			hideLoader();
            if (xhr.status === 200) {
                console.log("Data saved successfully:", xhr.responseText);
				sessionStorage.setItem("successMessage", "FullTimeContractor Gatepass request raised successfully!");
                loadCommonList('/contractworkmen/fullTimeContractorOnboardingList', 'Full Time Contractor List');
				//hideLoader();
            }else if (xhr.status === 400) {  
				       const msg = xhr.responseText.trim();
				       console.error("Server validation failed: " + msg);
					   showLicenseError(msg);
				       //alert(msg); // or show in UI better
				       //sessionStorage.setItem("errorMessage", msg);
					   return;
				   }else {
                console.error("Error saving data:", xhr.status, xhr.responseText);
				sessionStorage.setItem("errorMessage", "Failed to save fulltime contractor Gatepass request!");
            }
        };

        xhr.onerror = function () {
            console.error("Request failed");
			sessionStorage.setItem("errorMessage", "Failed to save fulltime contractor Gatepass request!");
			hideLoader();
        };

        // Send the FormData object
        xhr.send(data);
    } else {
        console.error("Validation failed for one or more fields.");
    }
}
function validateFullTimeContractorEmploymentInformation(){
	let isValid = true;
    const principalEmp = $("#principalEmployer").val();
     if (principalEmp === "") {
        $("#error-principalEmployer").show();
        isValid = false;
    }else{
		$("#error-principalEmployer").hide();
	}
	const cont = $("#contractor").val();
     if (cont === "") {
        $("#error-contractor").show();
        isValid = false;
    }else{
		$("#error-contractor").hide();
	}
	const dept = $("#department").val();
     if (dept === "") {
        $("#error-department").show();
        isValid = false;
    }else{
		$("#error-department").hide();
	}
	const subdept = $("#subdepartment").val();
     if (subdept === "") {
        $("#error-area").show();
        isValid = false;
    }else{
		$("#error-area").hide();
	}
	
	return isValid;
}
function validateFullTimeContractorFiles(aadharFile, policeFile, profilePc,appointmentFile) {
    let valid = true;

    // Aadhar File - Mandatory & Size check
     if (aadharFile.size > 5 * 1024 * 1024) {
        $("#aadharError").text("Aadhar file must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#aadharError").text("");
    }

    // Police Verification File - Mandatory & Size check
    if (policeFile.size > 5 * 1024 * 1024) {
        $("#policeError").text("Police file must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#policeError").text("");
    }
//appointment File - Mandatory & Size check
 if (appointmentFile.size > 5 * 1024 * 1024) {
        $("#appointmentError").text("Appointment file must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#appointmentError").text("");
    }
// Police Verification Date - Mandatory  check
const policeVerificationDate = $("#policeVerificationDate").val().trim();
    if (policeVerificationDate === "") {
        $("#error-policeVerificationDate").show();
        valid = false;
    }else {
        $("#error-policeVerificationDate").hide("");
    }
    
    // Profile Photo - Mandatory & Size check
    if (profilePc.size > 5 * 1024 * 1024) {
        $("#profilePcError").text("Photo/Image must be less than 5MB").addClass("error-bold");
        valid = false;
    } else {
        $("#profilePcError").text("");
    }

	const comments = $("#comments").val().trim();
		    if (comments === "") {
		        $("#error-comments").show();
		        valid = false;
		    }else{
				 $("#error-comments").hide();
			}
			// Accept Checkbox validation
			    if (!$("#acceptCheck").is(":checked")) {
			        $("#acceptError").show();
			        valid = false;
			    } else {
			        $("#acceptError").hide();
			    }
			    if (valid) {
    $("#docTabGlobalError").hide();
}		
    return valid;
}
function goBackToFullTimeContractoronboardingList() {
    	 loadCommonList('/contractworkmen/fullTimeContractorOnboardingList', 'Full Time Contractor List');
    }
    function searchFullTimeContractorGatePassBasedOnPE(type) {
					    var principalEmployerId = $('#principalEmployerId').val();
					    
						var deptId=$("#deptId").val();
					    $.ajax({
					        url: '/CWFM/contractworkmen/getFullTimeContractorgatePassListingDetails',
					        type: 'POST',
					        data: {
					            principalEmployerId: principalEmployerId,
								deptId:deptId,
								type:type
					        },
					        success: function(response) {
					            var tableBody = $('#workmenTable tbody');
								// 🔄 Clear previous DataTable and its config
								           if ($.fn.DataTable.isDataTable('#workmenTable')) {
								               $('#workmenTable').DataTable().destroy();
								           }
										   tableBody.empty();
					            if (Array.isArray(response) &&response.length > 0) {
					                $.each(response, function(index, wo) {
					                    var row = '<tr  >' +
												'<td  ><input type="checkbox" name="selectedWOs" value="' + wo.transactionId + '" class="bulk-check"  data-transaction="'+wo.transactionId+'"	data-gatepass="'+wo.gatePassId+'"  data-type="'+wo.gatePassTypeId+'"></td>'+
												//'<td  >' + wo.transactionId + '</td>' +
												
												 '<td>' +
        '<a href="#" class="transaction-link" ' +
        'onclick="redirectToFullTimeContractorWorkmenViewLink(\'' + wo.transactionId + '\', \'' + wo.status + '\'); return false;">' +
        wo.transactionId +
        '</a>' +
    '</td>' +
												 '<td  >' + wo.gatePassId + '</td>' +
					                              '<td  >' + wo.firstName+' ' +wo.lastName + '</td>' +
												 
												  
												  
												  '<td  >' + wo.aadhaarNumber + '</td>' +	
												  '<td  >' + wo.contractorName + '</td>' +	
												 
												  '<td  >' +wo.unitName + '</td>' +	
												  '<td  >' + wo.gatePassType + '</td>' +
												  '<td  >' + toCapitalCase(wo.onboardingType) + '</td>' +
												  '<td  >' + wo.status + '</td>' +				                             
					                              '</tr>';
					                    tableBody.append(row);
					                });
									
					            } 								

																	            // ✅ Always init after rows are drawn
																	            initWorkmenTable("workmenTable");
																	        },
					       
					        error: function(xhr, status, error) {
					            console.error("Error fetching data:", error);
					        }
					    });
					}
					
					function redirectToFullTimeContractorWorkmenView() {
    var selectedCheckboxes = document.querySelectorAll('input[type="checkbox"]:checked');
    if (selectedCheckboxes.length !== 1) {
        alert("Please select exactly one row to view.");
        return;
    }
    
    var selectedRow = selectedCheckboxes[0].closest('tr');
    var transactionId = selectedRow.querySelector('[name="selectedWOs"]').value;
    //var gatePassType = selectedRow.cells[7].innerText.trim(); // Adjust index if needed
    var status = selectedRow.cells[9].innerText.trim(); // Adjust index if needed

         if (status.toLowerCase() == "draft") {
             alert("Status 'Draft' record can not View.");
            return;
           }
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function() {
        if (xhr.readyState == 4 && xhr.status == 200) {
            document.getElementById("mainContent").innerHTML = xhr.responseText;
             setDateRange();
        }
    };
    xhr.open("GET", "/CWFM/contractworkmen/viewFullTimeContractor/" + transactionId, true);
    xhr.send();
}
function redirectToFullTimeContractorWorkmenViewLink(transactionId, status) {

    if (!transactionId || transactionId.trim() === '') {
        alert("Invalid Transaction Id");
        return;
    }

    if (status && status.toLowerCase() === "draft") {
        alert("Status 'Draft' record cannot be viewed.");
        return;
    }

    var xhr = new XMLHttpRequest();

    xhr.onreadystatechange = function () {

        if (xhr.readyState === 4) {

            if (xhr.status === 200) {

                document.getElementById("mainContent").innerHTML = xhr.responseText;
 setDateRange();
            } else {

                alert("Failed to load view page.");
            }
        }
    };

    xhr.open("GET", "/CWFM/contractworkmen/viewFullTimeContractor/" + encodeURIComponent(transactionId), true);

    xhr.send();
}
function toTitleCase(value) {
    if (!value) {
        return "";
    }

    return value
        .trim()
        .toLowerCase()
        .replace(/\b\w/g, function(char) {
            return char.toUpperCase();
        });
}
function checkAadharAndLoadCancelledDetails() {
    const aadharNumber = $("#aadharNumber").val().trim();
    if (aadharNumber === "" || aadharNumber.length !== 12 || isNaN(aadharNumber)) {
        return;
    }
    $.ajax({
        url: "/CWFM/contractworkmen/checkAadharExistsCreation",
        type: "GET",
        data: {
            aadharNumber: aadharNumber,
            gatePassId: $("#gatePassId").val(),
            transactionId: $("#transactionId").val()
        },
        async: false,
        success: function(response) {
            let status = response.status ? response.status.trim() : "";
            console.log("Aadhaar check for loading details:", status);
            if (status === "FOUND") {
                /*
                 * Populate cancelled record details ONLY here.
                 * This function is called when Aadhaar is entered/changed.
                 * It is NOT called during submit validation.
                 */
                loadCancelledAadharDetails(response);
                $("#error-aadhar").hide();
            } else{
             resetCancelledAadharFieldState();
             if (status === "Invalid") {
                $("#error-aadhar").text("Invalid Aadhar Number").show();
            } else if (status !== "") {
                $("#error-aadhar").text(status).show();
            } else {
                $("#error-aadhar").hide();
            }
          }
        },
        error: function() {
            $("#error-aadhar").text("Unable to verify Aadhaar").show();
        }

    });
}
function loadCancelledAadharDetails(response) {

    // ============================================
    // POPULATE CANCELLED RECORD DETAILS
    // ============================================

    $("#firstName").val(response.firstName || "");
    $("#lastName").val(response.lastName || "");
    $("#relationName").val(response.relativeName || "");
    $("#dateOfBirth").val(response.dob || "");
    $("#gender").val(response.gender || "");

    $("#mobileNumber").val(response.mobileNumber || "");
    $("#maritalStatus").val(response.maritalStatus || "");
    $("#address").val(response.address || "");
    $("#workmenType").val(response.workmenType || "");
    $("#disability").val(response.disability || "");


    // ============================================
    // NON-EDITABLE FIELDS
    // ============================================

    // First Name - NON EDITABLE
    $("#firstName").prop("readonly", true);

    // Date of Birth - NON EDITABLE
    $("#dateOfBirth").prop("readonly", true);

    // Gender - NON EDITABLE
    // Gender is normally a <select>, so readonly
    // will not work. Use disabled.
    $("#gender").prop("disabled", true);


    // ============================================
    // EDITABLE FIELDS
    // ============================================

    // Last Name - EDITABLE
    $("#lastName").prop("readonly", false);

    // Relation Name - EDITABLE
    $("#relationName").prop("readonly", false);

    // Mobile Number - EDITABLE
    $("#mobileNumber").prop("readonly", false);

    // Marital Status - EDITABLE
    // Normally a <select>
    $("#maritalStatus").prop("disabled", false);

    // Address - EDITABLE
    $("#address").prop("readonly", false);

    // Workmen Type - EDITABLE
    // Normally a <select>
    $("#workmenType").prop("disabled", false);

    // Disability - EDITABLE
    // Normally a <select>
    $("#disability").prop("disabled", false);
}


function resetCancelledAadharFieldState() {

    // ============================================
    // RESET ALL FIELDS TO NORMAL EDITABLE STATE
    // ============================================

    $("#firstName").prop("readonly", false);

    $("#lastName").prop("readonly", false);

    $("#relationName").prop("readonly", false);

    $("#dateOfBirth").prop("readonly", false);

    $("#gender").prop("disabled", false);

    $("#mobileNumber").prop("readonly", false);

    $("#maritalStatus").prop("disabled", false);

    $("#address").prop("readonly", false);

    $("#workmenType").prop("disabled", false);

    $("#disability").prop("disabled", false);
}
function lockExistingAadharFields() {
    // EXISTING AADHAAR:ONLY FIRST NAME, DOB AND GENDER MUST BE NON-EDITABLE

    // First Name
    $("#firstName")
        .prop("readonly", true)
        .attr("readonly", "readonly");

    // Date of Birth
    $("#dateOfBirth")
        .prop("readonly", true)
        .attr("readonly", "readonly");

    // Gender
    $("#gender")
        .prop("disabled", true)
        .attr("disabled", "disabled");

    // ALL OTHER FIELDS MUST REMAIN EDITABLE

    $("#lastName")
        .prop("readonly", false)
        .removeAttr("readonly");

    $("#relationName")
        .prop("readonly", false)
        .removeAttr("readonly");

    $("#mobileNumber")
        .prop("readonly", false)
        .removeAttr("readonly");

    $("#maritalStatus")
        .prop("disabled", false)
        .removeAttr("disabled");

    $("#address")
        .prop("readonly", false)
        .removeAttr("readonly");

    $("#workmenType")
        .prop("disabled", false)
        .removeAttr("disabled");

    $("#disability")
        .prop("disabled", false)
        .removeAttr("disabled");
        
    // PREVENT DOB DATEPICKER / KEYBOARD FROM CHANGING DOB

    $("#dateOfBirth")
        .off(".existingAadhar")
        .on("keydown.existingAadhar", function (e) {
            e.preventDefault();
            return false;
        })
        .on("click.existingAadhar", function (e) {
            e.preventDefault();

            // Hide jQuery UI datepicker if it is open
            try {
                $(this).datepicker("hide");
            } catch (error) {
                // Ignore if datepicker is not initialized
            }

            return false;
        });
}

function unlockAadharFields() {
    // NEW AADHAAR:ALL FIELDS SHOULD BE EDITABLE
    // First Name
    $("#firstName")
        .prop("readonly", false)
        .removeAttr("readonly");

    // Date of Birth
    $("#dateOfBirth")
        .prop("readonly", false)
        .removeAttr("readonly");

    // Gender
    $("#gender")
        .prop("disabled", false)
        .removeAttr("disabled");

    // Other fields
    $("#lastName")
        .prop("readonly", false)
        .removeAttr("readonly");

    $("#relationName")
        .prop("readonly", false)
        .removeAttr("readonly");

    $("#mobileNumber")
        .prop("readonly", false)
        .removeAttr("readonly");

    $("#maritalStatus")
        .prop("disabled", false)
        .removeAttr("disabled");

    $("#address")
        .prop("readonly", false)
        .removeAttr("readonly");

    $("#workmenType")
        .prop("disabled", false)
        .removeAttr("disabled");

    $("#disability")
        .prop("disabled", false)
        .removeAttr("disabled");
    // Remove the special existing-Aadhaar handlers from DOB
    $("#dateOfBirth").off(".existingAadhar");
}
