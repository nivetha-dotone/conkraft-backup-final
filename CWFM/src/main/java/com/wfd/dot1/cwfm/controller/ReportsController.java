package com.wfd.dot1.cwfm.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.wfd.dot1.cwfm.dto.ContractWorkmenReportDTO;
import com.wfd.dot1.cwfm.pojo.CmsGeneralMaster;
import com.wfd.dot1.cwfm.pojo.MasterUser;
import com.wfd.dot1.cwfm.pojo.PersonOrgLevel;
import com.wfd.dot1.cwfm.service.CommonService;
import com.wfd.dot1.cwfm.service.ContractorService;
import com.wfd.dot1.cwfm.service.WorkmenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/reports")
public class ReportsController {
	@Autowired
	WorkmenService workmenService;
	@Autowired
	CommonService commonService;
	@Autowired
	ContractorService contrService;
	@GetMapping("/list")
    public String list(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession(false); // Use `false` to avoid creating a new session
        MasterUser user = (MasterUser) (session != null ? session.getAttribute("loginuser") : null);
		List<CmsGeneralMaster> gmList = workmenService.getAllGeneralMaster();

		// Grouping the CmsGeneralMaster objects by gmType
		Map<String, List<CmsGeneralMaster>> groupedByGmType = gmList.stream()
		        .collect(Collectors.groupingBy(CmsGeneralMaster::getGmType));

		

	   	List<PersonOrgLevel> orgLevel = commonService.getPersonOrgLevelDetails(user.getUserAccount());
    	Map<String,List<PersonOrgLevel>> groupedByLevelDef = orgLevel.stream()
    			.collect(Collectors.groupingBy(PersonOrgLevel::getLevelDef));
    	List<PersonOrgLevel> peList = groupedByLevelDef.getOrDefault("Principal Employer", new ArrayList<>());
    	request.setAttribute("PrincipalEmployer", peList);
    	return "reports/contractWorkmenReport";
	 }
	
	@GetMapping("/inactiveWorkmenList")
    public String inactiveWorkmenList(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession(false); // Use `false` to avoid creating a new session
        MasterUser user = (MasterUser) (session != null ? session.getAttribute("loginuser") : null);
		List<CmsGeneralMaster> gmList = workmenService.getAllGeneralMaster();

		// Grouping the CmsGeneralMaster objects by gmType
		Map<String, List<CmsGeneralMaster>> groupedByGmType = gmList.stream()
		        .collect(Collectors.groupingBy(CmsGeneralMaster::getGmType));

		

	   	List<PersonOrgLevel> orgLevel = commonService.getPersonOrgLevelDetails(user.getUserAccount());
    	Map<String,List<PersonOrgLevel>> groupedByLevelDef = orgLevel.stream()
    			.collect(Collectors.groupingBy(PersonOrgLevel::getLevelDef));
    	List<PersonOrgLevel> peList = groupedByLevelDef.getOrDefault("Principal Employer", new ArrayList<>());
    	request.setAttribute("PrincipalEmployer", peList);
    	return "reports/inactiveWorkmenReport";
	 }
	
	@GetMapping("/policeverificationWorkmenList")
    public String policeverificationWorkmenList(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession(false); // Use `false` to avoid creating a new session
        MasterUser user = (MasterUser) (session != null ? session.getAttribute("loginuser") : null);
		List<CmsGeneralMaster> gmList = workmenService.getAllGeneralMaster();

		// Grouping the CmsGeneralMaster objects by gmType
		Map<String, List<CmsGeneralMaster>> groupedByGmType = gmList.stream()
		        .collect(Collectors.groupingBy(CmsGeneralMaster::getGmType));

		

	   	List<PersonOrgLevel> orgLevel = commonService.getPersonOrgLevelDetails(user.getUserAccount());
    	Map<String,List<PersonOrgLevel>> groupedByLevelDef = orgLevel.stream()
    			.collect(Collectors.groupingBy(PersonOrgLevel::getLevelDef));
    	List<PersonOrgLevel> peList = groupedByLevelDef.getOrDefault("Principal Employer", new ArrayList<>());
    	request.setAttribute("PrincipalEmployer", peList);
    	return "reports/policeverificationWorkmenReport";
	 }
	@GetMapping("/policysExpiryWorkmenList")
    public String policesWorkmenList(HttpServletRequest request,HttpServletResponse response) {
		HttpSession session = request.getSession(false); // Use `false` to avoid creating a new session
        MasterUser user = (MasterUser) (session != null ? session.getAttribute("loginuser") : null);
		List<CmsGeneralMaster> gmList = workmenService.getAllGeneralMaster();

		// Grouping the CmsGeneralMaster objects by gmType
		Map<String, List<CmsGeneralMaster>> groupedByGmType = gmList.stream()
		        .collect(Collectors.groupingBy(CmsGeneralMaster::getGmType));

		

	   	List<PersonOrgLevel> orgLevel = commonService.getPersonOrgLevelDetails(user.getUserAccount());
    	Map<String,List<PersonOrgLevel>> groupedByLevelDef = orgLevel.stream()
    			.collect(Collectors.groupingBy(PersonOrgLevel::getLevelDef));
    	List<PersonOrgLevel> peList = groupedByLevelDef.getOrDefault("Principal Employer", new ArrayList<>());
    	request.setAttribute("PrincipalEmployer", peList);
    	return "reports/workmenPolicysExpiryReport";
	 }
	
	@GetMapping("/fetchModuleData")
	@ResponseBody
	public Map<String, Object> fetchModuleData(@RequestParam String contractorId,
	                                           @RequestParam String unitId,
	                                           @RequestParam String reportType,
	                                           @RequestParam(value = "startDate", required = false) String startDate,
	                                           @RequestParam(value = "endDate", required = false) String endDate) {
	    Map<String, Object> response = new HashMap<>();
	    List<Map<String, String>> rows = new ArrayList<>();
	    List<String> columns = new ArrayList<>();

	    switch (reportType) {
        case "contractWorkmenReport":
	    
	    List<ContractWorkmenReportDTO> workmen =workmenService.getContractWorkmenReportData(unitId, contractorId,startDate,endDate);

	    columns = Arrays.asList("Gate Pass Type","Gate Pass Id","First Name","Last Name","Relative Name","DOJ","Birth Date","Phone 1","Address","Employment Status","DOT","Reasoning",
	            "Unit Code","Unit Name","Main Contractor Code","Main Contractor Name","Sub Contractor Code","Sub Contractor Name","SAP Workorder Number","Department","Section",
	            "Trade","Skill","Aadhar Number","Gender","Marital Status","Hazardous Area","Access Area","Technical","Academic","Blood Group","Accommodation",
	            "Bank Branch","Account Number","Mobile Number","EIC Manager","Insurance Type","WC ESIC Number","ESIC Number","LL Number","PF Applicable","PF Number","UAN Number",
	            "Health Check Date","Zone","PVS Doc Type","Emergency Contact Name","Emergency Contact Number");
	    for (ContractWorkmenReportDTO c : workmen) {
	        Map<String, String> row = new LinkedHashMap<>();

	        row.put("Gate Pass Type", value(c.getGatePassType()));
	        row.put("Gate Pass Id", value(c.getGatePassId()));
	        row.put("First Name", value(c.getFirstName()));
	        row.put("Last Name", value(c.getLastName()));
	        row.put("Relative Name", value(c.getRelativeName()));
	        row.put("DOJ", value(c.getDoj()));
	        row.put("Birth Date", value(c.getBirthDate()));
	        row.put("Phone 1", value(c.getPhone1()));
	        row.put("Address", value(c.getAddress()));
	        row.put("Employment Status", value(c.getEmploymentStatus()));
	        row.put("DOT", value(c.getDot()));
	        row.put("Reasoning", value(c.getReasoning()));
	        row.put("Unit Code", value(c.getUnitCode()));
	        row.put("Unit Name", value(c.getUnitName()));
	        row.put("Main Contractor Code", value(c.getMainContractorCode()));
	        row.put("Main Contractor Name", value(c.getMainContractorName()));
	        row.put("Sub Contractor Code", value(c.getSubContractorCode()));
	        row.put("Sub Contractor Name", value(c.getSubContractorName()));
	        row.put("SAP Workorder Number", value(c.getSapWorkOrderNum()));
	        row.put("Department", value(c.getDepartment()));
	        row.put("Section", value(c.getSection()));
	        row.put("Trade", value(c.getTrade()));
	        row.put("Skill", value(c.getSkill()));
	        row.put("Aadhar Number", value(c.getAadharNumber()));
	        row.put("Gender", value(c.getGender()));
	        row.put("Marital Status", value(c.getMaritalStatus()));
	        row.put("Hazardous Area", value(c.getHazardousArea()));
	        row.put("Access Area", value(c.getAccessAreaId()));
	        row.put("Technical", value(c.getTechnical()));
	        row.put("Academic", value(c.getAcademic()));
	        row.put("Blood Group", value(c.getBloodgroup()));
	        row.put("Accommodation", value(c.getAccommodation()));
	        row.put("Bank Branch", value(c.getBankbranch()));
	        row.put("Account Number", value(c.getAccountNumber()));
	        row.put("Mobile Number", value(c.getMobileNumber()));
	        row.put("EIC Manager", value(c.getEicmanager()));
	        row.put("Insurance Type", value(c.getInsuranceType()));
	        row.put("WC ESIC Number", value(c.getWcesicno()));
	        row.put("ESIC Number", value(c.getEsicNumber()));
	        row.put("LL Number", value(c.getLlNo()));
	        row.put("PF Applicable", value(c.getPfapplicable()));
	        row.put("PF Number", value(c.getPfNumber()));
	        row.put("UAN Number", value(c.getUanNumber()));
	        row.put("Health Check Date", value(c.getHealthCheckDate()));
	        row.put("Zone", value(c.getZone()));
	        row.put("PVS Doc Type", value(c.getPvsDocType()));
	        row.put("Emergency Contact Name", value(c.getEmergencyContactName()));
	        row.put("Emergency Contact Number", value(c.getEmergencyContactNumber()));

	        rows.add(row);
	    }
	    break;
	    
        case "inActiveWorkmenReport":
    	    
    	    List<ContractWorkmenReportDTO> inactiveWorkmen =workmenService.getInactiveWorkmenReportData(unitId, contractorId);

    	    columns = Arrays.asList("Gate Pass Type","Gate Pass Id","First Name","Last Name","Relative Name","DOJ","Birth Date","Phone 1","Address","Employment Status","DOT","Reasoning",
    	            "Unit Code","Unit Name","Main Contractor Code","Main Contractor Name","Sub Contractor Code","Sub Contractor Name","SAP Workorder Number","Department","Section",
    	            "Trade","Skill","Aadhar Number","Gender","Marital Status","Hazardous Area","Access Area","Technical","Academic","Blood Group","Accommodation",
    	            "Bank Branch","Account Number","Mobile Number","EIC Manager","Insurance Type","WC ESIC Number","ESIC Number","LL Number","PF Applicable","PF Number","UAN Number",
    	            "Health Check Date","Zone","PVS Doc Type","Emergency Contact Name","Emergency Contact Number");
    	    for (ContractWorkmenReportDTO c : inactiveWorkmen) {
    	        Map<String, String> row = new LinkedHashMap<>();

    	        row.put("Gate Pass Type", value(c.getGatePassType()));
    	        row.put("Gate Pass Id", value(c.getGatePassId()));
    	        row.put("First Name", value(c.getFirstName()));
    	        row.put("Last Name", value(c.getLastName()));
    	        row.put("Relative Name", value(c.getRelativeName()));
    	        row.put("DOJ", value(c.getDoj()));
    	        row.put("Birth Date", value(c.getBirthDate()));
    	        row.put("Phone 1", value(c.getPhone1()));
    	        row.put("Address", value(c.getAddress()));
    	        row.put("Employment Status", value(c.getEmploymentStatus()));
    	        row.put("DOT", value(c.getDot()));
    	        row.put("Reasoning", value(c.getReasoning()));
    	        row.put("Unit Code", value(c.getUnitCode()));
    	        row.put("Unit Name", value(c.getUnitName()));
    	        row.put("Main Contractor Code", value(c.getMainContractorCode()));
    	        row.put("Main Contractor Name", value(c.getMainContractorName()));
    	        row.put("Sub Contractor Code", value(c.getSubContractorCode()));
    	        row.put("Sub Contractor Name", value(c.getSubContractorName()));
    	        row.put("SAP Workorder Number", value(c.getSapWorkOrderNum()));
    	        row.put("Department", value(c.getDepartment()));
    	        row.put("Section", value(c.getSection()));
    	        row.put("Trade", value(c.getTrade()));
    	        row.put("Skill", value(c.getSkill()));
    	        row.put("Aadhar Number", value(c.getAadharNumber()));
    	        row.put("Gender", value(c.getGender()));
    	        row.put("Marital Status", value(c.getMaritalStatus()));
    	        row.put("Hazardous Area", value(c.getHazardousArea()));
    	        row.put("Access Area", value(c.getAccessAreaId()));
    	        row.put("Technical", value(c.getTechnical()));
    	        row.put("Academic", value(c.getAcademic()));
    	        row.put("Blood Group", value(c.getBloodgroup()));
    	        row.put("Accommodation", value(c.getAccommodation()));
    	        row.put("Bank Branch", value(c.getBankbranch()));
    	        row.put("Account Number", value(c.getAccountNumber()));
    	        row.put("Mobile Number", value(c.getMobileNumber()));
    	        row.put("EIC Manager", value(c.getEicmanager()));
    	        row.put("Insurance Type", value(c.getInsuranceType()));
    	        row.put("WC ESIC Number", value(c.getWcesicno()));
    	        row.put("ESIC Number", value(c.getEsicNumber()));
    	        row.put("LL Number", value(c.getLlNo()));
    	        row.put("PF Applicable", value(c.getPfapplicable()));
    	        row.put("PF Number", value(c.getPfNumber()));
    	        row.put("UAN Number", value(c.getUanNumber()));
    	        row.put("Health Check Date", value(c.getHealthCheckDate()));
    	        row.put("Zone", value(c.getZone()));
    	        row.put("PVS Doc Type", value(c.getPvsDocType()));
    	        row.put("Emergency Contact Name", value(c.getEmergencyContactName()));
    	        row.put("Emergency Contact Number", value(c.getEmergencyContactNumber()));

    	        rows.add(row);
    	    }
    	    break;
    	    
          case "policeVerificationWorkmenReport":
    	    
    	    List<ContractWorkmenReportDTO> policeVerificationWorkmen =workmenService.getPoliceverificationWorkmenReportData(unitId, contractorId);

    	    columns = Arrays.asList("Gate Pass Type","Gate Pass Id","First Name","Last Name","Relative Name","DOJ","Birth Date","Phone 1","Address","Employment Status","DOT","Reasoning",
    	            "Unit Code","Unit Name","Main Contractor Code","Main Contractor Name","Sub Contractor Code","Sub Contractor Name","SAP Workorder Number","Department","Section",
    	            "Trade","Skill","Aadhar Number","Gender","Marital Status","Hazardous Area","Access Area","Technical","Academic","Blood Group","Accommodation",
    	            "Bank Branch","Account Number","Mobile Number","EIC Manager","Insurance Type","WC ESIC Number","ESIC Number","LL Number","PF Applicable","PF Number","UAN Number",
    	            "Police Verification Date","Health Check Date","Zone","PVS Doc Type","Emergency Contact Name","Emergency Contact Number");
    	    for (ContractWorkmenReportDTO c : policeVerificationWorkmen) {
    	        Map<String, String> row = new LinkedHashMap<>();

    	        row.put("Gate Pass Type", value(c.getGatePassType()));
    	        row.put("Gate Pass Id", value(c.getGatePassId()));
    	        row.put("First Name", value(c.getFirstName()));
    	        row.put("Last Name", value(c.getLastName()));
    	        row.put("Relative Name", value(c.getRelativeName()));
    	        row.put("DOJ", value(c.getDoj()));
    	        row.put("Birth Date", value(c.getBirthDate()));
    	        row.put("Phone 1", value(c.getPhone1()));
    	        row.put("Address", value(c.getAddress()));
    	        row.put("Employment Status", value(c.getEmploymentStatus()));
    	        row.put("DOT", value(c.getDot()));
    	        row.put("Reasoning", value(c.getReasoning()));
    	        row.put("Unit Code", value(c.getUnitCode()));
    	        row.put("Unit Name", value(c.getUnitName()));
    	        row.put("Main Contractor Code", value(c.getMainContractorCode()));
    	        row.put("Main Contractor Name", value(c.getMainContractorName()));
    	        row.put("Sub Contractor Code", value(c.getSubContractorCode()));
    	        row.put("Sub Contractor Name", value(c.getSubContractorName()));
    	        row.put("SAP Workorder Number", value(c.getSapWorkOrderNum()));
    	        row.put("Department", value(c.getDepartment()));
    	        row.put("Section", value(c.getSection()));
    	        row.put("Trade", value(c.getTrade()));
    	        row.put("Skill", value(c.getSkill()));
    	        row.put("Aadhar Number", value(c.getAadharNumber()));
    	        row.put("Gender", value(c.getGender()));
    	        row.put("Marital Status", value(c.getMaritalStatus()));
    	        row.put("Hazardous Area", value(c.getHazardousArea()));
    	        row.put("Access Area", value(c.getAccessAreaId()));
    	        row.put("Technical", value(c.getTechnical()));
    	        row.put("Academic", value(c.getAcademic()));
    	        row.put("Blood Group", value(c.getBloodgroup()));
    	        row.put("Accommodation", value(c.getAccommodation()));
    	        row.put("Bank Branch", value(c.getBankbranch()));
    	        row.put("Account Number", value(c.getAccountNumber()));
    	        row.put("Mobile Number", value(c.getMobileNumber()));
    	        row.put("EIC Manager", value(c.getEicmanager()));
    	        row.put("Insurance Type", value(c.getInsuranceType()));
    	        row.put("WC ESIC Number", value(c.getWcesicno()));
    	        row.put("ESIC Number", value(c.getEsicNumber()));
    	        row.put("LL Number", value(c.getLlNo()));
    	        row.put("PF Applicable", value(c.getPfapplicable()));
    	        row.put("PF Number", value(c.getPfNumber()));
    	        row.put("UAN Number", value(c.getUanNumber()));
    	        row.put("Police Verification Date", value(c.getPoliceverificationDate()));
    	        row.put("Health Check Date", value(c.getHealthCheckDate()));
    	        row.put("Zone", value(c.getZone()));
    	        row.put("PVS Doc Type", value(c.getPvsDocType()));
    	        row.put("Emergency Contact Name", value(c.getEmergencyContactName()));
    	        row.put("Emergency Contact Number", value(c.getEmergencyContactNumber()));

    	        rows.add(row);
    	    }
    	    break;
    	    
          case "policyExpiryWorkmenReport":
      	    
      	    List<ContractWorkmenReportDTO> policyExpiryWorkmen =workmenService.getPolicyExpiryWorkmenReportData(unitId, contractorId);

      	    columns = Arrays.asList("Gate Pass Type","Gate Pass Id","First Name","Last Name","Relative Name","DOJ","Birth Date","Phone 1","Address","Employment Status","DOT","Reasoning",
      	            "Unit Code","Unit Name","Main Contractor Code","Main Contractor Name","Sub Contractor Code","Sub Contractor Name","SAP Workorder Number","Workorder Expiry",
      	            "Insurance Type","WC ESIC Number","WC ESIC Expiry","LL Number","LL Expiry","ESIC Number","Department","Section",
      	            "Trade","Skill","Aadhar Number","Gender","Marital Status","Hazardous Area","Access Area","Technical","Academic","Blood Group","Accommodation",
      	            "Bank Branch","Account Number","Mobile Number","EIC Manager","PF Applicable","PF Number","UAN Number",
      	            "Health Check Date","Zone","PVS Doc Type","Emergency Contact Name","Emergency Contact Number");
      	    for (ContractWorkmenReportDTO c : policyExpiryWorkmen) {
      	        Map<String, String> row = new LinkedHashMap<>();

      	        row.put("Gate Pass Type", value(c.getGatePassType()));
      	        row.put("Gate Pass Id", value(c.getGatePassId()));
      	        row.put("First Name", value(c.getFirstName()));
      	        row.put("Last Name", value(c.getLastName()));
      	        row.put("Relative Name", value(c.getRelativeName()));
      	        row.put("DOJ", value(c.getDoj()));
      	        row.put("Birth Date", value(c.getBirthDate()));
      	        row.put("Phone 1", value(c.getPhone1()));
      	        row.put("Address", value(c.getAddress()));
      	        row.put("Employment Status", value(c.getEmploymentStatus()));
      	        row.put("DOT", value(c.getDot()));
      	        row.put("Reasoning", value(c.getReasoning()));
      	        row.put("Unit Code", value(c.getUnitCode()));
      	        row.put("Unit Name", value(c.getUnitName()));
      	        row.put("Main Contractor Code", value(c.getMainContractorCode()));
      	        row.put("Main Contractor Name", value(c.getMainContractorName()));
      	        row.put("Sub Contractor Code", value(c.getSubContractorCode()));
      	        row.put("Sub Contractor Name", value(c.getSubContractorName()));
      	        row.put("SAP Workorder Number", value(c.getSapWorkOrderNum()));
      	        row.put("Workorder Expiry", value(c.getWorkorderExpiryDate()));
      	        row.put("Insurance Type", value(c.getInsuranceType()));
    	        row.put("WC ESIC Number", value(c.getWcesicno()));
    	        row.put("WC ESIC Expiry", value(c.getWcesicExpiryDate()));
    	        row.put("LL Number", value(c.getLlNo()));
    	        row.put("LL Expiry", value(c.getLicenseExpiryDate()));
    	        row.put("ESIC Number", value(c.getEsicNumber()));
      	        row.put("Department", value(c.getDepartment()));
      	        row.put("Section", value(c.getSection()));
      	        row.put("Trade", value(c.getTrade()));
      	        row.put("Skill", value(c.getSkill()));
      	        row.put("Aadhar Number", value(c.getAadharNumber()));
      	        row.put("Gender", value(c.getGender()));
      	        row.put("Marital Status", value(c.getMaritalStatus()));
      	        row.put("Hazardous Area", value(c.getHazardousArea()));
      	        row.put("Access Area", value(c.getAccessAreaId()));
      	        row.put("Technical", value(c.getTechnical()));
      	        row.put("Academic", value(c.getAcademic()));
      	        row.put("Blood Group", value(c.getBloodgroup()));
      	        row.put("Accommodation", value(c.getAccommodation()));
      	        row.put("Bank Branch", value(c.getBankbranch()));
      	        row.put("Account Number", value(c.getAccountNumber()));
      	        row.put("Mobile Number", value(c.getMobileNumber()));
      	        row.put("EIC Manager", value(c.getEicmanager()));
      	        row.put("PF Applicable", value(c.getPfapplicable()));
      	        row.put("PF Number", value(c.getPfNumber()));
      	        row.put("UAN Number", value(c.getUanNumber()));
      	        row.put("Health Check Date", value(c.getHealthCheckDate()));
      	        row.put("Zone", value(c.getZone()));
      	        row.put("PVS Doc Type", value(c.getPvsDocType()));
      	        row.put("Emergency Contact Name", value(c.getEmergencyContactName()));
      	        row.put("Emergency Contact Number", value(c.getEmergencyContactNumber()));

      	        rows.add(row);
      	    }
      	    break;
	    }
       
	    response.put("columns", columns);
	    response.put("rows", rows);
	    return response;
	}

	private String value(String input) {
	    return input == null ? "" : input;
	}
}
