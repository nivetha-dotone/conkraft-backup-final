function fetchData(module) {
    
    let unitId = $('#principalEmployer').val();
    if (!module) return;
	// Get module name (selected option's text)
	  let moduleName = $('#module option:selected').text();
    $.ajax({
        url: '/CWFM/export/fetchModuleData',
        method: 'GET',
        data: {
            module: moduleName.trim(),
            unitId: unitId
        },
        success: function (response) {
            let columns = response.columns;
            let rows = response.rows;
            let headerHtml = '<th><input type="checkbox" id="selectAll" onchange="toggleExportSelectAll()"/></th>';
            columns.forEach(col => { headerHtml += `<th>${col}</th>`; });
            $('#tableHeader').html(headerHtml);

            let bodyHtml = '';
            rows.forEach(row => {
                bodyHtml += '<tr><td><input type="checkbox" class="rowCheckbox" name="selectedUnitIds"  /></td>';
                columns.forEach(col => { bodyHtml += `<td>${row[col]}</td>`; });
                bodyHtml += '</tr>';
            });
            $('#tableBody').html(bodyHtml);

            $('#dynamicTable').show();
            $('#exportBtn').show();
        }
    });
}

/*$(document).on('change', '#selectAll', function () {
    $('.rowCheckbox').prop('checked', this.checked);
});*/

function toggleExportSelectAll() {
           var selectAllCheckbox = document.getElementById('selectAll');
           var checkboxes = document.querySelectorAll('input[name="selectedUnitIds"]');
           checkboxes.forEach(function(checkbox) {
               checkbox.checked = selectAllCheckbox.checked;
           });
       }

	   function exportModuleCSV() {
	       let moduleName = $('#module option:selected').text().trim();

	       // ✅ Only split CSV if module is "Contract Workmen"
	       if (moduleName === "Contract Workmen") {
	           let headers = [];
	           let employmentStatusIndex = -1;

	           // Collect headers and find the index of "Employment Status"
	           $('#dynamicTable thead th').each(function (index) {
	               if (index === 0) return; // Skip checkbox column
	               let headerText = $(this).text().trim();
	               headers.push(headerText);
	               if (headerText === "Employment Status") {
	                   employmentStatusIndex = index - 1;
	               }
	           });

	           if (employmentStatusIndex === -1) {
	               alert("Employment Status column not found.");
	               return;
	           }

	           let activeRows = [];
	           let terminatedRows = [];

	           $('#dynamicTable tbody tr').each(function () {
	               if ($(this).find('.rowCheckbox').is(':checked')) {
	                   let row = [];
	                   $(this).find('td:not(:first-child)').each(function () {
	                       row.push($(this).text().trim());
	                   });

	                   let status = row[employmentStatusIndex];
	                   if (status === "Active") {
	                       activeRows.push(row);
	                   } else if (status === "Terminated") {
	                       terminatedRows.push(row);
	                   }
	               }
	           });

	           if (activeRows.length > 0) {
	               downloadCSV(headers, activeRows, moduleName + "_Active.csv");
	           }

	           if (terminatedRows.length > 0) {
	               downloadCSV(headers, terminatedRows, moduleName + "_Terminated.csv");
	           }

	           if (activeRows.length === 0 && terminatedRows.length === 0) {
	               alert("No selected rows with Employment Status 'Active' or 'Terminated'.");
	           }
	       } else {
	           // ✅ Default single CSV export for all other modules
	           let headers = [];
	           $('#dynamicTable thead th').each(function (index) {
	               if (index === 0) return;
	               headers.push($(this).text().trim());
	           });

	           let rows = [];
	           $('#dynamicTable tbody tr').each(function () {
	               if ($(this).find('.rowCheckbox').is(':checked')) {
	                   let row = [];
	                   $(this).find('td:not(:first-child)').each(function () {
	                       row.push($(this).text().trim());
	                   });
	                   rows.push(row);
	               }
	           });

	           if (rows.length > 0) {
	               downloadCSV(headers, rows, moduleName + ".csv");
	           } else {
	               alert("No rows selected.");
	           }
	       }
	   }

	   function downloadCSV(headers, rows, fileName) {
	       let csv = headers.join(',') + '\n';
	       rows.forEach(row => {
	           csv += row.map(value => `${value}`).join(',') + '\n';
	       });

	       const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
	       const link = document.createElement("a");
	       link.href = URL.createObjectURL(blob);
	       link.download = fileName;
	       link.style.display = "none";
	       document.body.appendChild(link);
	       link.click();
	       document.body.removeChild(link);
	   }
	   
	   function fetchReportData() {

	       let unitId = $('#principalEmployers').val();
	       let reportType = "contractWorkmenReport";
           let startDate = $('#startDate').val();
           let endDate = $('#endDate').val();
	       //if (!module) return;

	       let contractorId = $('#contractors option:selected').val();

	       $.ajax({
	           url: '/CWFM/reports/fetchModuleData',
	           method: 'GET',
	           data: {
	               contractorId: contractorId,
	               unitId: unitId,
	               reportType: reportType,
	               startDate:startDate,
	               endDate:endDate
	           },
	           success: function (response) {

	               let table = $('#dynamicTable');
	               let tableBody = $('#dynamicTable tbody');

	               if ($.fn.DataTable.isDataTable('#dynamicTable')) {
	                   table.DataTable().clear().destroy();
	               }

	               tableBody.empty();

	               let columns = response.columns || [];
	               let rows = response.rows || [];

	               rows.forEach(row => {

	                   let gatePassId = row['Gate Pass Id'] || '';

	                   let bodyHtml = `
	                       <tr>
	                           <td>
	                               <input type="checkbox"
	                                      class="bulk-check"
	                                      name="selectedUnitIds"
	                                      value="${gatePassId}">
	                           </td>
	                   `;

	                   columns.forEach(col => {
	                       bodyHtml += `<td>${row[col] == null ? '' : row[col]}</td>`;
	                   });

	                   bodyHtml += '</tr>';

	                   tableBody.append(bodyHtml);
	               });

	               table.show();

	               initTable("dynamicTable");
	                //reinitializeDataTable('#dynamicTable');

	               $('#exportBtn').show();
	           },
	           error: function () {
	               alert("Error while fetching report data.");
	           }
	       });
	   }
	   function initTable(tablename) {
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
		function reportModuleCSV() {

		    let headers = [];

		    $('#dynamicTable thead th').each(function (index) {
		        if (index === 0) return;
		        headers.push($(this).text().trim());
		    });

		    let rows = [];

		    if ($.fn.DataTable.isDataTable('#dynamicTable')) {

		        let dataTable = $('#dynamicTable').DataTable();

		        dataTable.rows().every(function () {

		            let rowNode = $(this.node());
		            let checkbox = rowNode.find('.bulk-check');

		            if (checkbox.is(':checked')) {

		                let row = [];

		                rowNode.find('td:not(:first-child)').each(function () {
		                    row.push($(this).text().trim());
		                });

		                rows.push(row);
		            }
		        });

		    } else {

		        $('#dynamicTable tbody tr').each(function () {

		            let checkbox = $(this).find('.bulk-check');

		            if (checkbox.is(':checked')) {

		                let row = [];

		                $(this).find('td:not(:first-child)').each(function () {
		                    row.push($(this).text().trim());
		                });

		                rows.push(row);
		            }
		        });
		    }

		    if (rows.length === 0) {
		        alert("No rows selected.");
		        return;
		    }

		    downloadCSV(headers, rows, "Contractor Workmen Report.csv");
		}
	   	     function inactiveReportModuleCSV() {
	   	        let headers = [];

		    $('#dynamicTable thead th').each(function (index) {
		        if (index === 0) return;
		        headers.push($(this).text().trim());
		    });

		    let rows = [];

		    if ($.fn.DataTable.isDataTable('#dynamicTable')) {

		        let dataTable = $('#dynamicTable').DataTable();

		        dataTable.rows().every(function () {

		            let rowNode = $(this.node());
		            let checkbox = rowNode.find('.bulk-check');

		            if (checkbox.is(':checked')) {

		                let row = [];

		                rowNode.find('td:not(:first-child)').each(function () {
		                    row.push($(this).text().trim());
		                });

		                rows.push(row);
		            }
		        });

		    } else {

		        $('#dynamicTable tbody tr').each(function () {

		            let checkbox = $(this).find('.bulk-check');

		            if (checkbox.is(':checked')) {

		                let row = [];

		                $(this).find('td:not(:first-child)').each(function () {
		                    row.push($(this).text().trim());
		                });

		                rows.push(row);
		            }
		        });
		    }

		    if (rows.length === 0) {
		        alert("No rows selected.");
		        return;
		    }

		    downloadCSV(headers, rows, "Gatepass Expiry within 30 days Report.csv");
	   	       }
	   	         function policeverificationReportModuleCSV() {
	   	       
	   	          let headers = [];

		    $('#dynamicTable thead th').each(function (index) {
		        if (index === 0) return;
		        headers.push($(this).text().trim());
		    });

		    let rows = [];

		    if ($.fn.DataTable.isDataTable('#dynamicTable')) {

		        let dataTable = $('#dynamicTable').DataTable();

		        dataTable.rows().every(function () {

		            let rowNode = $(this.node());
		            let checkbox = rowNode.find('.bulk-check');

		            if (checkbox.is(':checked')) {

		                let row = [];

		                rowNode.find('td:not(:first-child)').each(function () {
		                    row.push($(this).text().trim());
		                });

		                rows.push(row);
		            }
		        });

		    } else {

		        $('#dynamicTable tbody tr').each(function () {

		            let checkbox = $(this).find('.bulk-check');

		            if (checkbox.is(':checked')) {

		                let row = [];

		                $(this).find('td:not(:first-child)').each(function () {
		                    row.push($(this).text().trim());
		                });

		                rows.push(row);
		            }
		        });
		    }

		    if (rows.length === 0) {
		        alert("No rows selected.");
		        return;
		    }

		    downloadCSV(headers, rows, "Policeverification Expiry Report.csv");
	   	       }
  function fetchPoliceReportData() {
	       
	       let unitId = $('#principalEmployers').val();
	       let reportType = "policeVerificationWorkmenReport";

	       //if (!module) return;

	       let contractorId = $('#contractors option:selected').val();

	       $.ajax({
	           url: '/CWFM/reports/fetchModuleData',
	           method: 'GET',
	           data: {
	               contractorId: contractorId,
	               unitId: unitId,
	               reportType: reportType
	           },
	           success: function (response) {

	               let table = $('#dynamicTable');
	               let tableBody = $('#dynamicTable tbody');

	               if ($.fn.DataTable.isDataTable('#dynamicTable')) {
	                   table.DataTable().clear().destroy();
	               }

	               tableBody.empty();

	               let columns = response.columns || [];
	               let rows = response.rows || [];

	               rows.forEach(row => {

	                   let gatePassId = row['Gate Pass Id'] || '';

	                   let bodyHtml = `
	                       <tr>
	                           <td>
	                               <input type="checkbox"
	                                      class="bulk-check"
	                                      name="selectedUnitIds"
	                                      value="${gatePassId}">
	                           </td>
	                   `;

	                   columns.forEach(col => {
	                       bodyHtml += `<td>${row[col] == null ? '' : row[col]}</td>`;
	                   });

	                   bodyHtml += '</tr>';

	                   tableBody.append(bodyHtml);
	               });

	               table.show();

	               initTable("dynamicTable");

	               $('#exportBtn').show();
	           },
	           error: function () {
	               alert("Error while fetching report data.");
	           }
	       });
	   }
function fetchInactiveReportData() {
	       
	        let unitId = $('#principalEmployers').val();
	       let reportType = "inActiveWorkmenReport";

	       //if (!module) return;

	       let contractorId = $('#contractors option:selected').val();

	       $.ajax({
	           url: '/CWFM/reports/fetchModuleData',
	           method: 'GET',
	           data: {
	               contractorId: contractorId,
	               unitId: unitId,
	               reportType: reportType
	           },
	           success: function (response) {

	               let table = $('#dynamicTable');
	               let tableBody = $('#dynamicTable tbody');

	               if ($.fn.DataTable.isDataTable('#dynamicTable')) {
	                   table.DataTable().clear().destroy();
	               }

	               tableBody.empty();

	               let columns = response.columns || [];
	               let rows = response.rows || [];

	               rows.forEach(row => {

	                   let gatePassId = row['Gate Pass Id'] || '';

	                   let bodyHtml = `
	                       <tr>
	                           <td>
	                               <input type="checkbox"
	                                      class="bulk-check"
	                                      name="selectedUnitIds"
	                                      value="${gatePassId}">
	                           </td>
	                   `;

	                   columns.forEach(col => {
	                       bodyHtml += `<td>${row[col] == null ? '' : row[col]}</td>`;
	                   });

	                   bodyHtml += '</tr>';

	                   tableBody.append(bodyHtml);
	               });

	               table.show();

	               initTable("dynamicTable");

	               $('#exportBtn').show();
	           },
	           error: function () {
	               alert("Error while fetching report data.");
	           }
	       });
	   }
	   function fetchPolicysExpiryReportData() {
	        let unitId = $('#principalEmployers').val();
	       let reportType = "policyExpiryWorkmenReport";

	       //if (!module) return;

	       let contractorId = $('#contractors option:selected').val();

	       $.ajax({
	           url: '/CWFM/reports/fetchModuleData',
	           method: 'GET',
	           data: {
	               contractorId: contractorId,
	               unitId: unitId,
	               reportType: reportType
	           },
	           success: function (response) {

	               let table = $('#dynamicTable');
	               let tableBody = $('#dynamicTable tbody');

	               if ($.fn.DataTable.isDataTable('#dynamicTable')) {
	                   table.DataTable().clear().destroy();
	               }

	               tableBody.empty();

	               let columns = response.columns || [];
	               let rows = response.rows || [];

	               rows.forEach(row => {

	                   let gatePassId = row['Gate Pass Id'] || '';

	                   let bodyHtml = `
	                       <tr>
	                           <td>
	                               <input type="checkbox"
	                                      class="bulk-check"
	                                      name="selectedUnitIds"
	                                      value="${gatePassId}">
	                           </td>
	                   `;

	                   columns.forEach(col => {
	                       bodyHtml += `<td>${row[col] == null ? '' : row[col]}</td>`;
	                   });

	                   bodyHtml += '</tr>';

	                   tableBody.append(bodyHtml);
	               });

	               table.show();

	               initTable("dynamicTable");

	               $('#exportBtn').show();
	           },
	           error: function () {
	               alert("Error while fetching report data.");
	           }
	       });
	   }

 function policyExpiryReportModuleCSV() {
	   	       
	   	        let headers = [];

		    $('#dynamicTable thead th').each(function (index) {
		        if (index === 0) return;
		        headers.push($(this).text().trim());
		    });

		    let rows = [];

		    if ($.fn.DataTable.isDataTable('#dynamicTable')) {

		        let dataTable = $('#dynamicTable').DataTable();

		        dataTable.rows().every(function () {

		            let rowNode = $(this.node());
		            let checkbox = rowNode.find('.bulk-check');

		            if (checkbox.is(':checked')) {

		                let row = [];

		                rowNode.find('td:not(:first-child)').each(function () {
		                    row.push($(this).text().trim());
		                });

		                rows.push(row);
		            }
		        });

		    } else {

		        $('#dynamicTable tbody tr').each(function () {

		            let checkbox = $(this).find('.bulk-check');

		            if (checkbox.is(':checked')) {

		                let row = [];

		                $(this).find('td:not(:first-child)').each(function () {
		                    row.push($(this).text().trim());
		                });

		                rows.push(row);
		            }
		        });
		    }

		    if (rows.length === 0) {
		        alert("No rows selected.");
		        return;
		    }

		    downloadCSV(headers, rows, "Policy Expiry Report.csv");
	   	       }
	   	       
			   function getContractorsForReports(unitId, userAccount, callback) {
			   					    $.ajax({
			   					        url:"/CWFM/contractworkmen/getAllContractors",
			   					        type: "GET",
			   					        data: {
			   					            unitId: unitId,
			   					            userAccount: userAccount
			   					        },
			   					        success: function (contractors) {

			   					            const $contractor = $("#contractors");
			   					            $contractor.empty();
			   					            $contractor.append('<option value="">Select Contractor</option>');

			   					            $.each(contractors, function (index, contractor) {
			   					                $contractor.append(
			   					                    '<option value="' + contractor.contractorId + '">' +
			   					                    contractor.contractorName +
			   					                    '</option>'
			   					                );
			   					            });

			   					            if (typeof callback === "function") {
			   					                callback();
			   					            }
			   					        },
			   					        error: function () {
			   					            console.error("Error loading contractors");
			   					        }
			   					    });
			   					}
                            function getContractorsForInactiveReports(unitId, userAccount, callback) {
			   					    $.ajax({
			   					        url:"/CWFM/contractworkmen/getAllContractors",
			   					        type: "GET",
			   					        data: {
			   					            unitId: unitId,
			   					            userAccount: userAccount
			   					        },
			   					        success: function (contractors) {

			   					            const $contractor = $("#contractors");
			   					            $contractor.empty();
			   					            $contractor.append('<option value="">Select Contractor</option>');

			   					            $.each(contractors, function (index, contractor) {
			   					                $contractor.append(
			   					                    '<option value="' + contractor.contractorId + '">' +
			   					                    contractor.contractorName +
			   					                    '</option>'
			   					                );
			   					            });

			   					            if (typeof callback === "function") {
			   					                callback();
			   					            }
			   					        },
			   					        error: function () {
			   					            console.error("Error loading contractors");
			   					        }
			   					    });
			   					}
                        function getContractorsForPolicyExpiryReports(unitId, userAccount, callback) {
			   					    $.ajax({
			   					        url:"/CWFM/contractworkmen/getAllContractors",
			   					        type: "GET",
			   					        data: {
			   					            unitId: unitId,
			   					            userAccount: userAccount
			   					        },
			   					        success: function (contractors) {

			   					            const $contractor = $("#contractors");
			   					            $contractor.empty();
			   					            $contractor.append('<option value="">Select Contractor</option>');

			   					            $.each(contractors, function (index, contractor) {
			   					                $contractor.append(
			   					                    '<option value="' + contractor.contractorId + '">' +
			   					                    contractor.contractorName +
			   					                    '</option>'
			   					                );
			   					            });

			   					            if (typeof callback === "function") {
			   					                callback();
			   					            }
			   					        },
			   					        error: function () {
			   					            console.error("Error loading contractors");
			   					        }
			   					    });
			   					}
			   					 function getContractorsForPoliceReports(unitId, userAccount, callback) {
			   					    $.ajax({
			   					        url:"/CWFM/contractworkmen/getAllContractors",
			   					        type: "GET",
			   					        data: {
			   					            unitId: unitId,
			   					            userAccount: userAccount
			   					        },
			   					        success: function (contractors) {

			   					            const $contractor = $("#contractors");
			   					            $contractor.empty();
			   					            $contractor.append('<option value="">Select Contractor</option>');

			   					            $.each(contractors, function (index, contractor) {
			   					                $contractor.append(
			   					                    '<option value="' + contractor.contractorId + '">' +
			   					                    contractor.contractorName +
			   					                    '</option>'
			   					                );
			   					            });

			   					            if (typeof callback === "function") {
			   					                callback();
			   					            }
			   					        },
			   					        error: function () {
			   					            console.error("Error loading contractors");
			   					        }
			   					    });
			   					}