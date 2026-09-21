<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ page isELIgnored="false" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="f"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<!DOCTYPE html>
<html lang="en">

<head>
<title>Export</title>
   <script src="resources/js/cms/export.js"></script>
  
    <style>
        /* Add your styles here */
        .success {
            color: green;
            font-weight: bold;
            padding: 10px;
            background-color: #e0ffe0;
            border: 1px solid green;
            margin-bottom: 1rem;
        }

        .error {
            color: red;
            font-weight: bold;
            padding: 10px;
            background-color: #ffe0e0;
            border: 1px solid red;
            margin-bottom: 1rem;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 10px;
            text-align: left;

            border: 1px solid #ddd;
        }

        th {
            background-color: #DDF3FF;
            color: #005151;
        }

        .checkbox-cell input[type="checkbox"] {
            margin: 0;
        }

        .action-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 1rem;
            background-color: #f8f8f8;
        }

        .action-buttons {
            display: flex;
            gap: 10px;
        }

        .action-buttons button {
            padding: 0.5rem 1rem;
            font-size: 1rem;
            cursor: pointer;
        }
         .success {
        color: green;
        font-weight: bold;
        padding: 10px;
        background-color: #e0ffe0;
        border: 1px solid green;
        margin-bottom: 1rem;
    }
    .error {
        color: red;
        font-weight: bold;
        padding: 10px;
        background-color: #ffe0e0;
        border: 1px solid red;
        margin-bottom: 1rem;
    }
 label {
    color: black;
}
    body {
        background-color: #FFFFFF; /* White background for the page */
        font-family: 'Volte Rounded', 'Noto Sans', sans-serif;
    }

    .action-bar {
        display: flex;
        justify-content: space-between;
        align-items: center;
        padding: 1rem;
        background-color: #f8f8f8;
        margin-bottom: 1rem;
    }

    .action-buttons {
        display: flex;
        gap: 10px;
    }

    .action-buttons button {
        padding: 0.5rem 1rem;
        font-size: 1rem;
        cursor: pointer;
    }

    #searchForm {
        display: flex;
        align-items: center;
        flex-grow: 1;
        margin-right: 10px;
    }

    .search-box {
        width: 200px; /* Adjust width to fit layout */
        padding: 0.25rem; /* Reduced padding for height */
        font-size: 0.875rem; /* Smaller font size */
        border: 1px solid #ccc; /* Border to match design */
        border-radius: 4px; /* Slightly rounded corners */
        outline: none; /* Remove default outline */
        margin-right: 10px; /* Space between input and button */
        box-sizing: border-box; /* Include padding and border in element's total width and height */
    }

    .table-container {
        overflow-x: auto;
        margin: 0; /* Remove space before the table */
        padding: 0; /* Remove padding if any */
    }

   /*  table {
        width: 100%;
        border-collapse: collapse;
    } */

    th, td {
        padding: 10px;
        text-align: left;
        border: 1px solid #ddd;
        font-size: 0.875rem; /* Smaller text size matching the side nav bar */
        color: grey;
    }
  td {
        padding: 10px;
        text-align: left;
        border: 1px solid #ddd;
        font-size: 0.875rem; /* Smaller text size matching the side nav bar */
         font-family: 'Noto Sans', sans-serif;
         
    color: #898989;/* Label text color */
  padding: .2em .6em .3em;
  font-size: 85%;
  font-weight: 700;
  line-height: 1;
    white-space: nowrap;
  vertical-align: baseline;
  border-radius: .25em;
    }
     th {
        padding: 10px;
        text-align: left;
        border: 1px solid #ddd;
        font-size: 0.875rem; /* Smaller text size matching the side nav bar */
          font-weight: bold;
    }

    th {
        background-color: #DDF3FF; /* Light green for the table header */
        color: #005151; /* Text color from side nav bar */
        cursor: pointer;
        font-family: 'Volte Rounded', 'Noto Sans', sans-serif;
        font-size: 0.75rem; /* Decreased font size for table header */
        line-height: 1.2rem; /* Adjust line-height for better fit */
        padding: 6px; /* Reduced padding for table header */
    }

    .page-header {
        display: flex;
        align-items: center;
        justify-content: flex-start; /* Align elements to the left */
    gap: 10px;  /* Distribute space between search and buttons */
        padding: 8px; /* Adjust padding */
        background-color: #FFFFFF; /* White background */
        border-bottom: 1px solid #ccc; /* Subtle border for separation */
    }

    .page-header > div {
        display: flex;
        gap: 10px; /* Space between buttons */
    }

    @media (max-width: 768px) {
        .page-header {
            flex-direction: column; /* Stack items vertically on small screens */
            align-items: flex-start; /* Align items to the start */
        }

        #searchForm {
            width: 100%; 
            margin-right: 0; /* Remove margin on small screens */
        }

        .search-box {
            width: 100%; /* Full width for small screens */
        }

        .page-header > div {
            width: 100%; /* Full width for small screens */
            margin-top: 10px; /* Add space above buttons */
            flex-direction: column; /* Stack buttons vertically */
        }
    }
    .header-text-new {
        font-family: 'Noto Sans', Arial, sans-serif; /* Font family similar to grid header */
        font-size: 14px; /* Adjusted font size to match typical grid header size */
        font-weight: 600; /* Bold text for prominence */
        border: 1px solid #ddd; /* Lighter border for a cleaner look */
        white-space: nowrap; /* Prevent text from wrapping */
        padding: 8px 10px; /* Adjusted padding for better spacing */
          background-color: #E0E0E0;  /* Light background color to match grid header */
        color: #333; /* Text color for readability */
    }
       table th {
        border-top: 0.0625rem solid var(--zed_sys_color_border_lowEmphasis); /* Top border color */
        border-bottom: 1px solid var(--zed_sys_color_border_lowEmphasis); /* Bottom border color */
        border-right: none; /* No right border */
        background-color: #DDF3FF; /* Light green background color */
        color: var(--zed_sys_color_tableHeader_text); /* Text color */
            font-size: 0.75rem;
        line-height: 1.2rem; /* Reduced line height */
        letter-spacing: normal; /* Letter spacing */
        font-family: 'Noto Sans', sans-serif; /* Font family */
         font-weight: bold;
        text-align: center; /* Center align text */
        padding: 4px; /* Reduced padding for the table header */
        box-sizing: border-box; /* Include padding and border in element's total width and height */
    }
    
    .error-row {
    background-color: #ffcccc !important;
}

.error-message-row .error-message-cell {
    color: red;
    font-weight: bold;
    font-size: 13px;
    padding: 6px 10px;
    background-color: #ffe5e5;
    border-top: none;
}
  .page-header-buttons {
    margin-left: auto;      /* <<< THIS moves the buttons to the right */
    display: flex;
    gap: 10px;
}  
    </style>	
  <script src="resources/js/cms/export.js"></script>
   <script src="resources/js/cms/workmen.js"></script>
</head>
<body>

   
<div class="page-header">
<input type="hidden" id="loggedInUserAccount" value="${sessionScope.loginuser.userAccount}">
  <label for="principalEmployerId" style="color: darkcyan;">PrincipalEmployer:</label> 
  <select class="custom-select" id="principalEmployers" name="principalEmployerId" onchange="getContractorsForReports(this.value, document.getElementById('loggedInUserAccount').value)" style="color:gray;padding:3px;">
                                <option value="">Please select Principal Employer</option>
                                
                                
                                
								<c:forEach var="pe" items="${PrincipalEmployer}">
								
                					<option value="${pe.id}"
									>
									${pe.description}</option>
            					</c:forEach>
							
                                </select>
                            
                          <label for="deptId" style="color: darkcyan;">Contractor:</label>
<input type="hidden" id="autoSearchFunction">
<input type="hidden" id="autoSearchParam" value="">
                            <select class="custom-select" id="contractors" name="contractors"  style="color:gray;padding:3px;">
            						<option value="">Please select Contractor</option>
									<c:forEach var="contr" items="${Contractors}">
										
                					<option value="${contr.contractorId}" >
									${contr.contractorName}</option>
            					</c:forEach>
        						</select>
        						
        						<label id="error-contractor"style="color: red;display: none;">Contractor is required</label>
        				<label for="startDate" style="color: darkcyan;">FromDate:</label>	<input id="startDate" name="startDate" class="datetimepickerformat2"  type="text" size="30" autocomplete="off">
        				<label for="endDate" style="color: darkcyan;">ToDate:</label>	<input id="endDate" name="endDate" class="datetimepickerformat2"  type="text" size="30" autocomplete="off">
    <button type="button" id="exportBtn"  class="btn btn-default process-footer-button-cancel ng-binding" onclick="fetchReportData()">Search</button>
   <div class="page-header-buttons">
       <button type="button" id="exportBtn"  class="btn btn-default process-footer-button-cancel ng-binding" onclick="reportModuleCSV()">Export</button> 
         <button type="button" class="btn btn-default process-footer-button-cancel ng-binding" onclick=" loadCommonList('/reports/list', 'Contract Workmen Report');">Cancel</button>
     </div>     
          <div id="formErrorMessage" class="error-message" style="display: none; color: red; font-weight: bold;"></div>
    </div>

 <!-- Dynamic Table -->
 <div class="table-container">
<table id="dynamicTable"  style="margin-top: 20px;width:100%;">
   <thead>
<tr>
    <th class="header-text">
        <input type="checkbox" id="selectAllBlockCheckbox" onchange="toggleAll(this)">
    </th>

    <th class="header-text">Gate Pass Type</th>
    <th class="header-text">Gate Pass Id</th>
    <th class="header-text">First Name</th>
    <th class="header-text">Last Name</th>
    <th class="header-text">Relative Name</th>
    <th class="header-text">DOJ</th>
    <th class="header-text">Birth Date</th>
    <th class="header-text">Phone 1</th>
    <th class="header-text">Address</th>
    <th class="header-text">Employment Status</th>
    <th class="header-text">DOT</th>
    <th class="header-text">Reasoning</th>
    <th class="header-text">Unit Code</th>
    <th class="header-text">Unit Name</th>
    <th class="header-text">Main Contractor Code</th>
    <th class="header-text">Main Contractor Name</th>
    <th class="header-text">Sub Contractor Code</th>
    <th class="header-text">Sub Contractor Name</th>
    <th class="header-text">SAP Workorder Number</th>
    <th class="header-text">Department</th>
    <th class="header-text">Section</th>
    <th class="header-text">Trade</th>
    <th class="header-text">Skill</th>
    <th class="header-text">Aadhar Number</th>
    <th class="header-text">Gender</th>
    <th class="header-text">Marital Status</th>
    <th class="header-text">Hazardous Area</th>
    <th class="header-text">Access Area</th>
    <th class="header-text">Technical</th>
    <th class="header-text">Academic</th>
    <th class="header-text">Blood Group</th>
    <th class="header-text">Accommodation</th>
    <th class="header-text">Bank Branch</th>
    <th class="header-text">Account Number</th>
    <th class="header-text">Mobile Number</th>
    <th class="header-text">EIC Manager</th>
    <th class="header-text">Insurance Type</th>
    <th class="header-text">WC ESIC Number</th>
    <th class="header-text">ESIC Number</th>
    <th class="header-text">LL Number</th>
    <th class="header-text">PF Applicable</th>
    <th class="header-text">PF Number</th>
    <th class="header-text">UAN Number</th>
    <th class="header-text">Health Check Date</th>
    <th class="header-text">Zone</th>
    <th class="header-text">PVS Doc Type</th>
    <th class="header-text">Emergency Contact Name</th>
    <th class="header-text">Emergency Contact Number</th>
</tr>
</thead>
    <tbody id="tableBody"></tbody>
</table>
</div>
</body>
</html>
