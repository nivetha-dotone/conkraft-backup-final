package com.wfd.dot1.cwfm.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.wfd.dot1.cwfm.controller.CreateEmpFetchByGatePassAPICALL;
import com.wfd.dot1.cwfm.dao.DepartmentMappingDao;
import com.wfd.dot1.cwfm.dao.FileUploadDao;
import com.wfd.dot1.cwfm.dao.WorkmenBulkUploadDao;
import com.wfd.dot1.cwfm.dao.WorkmenDao;
import com.wfd.dot1.cwfm.dto.MinimumWageDTO;
import com.wfd.dot1.cwfm.enums.DotType;
import com.wfd.dot1.cwfm.enums.EmployeeStatusType;
import com.wfd.dot1.cwfm.enums.GatePassStatus;
import com.wfd.dot1.cwfm.enums.GatePassType;
import com.wfd.dot1.cwfm.pojo.BulkCancel;
import com.wfd.dot1.cwfm.pojo.BulkRenew;
import com.wfd.dot1.cwfm.pojo.CMSContrPemm;
import com.wfd.dot1.cwfm.pojo.CMSSubContractor;
import com.wfd.dot1.cwfm.pojo.CMSVendor;
import com.wfd.dot1.cwfm.pojo.CMSWorkorderLLWC;
import com.wfd.dot1.cwfm.pojo.CmsContractorWC;
import com.wfd.dot1.cwfm.pojo.CmsGeneralMaster;
import com.wfd.dot1.cwfm.pojo.Contractor;
import com.wfd.dot1.cwfm.pojo.DeptMapping;
import com.wfd.dot1.cwfm.pojo.GatePassMain;
import com.wfd.dot1.cwfm.pojo.KTCWorkorderStaging;
import com.wfd.dot1.cwfm.pojo.PersonOrgLevel;
import com.wfd.dot1.cwfm.pojo.PrincipalEmployer;
import com.wfd.dot1.cwfm.pojo.UserImport;
import com.wfd.dot1.cwfm.pojo.WorkmenBulkUpload;
import com.wfd.dot1.cwfm.util.QueryFileWatcher;

@Service
public class FileUploadServiceImpl implements FileUploadService {
	
	private static final Logger log = LoggerFactory.getLogger(FileUploadServiceImpl.class);
    @Autowired
    private FileUploadDao fileUploadDao;

    @Autowired
	DepartmentMappingDao deptMapDao;
    
    @Autowired
	CommonService commonService;
    
	@Autowired
    private WorkmenBulkUploadDao workmenUploadDao;
	
	@Autowired
	WorkmenDao workmenDao;
	 @Autowired
		CreateEmpFetchByGatePassAPICALL api;
	
    @Override
    public void uploadFiles(List<MultipartFile> files) {
        for (MultipartFile file : files) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    // Assuming each line represents a record to save in DB
                    String[] data = line.split(",");
                    fileUploadDao.saveData(data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
	/*
	 * private static final List<String> EXPECTED_COLUMNS = Arrays.asList("GMTYPE",
	 * "MASTERNAME", "MASTERVALUE");
	 */

    @Override
    public Map<String, Object> processTemplateFile(MultipartFile file, String templateType,String createdBy,String userAccount) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        String headerLine = reader.readLine();

        Map<String, Object> savedData = new HashMap<>();

        switch (templateType) {
            case "Data-General Master":
                if (!headerLine.equalsIgnoreCase("GM Name,GM Description,GM Type")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processGeneralMaster(reader);
                break;
                
            case "Data-Trade Skill":
                if (!headerLine.equalsIgnoreCase("Plant Code,Trade,Skill,Workmen Count")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processTradeSkillUnitMapping(reader);
                break;
                
            case "Data-Department Area":
                if (!headerLine.equalsIgnoreCase("Plant Code,Department,Sub Department")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processDepartmentSubDepartmentUnitMapping(reader);
                break;
                
            case "Data-Contractor":
                if (!headerLine.equalsIgnoreCase("Work Order Number,Plant Code,Organisation,Main Contractor Code,Contractor Code,Contractor Name,Contractor Address,City,Contractor Manager Name,Total Workmen Strength,Maximum Number of Workmen,Labour License Number,License Valid From,License Valid To,"
                        + "License Coverage,WC Number,WC Valid From,WC Valid To,WC Coverage,ESIC Number,ESIC Valid From,Nature of Work,"
                        + "PF Number,PF Apply Date")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                 savedData = processContractor(reader);
                break;
            case "Data-Work Order":
                if (!headerLine.equalsIgnoreCase("Work Order Number,Item,Line,Line Number,Service Code,Short Text,Delivery Completion,Item Changed On,Vendor Code,Vendor Name,Vendor Address,Blocked Vendor,Work Order Validity From,Work Order Validity To,Work Order Type,Plant Code,Section Code,Department Code,G/L Code,Cost Center,Nature of Job,Rate/Unit,Quantity,Base Unit of Measure,Work Order Released,PM Order No,WBS Element,Qty Completed,Work Order Release Date,Service Entry Created Date,Service Entry Updated Date,Purchase Org Level,Company Code")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processWorkorder(reader);
                break;
            case "Data-Minimum Wage":
                if (!headerLine.equalsIgnoreCase("Unit Code,State Name,Zone Name,Skill Name,Basic,DA,Other Allowances,From Date")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                 savedData = processMinimumWage(reader);
                break;
            case "Data-Principal Employer":
                if (!headerLine.equalsIgnoreCase("Organization,Plant Code,Name,Address,Manager Name,Manager Address,Business Type,Max Workmen,Max Contract Workmen,BOCW Applicability,"
                        + "Is MW Applicability,License Number,PF Code,ESWC,Factory License Number,State")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                 savedData = processPrincipalEmployer(reader, createdBy);
                break;
            case "Data-Workmen Bulk Upload":
                if (!headerLine.equalsIgnoreCase("First Name*,Last Name*,Father's Name or Husband's Name*,Date of Birth*,Trade*,Skill*,Nature of Work*,Hazardous Area*,"
                		+ "Aadhar/Id Proof Number*,Vendor Code*,Gender*,Date of Joining,Department*,Area,Work Order Number*,PF A/C Number,Marital Status*,"
                		+ "Technical/Non Technical*,Academic,Blood Group,Accommodation*,Bank Branch Name,Account Number,"
                		+ "Mobile Number,Emergency Contact Number*,Police Verification Date,Health Chekup Date,Access Levels*,ESIC Number,Unit Code*,Organization Name,"
                		+ "EIC Number*,EC Number*,UAN Number,Emergency Contact Person*,Is Eligible for PF,SpecializationName,Insurance Type,LL Number,Address*,Zone,IdMark*,Employee Code")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processworkmenbulkupload(reader);
                break;
            case "Data-Workmen Bulk Upload Draft":
                if (!headerLine.equalsIgnoreCase("First Name,Last Name,Father's Name or Husband's Name,Date of Birth,Trade,Skill,Nature of Work,Hazardous Area,"
                		+ "Aadhar/Id Proof Number,Vendor Code,Gender,Date of Joining,Department,Area,Work Order Number,PF A/C Number,Marital Status,"
                		+ "Technical/Non Technical,Academic,Blood Group,Accommodation,Bank Branch Name,Account Number,"
                		+ "Mobile Number,Emergency Contact Number,Police Verification Date,Health Chekup Date,Access Levels,ESIC Number,Unit Code,Organization Name,"
                		+ "EIC Number,EC Number,UAN Number,Emergency Contact Person,Is Eligible for PF,SpecializationName,Insurance Type,LL Number,Address,Zone,IdMark")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processworkmenbulkuploaddraft(reader);
                break;
            case "Data-Bulk Cancel":
                if (!headerLine.equalsIgnoreCase("Gatepass Number,Bulk Cancel Reason")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processBulkCancel(reader, userAccount,createdBy);
                break;
            case "Data-Bulk Renew":
                if (!headerLine.equalsIgnoreCase("Gatepass Number,WorkOrder Number,WC/ESIC Number,LL Number")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processBulkRenew(reader, userAccount,createdBy);
                break;
            case "Data-User":
                if (!headerLine.equalsIgnoreCase("First Name,Last Name,Login Id,Password,Email Address,Mobile Number,Plant Code,Organisation,Department,Area,Role,SAP Vendor Code")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processUser(reader);
                break;
            case "Data-Intra Plant Transfer":
                if (!headerLine.equalsIgnoreCase("GatepassId,Plant Code,Contractor Code,Department,Area,EIC Number,Workorder,WC/ESIC,LL Number,ESIC,Effective From Date")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                 savedData = processIntraPlantTransfer(reader,userAccount,createdBy);
                 break;
            case "Data-Safety Training":
                if (!headerLine.equalsIgnoreCase("Plant Code,Department,Training Type,Training Name")) {
                    throw new Exception("File can not upload due to incorrect format.");
                }
                savedData = processSafetyTraining(reader, userAccount,createdBy);
                break;
            default:
                throw new Exception("Unsupported template type: " + templateType);
        }

        // Use mutable map instead of Map.of
        Map<String, Object> result = new HashMap<>();
        result.put("status", "success");
        result.put("message", "File uploaded successfully!");
        result.put("data", savedData);
        return result;
    }

    public String getWFDIntegration() {
		return QueryFileWatcher.getQuery("TRADE_SKILL_WFD_INTEGRATION");
	}
    
    public String getWFDIntegrationForBULKCancel() {
		return QueryFileWatcher.getQuery("WFD_INTEGRATION");
	}
    private Map<String, Object> processTradeSkillUnitMapping(BufferedReader reader) throws IOException {
        List<Map<String, Object>> successData = new ArrayList<>();
        List<Map<String, Object>> errorData = new ArrayList<>();

        String line;
        int rowNum = 0;

        String[] fieldNames = {"principalEmployer", "trade", "skill","workmenCount"};
        Set<String> mandatoryFields = Set.of("principalEmployer", "trade", "skill");

        while ((line = reader.readLine()) != null) {
            rowNum++;
            line = line.replaceAll("[\\x00-\\x1F\\x7F]", ""); // Clean control characters

            if (line.trim().isEmpty()) continue;

            String[] rawFields = line.split(",", -1);
            String[] fields = new String[rawFields.length];
            for (int i = 0; i < rawFields.length; i++) {
                fields[i] = rawFields[i].trim().replaceAll("\"", "");
            }

            Map<String, String> fieldErrors = new LinkedHashMap<>();

            if (fields.length < 3) {
                errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                continue;
            }

//            for (int i = 0; i < fieldNames.length; i++) {
//                if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
//                    fieldErrors.put(fieldNames[i], "is mandatory");
//                }
//            }
            for (int i = 0; i < fieldNames.length; i++) {
                String fieldName = fieldNames[i];
                String value = fields[i];

                if (mandatoryFields.contains(fieldName) && value.isBlank()) {
                    fieldErrors.put(fieldName, "is mandatory");
                    continue;
                }

                // 🔴 Reject special characters
                if ((fieldName.equals("trade") || fieldName.equals("skill"))
                        && !value.isBlank()
                        && !isValidAlphaNumeric(value)) {
                    fieldErrors.put(fieldName, "invalid value (enter valid value)");
                }
                
                // Workmen Count validation
                if (fieldName.equals("workmenCount") && !value.isBlank() && !value.matches("\\d+")) {
                    fieldErrors.put(fieldName,"workmenCount only allow numeric value");
                }
            }
            if (!fieldErrors.isEmpty()) {
                errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                continue;
            }

            try {
                String principalEmployer = fields[0];
                String trade = fields[1];
                String skill = fields[2];
                String workmenCount = fields[3];

                // Get unitId from plant code
                Integer unitId = fileUploadDao.getUnitIdByName(principalEmployer);
                if (unitId == null) {
                    errorData.add(Map.of("row", rowNum, "error", "PlantCode '" + principalEmployer + "' not found"));
                    continue;
                }

                // Check or insert Trade
                Integer tradeId = fileUploadDao.getGeneralMasterId("Trade", trade);
                if (tradeId == null || tradeId == 0) {
                    tradeId = fileUploadDao.insertGeneralMaster("Trade", trade);
                }

                // Check or insert Skill
                Integer skillId = fileUploadDao.getGeneralMasterId("Skill", skill);
                if (skillId == null || skillId == 0) {
                    skillId = fileUploadDao.insertGeneralMaster("Skill", skill);
                    //make api call to wfd
                    String wfdIntegration = this.getWFDIntegration();
                	if("yes".equalsIgnoreCase(wfdIntegration)) {
                		api.postSkills(skillId);
                	}
                }

                // Check if mapping exists
                boolean mappingExists = fileUploadDao.existsUnitTradeSkillMapping(unitId, tradeId, skillId);
                if (mappingExists) {
                    errorData.add(Map.of("row", rowNum, "error", "Mapping already exists for '"+trade+"' and '"+skill+"' with "+principalEmployer));
                    continue;
                }

                // Insert mapping
                fileUploadDao.insertUnitTradeSkillMapping(unitId, tradeId, skillId,workmenCount);

                // ✅ Add success record (like PrincipalEmployer)
                Map<String, Object> success = new LinkedHashMap<>();
                success.put("row", rowNum);
                success.put("plantCode", principalEmployer);
                success.put("trade", trade);
                success.put("skill", skill);
                success.put("workmenCount", workmenCount);
                //success.put("message", "Mapping inserted successfully");

                successData.add(success);

            } catch (Exception e) {
                errorData.add(Map.of("row", rowNum, "error", "Exception while processing row: " + e.getMessage()));
                e.printStackTrace();
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("successData", successData);
        result.put("errorData", errorData);
        return result;
    }
    
    @Transactional
    private Map<String, Object> processDepartmentSubDepartmentUnitMapping(BufferedReader reader) throws IOException {
        List<Map<String, Object>> successData = new ArrayList<>();
        List<Map<String, Object>> errorData = new ArrayList<>();
        
        List<DeptMapping> deptList = new ArrayList<>();
        List<DeptMapping> areaList = new ArrayList<>();
        
        Set<String> uniqueDepartments = new HashSet<>();
        Set<String> uniqueAreas = new HashSet<>();
        String line;
        int rowNum = 0;

        String[] fieldNames = {"principalEmployer", "department", "subDepartment"};
        Set<String> mandatoryFields = Set.of("principalEmployer", "department","subDepartment");

        while ((line = reader.readLine()) != null) {
            rowNum++;
            line = line.replaceAll("[\\x00-\\x1F\\x7F]", ""); // Clean control characters

            if (line.trim().isEmpty()) continue;

            String[] rawFields = line.split(",", -1);
            String[] fields = new String[rawFields.length];
            for (int i = 0; i < rawFields.length; i++) {
                fields[i] = rawFields[i].trim().replaceAll("\"", "");
            }

            Map<String, String> fieldErrors = new LinkedHashMap<>();

            if (fields.length < 3) {
                errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                continue;
            }

            for (int i = 0; i < fieldNames.length; i++) {
                String fieldName = fieldNames[i];
                String value = fields[i];

                if (mandatoryFields.contains(fieldName) && value.isBlank()) {
                    fieldErrors.put(fieldName, "is mandatory");
                    continue;
                }

                // 🔴 Reject special characters
                if ((fieldName.equals("department") || fieldName.equals("subDepartment"))
                        && !value.isBlank()
                        && !isValidAlphaNumeric(value)) {
                    fieldErrors.put(fieldName, "invalid value (enter valid value)");
                }
            }

            if (!fieldErrors.isEmpty()) {
                errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                continue;
            }

            try {
                String principalEmployer = fields[0];
                String department = fields[1];
                String subDepartment = fields[2];

                // Get unitId from plant code
                Integer unitId = fileUploadDao.getUnitIdByName(principalEmployer);
                if (unitId == null) {
                    errorData.add(Map.of("row", rowNum, "error", "PlantCode '" + principalEmployer + "' not found"));
                    continue;
                }

                // Check or insert department
                Integer departmentId = fileUploadDao.getGeneralMasterId("Department", department);
                if (departmentId == null || departmentId == 0) {
                	departmentId = fileUploadDao.insertGeneralMaster("Department", department);
                }

                // Check or insert subDepartment
                Integer subDepartmentId = 0; // default
                if (subDepartment != null) {
                    subDepartmentId = fileUploadDao.getGeneralMasterId("Area", subDepartment);
                    if (subDepartmentId == null || subDepartmentId == 0) {
                        subDepartmentId = fileUploadDao.insertGeneralMaster("Area", subDepartment);
                    }
                }

                // Check if mapping exists
                boolean mappingExists = deptMapDao.trioexistsMapping(unitId, departmentId,subDepartmentId);
                if (mappingExists) {
                    errorData.add(Map.of("row", rowNum, "error", "Mapping already exists for '" +department+"' and '"+subDepartment+"' with "+principalEmployer));
                    continue;
                }

             // ✅ Additional check: Only PlantCode + Department (ignore subDepartment)
               // boolean deptOnlyExists = deptMapDao.existsMapping(unitId, departmentId);
               // if (deptOnlyExists && subDepartment == null) {
               //     errorData.add(Map.of("row", rowNum, "error",
               //             "Mapping already exists for PlantCode '" + principalEmployer + "' and Department '" + department + "' (without SubDepartment)"));
                //    continue;
               // }
                
                // Insert mapping
                fileUploadDao.insertUnitDepartmentSubDepartmentMapping(unitId, departmentId, subDepartmentId);
                
				
                // Collect Unique Department
                if (uniqueDepartments.add(department.toLowerCase())) {
                    DeptMapping d = new DeptMapping();
                    d.setDepartment(department);
                    deptList.add(d);
                }

                // Collect Unique Area
                if (subDepartment != null && uniqueAreas.add(subDepartment.toLowerCase())) {
                    DeptMapping a = new DeptMapping();
                    a.setSubDepartment(subDepartment);
                    areaList.add(a);
                }
                // ✅ Add success record (like PrincipalEmployer)
                Map<String, Object> success = new LinkedHashMap<>();
                success.put("row", rowNum);
                success.put("plantCode", principalEmployer);
                success.put("department", department);
                success.put("subDepartment", subDepartment);
                //success.put("message", "Mapping inserted successfully");

                successData.add(success);

            } catch (Exception e) {
                errorData.add(Map.of("row", rowNum, "error", "Exception while processing row: " + e.getMessage()));
                e.printStackTrace();
            }
           // batchInsertDeptOrgLevelEntry(deptList, "Department");
           // batchInsertDeptOrgLevelEntry(areaList, "Area");
        }
        batchInsertDeptOrgLevelEntry(deptList, "Department");
        batchInsertDeptOrgLevelEntry(areaList, "Area");
        
        Map<String, Object> result = new HashMap<>();
        result.put("successData", successData);
        result.put("errorData", errorData);
        return result;
    }
    
//    private boolean isValidAlphaNumeric(String value) {
//        return value != null && value.matches("^(?=.*[A-Za-z0-9])[A-Za-z0-9 &\\-/]+$");
//    }
    
    private boolean isValidAlphaNumeric(String value) {
        return value != null && value.matches("^[A-Za-z0-9 ]+$");
    }

    private Map<String, Object> processGeneralMaster(BufferedReader reader) throws IOException {
    	 List<Map<String, Object>> successData = new ArrayList<>();
         List<Map<String, Object>> errorData = new ArrayList<>();

         String line;
         int rowNum = 0;
         String[] fieldNames = {"gmName", "gmDescription", "gmType"};
                
         Set<String> mandatoryFields = Set.of("gmName", "gmDescription", "gmType");

             while ((line = reader.readLine()) != null) {
                 rowNum++;
                 line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");

                 if (line.trim().isEmpty()) continue;

                 String[] rawFields = line.split(",", -1);
                 String[] fields = new String[rawFields.length];
                 for (int i = 0; i < rawFields.length; i++) {
                     fields[i] = rawFields[i].trim().replaceAll("\"", "");
                 }

                 Map<String, String> fieldErrors = new LinkedHashMap<>();

                 if (fields.length < 3) {
                     errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                     continue;
                 }

                 // Check mandatory fields
                 for (int i = 0; i < fieldNames.length; i++) {
                     if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
                         fieldErrors.put(fieldNames[i], "is mandatory");
                     }
                 }

                 if (!fieldErrors.isEmpty()) {
                     errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                     continue;
                 }
                 String gmName = toTitleCase(fields[0]);
                 String gmDescription = toTitleCase(fields[1]);
                 String gmType = fields[2];
                 
                 //find gmtypeId
                  Integer gmTypeId = fileUploadDao.getGmTypeId(gmType);
                  if(gmTypeId == null || gmTypeId ==0) {
                	  errorData.add(Map.of("row", rowNum,"error", "GMType: " + gmType + " is not Found"));
                          continue;
                  }
            	// Duplicate check for isGmNameGmDescriptionExists
                 if (fileUploadDao.isGmNameGmTypeExists(gmName,gmTypeId)) {
                     errorData.add(Map.of("row", rowNum,"error", "Duplicate GMName: " + gmName + " Found for: "+ gmType));
                     continue;
                 }
                 if (!fieldErrors.isEmpty()) {
                     errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                     continue;
                 }
                 
             try {  
                	 CmsGeneralMaster gm = new CmsGeneralMaster();
                	 gm.setGmName(gmName);
                	 gm.setGmDescription(gmDescription);
                	 gm.setGmTypeId(gmTypeId);
                	 
                    fileUploadDao.saveGeneralMaster(gm);

            // Map each POJO to a simple map for JSON-friendly structure
            Map<String, Object> map = new HashMap<>();
            map.put("gmName", gmName);
            map.put("gmDescription", gmDescription);
            map.put("gmType",gmType);
            
            successData.add(map);

                 } catch (Exception e) {
                     errorData.add(Map.of("row", rowNum, "error", "Exception while processing row: " + e.getMessage()));
                 }
             }

             Map<String, Object> result = new HashMap<>();
             result.put("successData", successData);
             result.put("errorData", errorData);
             return result;
    }
    
    private String toTitleCase(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }
        // If already ALL CAPS, return as-is
        if (input.equals(input.toUpperCase())) {
            return input;
        }
        StringBuilder result = new StringBuilder();
        String[] words = input.toLowerCase().split("\\s+");

        for (String word : words) {

            // Split by both '-' and '_' while keeping delimiters
            String[] parts = word.split("(?=[-_])|(?<=[-_])");

            for (String part : parts) {
                if (part.equals("-") || part.equals("_")) {
                    result.append(part); // keep delimiter as-is
                } else if (!part.isEmpty()) {
                    result.append(Character.toUpperCase(part.charAt(0)))
                          .append(part.substring(1));
                }
            }
            result.append(" ");
        }

        return result.toString().trim();
    }
    
    private Map<String, Object> processMinimumWage(BufferedReader reader) throws IOException {
    	  List<Map<String, Object>> successData = new ArrayList<>();
          List<Map<String, Object>> errorData = new ArrayList<>();

          String line;
          int rowNum = 0;
          List<MinimumWageDTO> MinimumWageDTO = new ArrayList<>();
          String[] fieldNames = {"unitCode", "stateName", "zoneName", "skillName", "basic", "da", "otherAllowance",
                                 "fromDate"};

          Set<String> mandatoryFields = Set.of("unitCode", "stateName", "zoneName", "skillName", "basic", 
        		                    "da", "otherAllowance","fromDate");


          while ((line = reader.readLine()) != null) {
              rowNum++;
              line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");
              if (line.trim().isEmpty()) continue;

              String[] rawFields = line.split(",", -1);
              String[] fields = new String[rawFields.length];
              for (int i = 0; i < rawFields.length; i++) {
                  fields[i] = rawFields[i].trim().replaceAll("\"", "");
              }

              Map<String, String> fieldErrors = new LinkedHashMap<>();

              if (fields.length < fieldNames.length) {
                  errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                  continue;
              }

              // Check mandatory fields
              for (int i = 0; i < fieldNames.length; i++) {
                  if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
                      fieldErrors.put(fieldNames[i], "is mandatory");
                  }
              }
              String unitCode = fields[0];
              String stateName = fields[1];
              String zoneName = fields[2];
              String skillName = fields[3];

              Date fromDate = parseDateStrict(fields[7], "fromDate", fieldErrors);
             // Date toDate = parseDateStrict(fields[8], "toDate", fieldErrors);

              BigDecimal basic = null;
              BigDecimal da = null;
              BigDecimal otherAllowance = null;

              /* ---------- UNIT CODE VALIDATION ---------- */
              Integer unitId = null;
              if (!unitCode.isBlank()) {
                  unitId = fileUploadDao.getUnitIdByName(unitCode);
                  if (unitId == null) {
                      fieldErrors.put("unitCode", "UnitCode not found");
                  }
              }

              /* ---------- STATE VALIDATION ---------- */
              Long stateId = null;
              if (!stateName.isBlank()) {
                  stateId = fileUploadDao.getStateIdByName(stateName);
                  if (stateId == null) {
                      fieldErrors.put("stateName", "State not found");
                  }
              }

              /* ---------- SKILL VALIDATION ---------- */
              Integer skillId = null;
              if (!skillName.isBlank()) {
                  skillId = fileUploadDao.getGeneralMasterId(skillName);
                  if (skillId == null) {
                      fieldErrors.put("skillName", "Skill Name not found");
                  }
              }

              /* ---------- ZONE VALIDATION ---------- */
              Integer zoneId = null;
              if (!zoneName.isBlank()) {
                  zoneId = fileUploadDao.getGeneralMasterId(zoneName);
                  if (zoneId == null) {
                      fieldErrors.put("zoneName", "Zone Name not found");
                  }
              }

              /* ---------- BASIC VALIDATION ---------- */
              if (!fields[4].isBlank()) {
                  try {
                      basic = new BigDecimal(fields[4]);
                  } catch (Exception e) {
                      fieldErrors.put("basic", "Invalid number");
                  }
              }

              /* ---------- DA VALIDATION ---------- */
              if (!fields[5].isBlank()) {
                  try {
                      da = new BigDecimal(fields[5]);
                  } catch (Exception e) {
                      fieldErrors.put("da", "Invalid number");
                  }
              }

              /* ---------- OTHER ALLOWANCE VALIDATION ---------- */
              if (!fields[6].isBlank()) {
                  try {
                      otherAllowance = new BigDecimal(fields[6]);
                  } catch (Exception e) {
                      fieldErrors.put("otherAllowance", "Invalid number");
                  }
              }
              if (fromDate != null) {

            	    LocalDate today = LocalDate.now();
            	    LocalDate minDate = today.minusDays(60);
            	    LocalDate maxDate = today.plusDays(15);

            	    LocalDate fromLocalDate = fromDate.toInstant()
            	            .atZone(ZoneId.systemDefault())
            	            .toLocalDate();

            	    if (fromLocalDate.isBefore(minDate) || fromLocalDate.isAfter(maxDate)) {
            	        fieldErrors.put("fromDate", 
            	            "From Date must be between " + minDate + " and " + maxDate);
            	    }
            	}
              if (!fieldErrors.isEmpty()) {
                  errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                  continue;
              }
              
              try {
                  
            	  MinimumWageDTO staging = new MinimumWageDTO();
            	  staging.setUnitCode(unitCode);
            	  staging.setStateName(stateName);
            	  staging.setZoneName(zoneName);
            	  staging.setSkillName(skillName);
            	  staging.setBasic(basic);
            	  staging.setDa(da);
            	  staging.setOtherAllowance(otherAllowance);
            	  staging.setFromDate(fromDate);
            	  staging.setToDate(ESIC_MAX_DATE);

            	  if (fileUploadDao.minimumWageFromExistsInStagging(unitCode, stateName,zoneName,skillName,fromDate)) {
                      fileUploadDao.updateMinimumWageToStaging(staging);
                  }
//            	  else if(fileUploadDao.minimumWagesExistsInStagging(unitCode, stateName,zoneName,skillName)) {
//                   fileUploadDao.saveMinimumWageToStaging(staging);
//                  }
            	  else {
                	  fileUploadDao.saveMinimumWageToStaging(staging);  
                  }
                   // Prepare and add to success list
                   Map<String, Object> success = new LinkedHashMap<>();
                   success.put("unitCode",unitCode );
                   success.put("stateName",stateName);
                   success.put("zoneName",zoneName);
                   success.put("skillName", skillName);
                   success.put("basic", basic);
                   success.put("da", da);
                   success.put("otherAllowance",otherAllowance);
                   success.put("fromDate", toSqlDateString(fromDate));
                   //success.put("toDate", toSqlDateString(toDate));

                   successData.add(success);

              } catch (Exception e) {
                  // fallback if DB insert fails despite validations
              	 errorData.add(Map.of("row", rowNum, "error", "Exception while processing row: " + e.getMessage()));
              }
          }
          try {
              fileUploadDao.callMinimumWageProcessingSP();
          } catch (Exception e) {
              errorData.add(Map.of("row", "Procedure", "error", "Stored Procedure Failed: " + e.getMessage()));
          }

          Map<String, Object> result = new HashMap<>();
          result.put("successData", successData);
          result.put("errorData", errorData);
          return result;
    }
    
    @Transactional
    private Map<String, Object> processPrincipalEmployer(BufferedReader reader,String createdBy) throws IOException {
        List<Map<String, Object>> successData = new ArrayList<>();
        List<Map<String, Object>> errorData = new ArrayList<>();

        String line;
        int rowNum = 0;
        List<PrincipalEmployer> peListForOrgEntry = new ArrayList<>();
        String[] fieldNames = {
            "organization", "code", "name", "address", "managerName",
            "managerAddrs", "businessType", "maxWorkmen", "maxCntrWorkmen",
            "bocwApplicability", "isMwApplicability", "licenseNumber", "pfCode",
            "wcNumber", "factoryLicenseNumber", "stateNM"
        };

        while ((line = reader.readLine()) != null) {
            rowNum++;
            line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");

            if (line.trim().isEmpty()) continue;

            String[] rawFields = line.split(",", -1);
            String[] fields = new String[rawFields.length];
            for (int i = 0; i < rawFields.length; i++) {
                fields[i] = rawFields[i].trim().replaceAll("\"", "");
            }

            Map<String, String> fieldErrors = new LinkedHashMap<>();

            if (fields.length < 16) {
                errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                continue;
            }

            for (int i = 0; i < fieldNames.length; i++) {
                if (fields[i].isBlank()) {
                    fieldErrors.put(fieldNames[i], "is Mandatory");
                }
            }

            if (!fieldErrors.isEmpty()) {
                errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                continue;
            }

            try {
                String code = fields[1];
                String stateName = fields[15];
                String organization = fields[0];
                String businessType = fields[6];
                
                // Duplicate plantCode check
               //boolean unitid=  fileUploadDao.isPrincipalEmployerCodeExists(code);
                   // errorData.add(Map.of("row", rowNum, "error", "Duplicate plantCode: " + code + " already exists"));
                  //  continue;
               // }
                Long unitId = fileUploadDao.getPrincipalEmployerExists(code, organization,businessType);
                log.info("fetch unitid"+unitId);
                // Check if state exists in CMSSTATE table
                Long stateId = fileUploadDao.getStateIdByName(stateName);
                if (stateId == null) {
                    errorData.add(Map.of("row", rowNum, "error", "State not found: " + stateName));
                    continue;
                }

                // Save PrincipalEmployer
                PrincipalEmployer p = new PrincipalEmployer();
                p.setOrganization(organization);
                p.setCode(code);
                p.setName(fields[2]);
                p.setAddress(fields[3]);
                p.setManagerName(fields[4]);
                p.setManagerAddrs(fields[5]);
                p.setBusinessType(businessType);
                p.setMaxWorkmen(Integer.parseInt(fields[7]));
                p.setMaxCntrWorkmen(Integer.parseInt(fields[8]));
                p.setBocwApplicability(Integer.parseInt(fields[9]));
                p.setIsMwApplicability(Integer.parseInt(fields[10]));
                p.setLicenseNumber(fields[11]);
                p.setPfCode(fields[12]);
                p.setWcNumber(fields[13]);
                p.setFactoryLicenseNumber(fields[14]);
                p.setStateNM(String.valueOf(stateId));  // Save for display/reference
                if(unitId == null || unitId == 0) {
                 unitId = fileUploadDao.savePrincipalEmployer(p, createdBy);  // Save and get unitId
                 log.info("insert into cmsprincipalemployer"+unitId);
                }else {
                	fileUploadDao.updatePrincipalEmployer(p, createdBy,unitId); 
                	log.info("update into cmsprincipalemployer"+unitId);
                }
                // Save to CMSPrincipalEmployerState with unitId and stateId
                
               boolean PEStateExists = fileUploadDao.getPEStateExists(unitId, stateId);
               if(!PEStateExists) {
                   fileUploadDao.savePEState(unitId, stateId);
               }
               
                peListForOrgEntry.add(p);
                
                // Prepare and add to success list
                Map<String, Object> success = new LinkedHashMap<>();
                success.put("organization", p.getOrganization());
                success.put("code", p.getCode());
                success.put("name", p.getName());
                success.put("address", p.getAddress());
                success.put("managerName", p.getManagerName());
                success.put("managerAddrs", p.getManagerAddrs());
                success.put("businessType", p.getBusinessType());
                success.put("maxWorkmen", p.getMaxWorkmen());
                success.put("maxCntrWorkmen", p.getMaxCntrWorkmen());
                success.put("bocwApplicability", p.getBocwApplicability());
                success.put("isMwApplicability", p.getIsMwApplicability());
                success.put("licenseNumber", p.getLicenseNumber());
                success.put("pfCode", p.getPfCode());
                success.put("wcNumber", p.getWcNumber());
                success.put("factoryLicenseNumber", p.getFactoryLicenseNumber());
                success.put("stateNM", stateName);

                successData.add(success);

            } catch (Exception e) {
                errorData.add(Map.of("row", rowNum, "error", "Exception while processing row: " + e.getMessage()));
                log.error("Error insertion/updation in  cmsprincipalemployer: ", e);
            }
			/*
			 * if (fields == null || fields.length == 0) { return null; } else {
			 * InsertPEOrgLevelEntry(peListForOrgEntry); }
			 */
        }
        this.InsertPEOrgLevelEntry(peListForOrgEntry);

        Map<String, Object> result = new HashMap<>();
        result.put("successData", successData);
        result.put("errorData", errorData);
        return result;
    }

    @Transactional(rollbackFor = Exception.class)
    private Map<String, Object> processContractor(BufferedReader reader) throws IOException {
        List<Map<String, Object>> successData = new ArrayList<>();
        List<Map<String, Object>> errorData = new ArrayList<>();
        String line;
        int rowNum = 0;
        List<Contractor> ContListForOrgEntry = new ArrayList<>();

        String[] fieldNames = {
        		"workOrderNumber","plantCode","organization","contractorCode","contractorId",
            "contractorName","contractorAddress","city","managerNm","totalStrength","maxNoEmp",
            "licenseNumber","licenseValidFrom","licenseValidTo","coverage",
            "wcCode","wcFromDtm","wcToDtm","wcTotal","esiwc","esiValidFrom",
            "natureofWork","pfNum","pfApplyDt"
        };

        Set<String> mandatoryFields = Set.of(
            "contractorName","contractorAddress","city","plantCode","managerNm",
            "totalStrength","maxNoEmp","natureofWork","contractorId",
            "organization","contractorCode","workOrderNumber"
        );

        while ((line = reader.readLine()) != null) {
            rowNum++;
            line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");
            if (line.trim().isEmpty()) continue;

            String[] rawFields = line.split(",", -1);
            String[] fields = new String[rawFields.length];
            for (int i = 0; i < rawFields.length; i++) {
                fields[i] = rawFields[i].trim().replaceAll("\"", "");
            }

            Map<String, String> fieldErrors = new LinkedHashMap<>();
            if (fields.length < fieldNames.length) {
                errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                continue;
            }

            // Check mandatory fields
            for (int i = 0; i < fieldNames.length; i++) {
                if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
                    fieldErrors.put(fieldNames[i], "is mandatory");
                }
            }
            Date today = new Date();
            String plantCode = fields[1];
            String organization = fields[2];
            String contractorCode = fields[3];
            String subContractorCode = fields[4];
            String contractorName = fields[5];
            String workOrder = fields[0];
            Date llFromDate = parseDateStrict(fields[12], "licenseValidFrom", fieldErrors);
            Date llToDate   = parseDateStrict(fields[13], "licenseValidTo", fieldErrors);
            Date pfApplyDate = parseDateStrict(fields[23], "pfApplyDt", fieldErrors);
            Date esicFromDate = parseDateStrict(fields[20], "esiValidFrom", fieldErrors);
            Date wcFromDate = parseDateStrict(fields[16], "wcFromDtm", fieldErrors);
            Date wcToDate = parseDateStrict(fields[17], "wcToDtm", fieldErrors);
            String esic = fields[19];
            String wcCode = fields[15];
            Integer wcTotal = parseIntegerSafe(fields[18]);
            String llNumber = fields[11];
            Integer coverageVal = parseIntegerSafe(fields[14]);
            Integer totalStrength = parseIntegerSafe(fields[9]);
            Integer maxNoEmp = parseIntegerSafe(fields[10]);
            
            //1.plant exists are not
            Long unitId = fileUploadDao.getUnitIdByPlantCodeAndOrg(plantCode, organization);
            if (unitId == null) {
            	fieldErrors.put("plantCode" ,"Not found for plantCode and organization ");
            	fieldErrors.put("organization" ,"Not found for plantCode and organization ");
            }
            
            Long MainContractorexists = fileUploadDao.getContractorIdByCode(contractorCode);
            
            //2 Work order exists check
            boolean activeWorkorderExists =fileUploadDao.hasActiveWorkorder(unitId, MainContractorexists,workOrder);
            if (!activeWorkorderExists) {
            	fieldErrors.put("workOrder" ,"No active workorder exists for this contractor");
          }

              // 3 Either ESIC or ECWC mandatory
            if ((esic == null || esic.isBlank()) && (wcCode == null || wcCode.isBlank())) {
                fieldErrors.put("esiwc", "Either ESIC or EC/WC is mandatory");
                fieldErrors.put("wcCode", "Either ESIC or EC/WC is mandatory");
            }
         // 4 ESIC VALIDATIONS
            if (esic != null && !esic.isBlank()) {

                // ESIC From Date → mandatory + past date
                if (fields[20] == null || fields[20].isBlank()) {
                    fieldErrors.put("esiValidFrom", "ESIC From Date is mandatory");
                } else {
                    if (esicFromDate == null) {
                        fieldErrors.put("esiValidFrom", "Invalid date format. Expected yyyy-MM-dd");
                    } else if (!isPastDate(esicFromDate)) {
                        fieldErrors.put("esiValidFrom", "ESIC From Date must be a past date");
                    }
                }
            }
             //  5 ECWC validations
            if (wcCode != null && !wcCode.isBlank()) {

                if (fields[16] == null || fields[16].isBlank()) {
                    fieldErrors.put("wcFromDtm", "WC From Date is mandatory when WC Code is provided");
                }
               else if (!isPastDate(wcFromDate)) {
                   fieldErrors.put("wcFromDtm", "WC From Date must be a past date");
               }

                if (fields[17] == null || fields[17].isBlank()) {
                    fieldErrors.put("wcToDtm", "WC To Date is mandatory when WC Code is provided");
                } 
                else if (!isFutureDate(wcToDate)) {
                    fieldErrors.put("wcToDtm", "WC To Date must be greater than today");
                }

                //  wcTotal > 0 
                if (wcTotal == null) {
        	        fieldErrors.put("wcTotal"," Coverage is mandatory when WC Code is provided");
        	    } else if  (wcTotal <= 0) {
                    fieldErrors.put("wcTotal", "Coverage must be greater than 0 when WC Code is present");
                }
            }
             //  6 License validations
            if (llNumber != null && !llNumber.isBlank()) {

            	if (fields[12] == null || fields[12].isBlank()) {
                    fieldErrors.put("licenseValidFrom", "License Valid From date is mandatory when License Number is provided");
                }else if (!isPastDate(llFromDate)) {
                    fieldErrors.put("licenseValidFrom","License Valid From date must be a past date");
                    }

            	if (fields[13] == null || fields[13].isBlank()) {
                    fieldErrors.put("licenseValidTo", "License Valid To date is mandatory when License Number is provided");
                }else if (!isFutureDate(llToDate)) {
                    fieldErrors.put("licenseValidTo","License Valid To date must be greater than today");
                    }

              //    Coverage > 0 
            	  if (coverageVal == null) {
            	        fieldErrors.put("coverage","License Coverage is mandatory when License Number is provided");
            	    } else if (coverageVal <= 0) {
            	        fieldErrors.put("coverage","License Coverage must be greater than 0 when License Number is provided");
            	    }
                }
           // 7️⃣ Total Strength validation
            if (fields[9] == null || fields[9].isBlank()) {
                fieldErrors.put("totalStrength", "Total Strength is mandatory");
            } else if (totalStrength == null) {
                fieldErrors.put("totalStrength", "Total Strength must be a valid number");
            } else if (totalStrength <= 0) {
                fieldErrors.put("totalStrength", "Total Strength must be greater than 0");
            }

            // 8️⃣ Max Number of Workmen validation
            if (fields[10] == null || fields[10].isBlank()) {
                fieldErrors.put("maxNoEmp", "Max Number of Workmen is mandatory");
            } else if (maxNoEmp == null) {
                fieldErrors.put("maxNoEmp", "Max Number of Workmen must be a valid number");
            } else if (maxNoEmp <= 0) {
                fieldErrors.put("maxNoEmp", "Max Number of Workmen must be greater than 0");
            }

          //   STOP PROCESSING IF ANY ERROR FOUND
            if (!fieldErrors.isEmpty()) {
                errorData.add(Map.of("row", rowNum,"fieldErrors", fieldErrors));
                continue;
            }
            try { 
            	Long contractorId=null;
            	
            	Long SubContractorexists = fileUploadDao.getContractorIdByCode(subContractorCode);
                // Step 2: Save Contractor
                Contractor contractor = new Contractor();
                contractor.setContractorName(contractorName);
                contractor.setContractorAddress(fields[6]);
              //  contractor.setContractorCode(contractorCode);
                contractor.setCity(fields[7]);
                //Long contractorId = fileUploadDao.saveContractor(contractor);

                if(contractorCode != null && contractorCode.equals(subContractorCode)) {
                	contractor.setContractorCode(contractorCode);
                	 if (MainContractorexists == null) {
                		 fileUploadDao.saveContractor(contractor);
                     }else {
                    	 fileUploadDao.updateContractor(contractor);
                    	 contractorId = MainContractorexists;
                     }
            	}else {
            		if (MainContractorexists == null) {
                     	fieldErrors.put("MainContractorCode" ,"Main Contractor not Found");
                     }else{
                    	 if (SubContractorexists == null) {

                    		 Contractor subContractor = new Contractor();
                   	        subContractor.setContractorName(contractorName);
                   	        subContractor.setContractorAddress(fields[6]);
                   	        subContractor.setCity(fields[7]);
                   	        subContractor.setContractorCode(subContractorCode);

                   	        contractorId =   fileUploadDao.saveContractor(subContractor);
                          }else {
                        	  
                        	  Contractor subContractor = new Contractor();
                              subContractor.setContractorId(String.valueOf(SubContractorexists));
                              subContractor.setContractorName(contractorName);
                              subContractor.setContractorAddress(fields[6]);
                              subContractor.setCity(fields[7]);
                              subContractor.setContractorCode(subContractorCode);
                              
                         	 fileUploadDao.updateContractor(contractor);
                         	contractorId = SubContractorexists;
                          }
                     } 
                    	
            	}
             //  LICENSE UNIQUENESS VALIDATION — CMSCONTRACTOR_WC ONLY

             // LL
             if (llNumber != null && !llNumber.isBlank()) {
                 if (fileUploadDao.isLicenseMappedToOtherContractor(contractorId, llNumber, "LL")) {
                     
                	 throw new IllegalArgumentException("LL License number '" + llNumber + "' is already mapped to another contractor");
                 }
             }

             // ESIC
             if (esic != null && !esic.isBlank()) {
                 if (fileUploadDao.isLicenseMappedToOtherContractor(contractorId, esic, "ESIC")) {

                     throw new IllegalArgumentException("ESIC number '" + esic + "' is already mapped to another contractor");
                 }
             }

             // WC
             if (wcCode != null && !wcCode.isBlank()) {
                 if (fileUploadDao.isLicenseMappedToOtherContractor(contractorId, wcCode, "WC")) {

                     throw new IllegalArgumentException("WC code '" + wcCode + "' is already mapped to another contractor");
                 }
             }

                // Step 3: Save PEMM
                CMSContrPemm pemm = new CMSContrPemm();
                pemm.setContractorId(contractorId);
                pemm.setUnitId(unitId);
                pemm.setManagerNm(fields[8]);
                pemm.setLicenseNumber(llNumber);
                pemm.setLicenseValidFrom(llFromDate);
                pemm.setLicenseValidTo(llToDate);
                pemm.setCoverage(coverageVal == null ? "" : String.valueOf(coverageVal));
                pemm.setTotalStrength(totalStrength);
                pemm.setMaxNoEmp(maxNoEmp);
                pemm.setNatureofWork(fields[21]);
                pemm.setPfNum(fields[22]);
                pemm.setPfApplyDt(pfApplyDate);
                pemm.setEsiwc(esic);
                pemm.setEsiValidFrom(esicFromDate);
                if (esic != null && !esic.isBlank()) {
                    pemm.setEsiValidTo(ESIC_MAX_DATE);   // ESIC present → set ESIC To Date as 01-01-3000
                } else {
                    pemm.setEsiValidTo(null);
                }
                    if (fileUploadDao.pemmExists(contractorId, unitId)) {
                        fileUploadDao.updatePemm(pemm);
                    } else {
                        fileUploadDao.savePemm(pemm);
                    }

                // Step 4: Save SubContractor
                CMSSubContractor csc = new CMSSubContractor();
                csc.setUnitId(String.valueOf(unitId));
                csc.setSubContractId(subContractorCode);
                csc.setContractorId(contractorCode);
                csc.setWorkOrderNumber(workOrder);
               boolean cmsSubContExists= fileUploadDao.subContractorExists(contractorCode, unitId, workOrder,subContractorCode);
                if (!cmsSubContExists) {
                    fileUploadDao.savecsc(csc);
                }
                
                // Step 5: Save WC
                CmsContractorWC wc =null;
                
                if (llNumber != null && !llNumber.isBlank()) {
                 wc = new CmsContractorWC();
                wc.setContractorId(String.valueOf(contractorId));
                wc.setUnitId(String.valueOf(unitId));
                wc.setWcCode(llNumber);
                wc.setWcFromDtm(toSqlDateString(llFromDate));
                wc.setWcToDtm(toSqlDateString(llToDate));
                wc.setWcTotal(wcTotal != null ? wcTotal : 0);
                wc.setLicenceType("LL");
                if (fileUploadDao.wcExists(contractorId, unitId,llNumber,"LL")) {
                    fileUploadDao.updatewc(wc);
                } else {
                    fileUploadDao.savewc(wc);
                }
                }
                
                if (esic != null && !esic.isBlank()) {
                     wc = new CmsContractorWC();
                    wc.setContractorId(String.valueOf(contractorId));
                    wc.setUnitId(String.valueOf(unitId));
                    wc.setWcCode(esic);
                    wc.setWcFromDtm(toSqlDateString(esicFromDate));
                    wc.setWcToDtm("3000-01-01");
                    wc.setWcTotal(wcTotal != null ? wcTotal : 0);
                    wc.setLicenceType("ESIC");
                    if (fileUploadDao.wcExists(contractorId, unitId,esic,"ESIC")) {
                        fileUploadDao.updatewc(wc);
                    } else {
                        fileUploadDao.savewc(wc);
                    }
                    }
                
                if (wcCode != null && !wcCode.isBlank()) {
                     wc = new CmsContractorWC();
                    wc.setContractorId(String.valueOf(contractorId));
                    wc.setUnitId(String.valueOf(unitId));
                    wc.setWcCode(wcCode);
                    wc.setWcFromDtm(toSqlDateString(wcFromDate));
                    wc.setWcToDtm(toSqlDateString(wcToDate));
                    wc.setWcTotal(wcTotal != null ? wcTotal : 0);
                    wc.setLicenceType("WC");
                    if (fileUploadDao.wcExists(contractorId, unitId,wcCode,"WC")) {
                        fileUploadDao.updatewc(wc);
                    } else {
                        fileUploadDao.savewc(wc);
                    }
                    }
             // Step 6: Save CMSWORKORDER_LL_WC

             // LL
             if (llNumber != null && !llNumber.isBlank()) {
                 CMSWorkorderLLWC llwc = new CMSWorkorderLLWC();
                 llwc.setWorkorderNumber(workOrder);
                 llwc.setLicenseNumber(llNumber);
                 llwc.setLicenseType("LL");

                 if (fileUploadDao.llwcExists(workOrder,"LL",llNumber)) {
                     fileUploadDao.updateWorkorderLLWC(llwc);
                 } else {
                     fileUploadDao.saveWorkorderLLWC(llwc);
                 }
             }

             // ESIC
             if (esic != null && !esic.isBlank()) {
                 CMSWorkorderLLWC llwc = new CMSWorkorderLLWC();
                 llwc.setWorkorderNumber(workOrder);
                 llwc.setLicenseNumber(esic);
                 llwc.setLicenseType("ESIC");

                 if (fileUploadDao.llwcExists(workOrder,"ESIC",esic)) {
                     fileUploadDao.updateWorkorderLLWC(llwc);
                 } else {
                     fileUploadDao.saveWorkorderLLWC(llwc);
                 }
             }

             // WC
             if (wcCode != null && !wcCode.isBlank()) {
                 CMSWorkorderLLWC llwc = new CMSWorkorderLLWC();
                 llwc.setWorkorderNumber(workOrder);
                 llwc.setLicenseNumber(wcCode);
                 llwc.setLicenseType("WC");

                 if (fileUploadDao.llwcExists(workOrder,"WC",wcCode)) {
                     fileUploadDao.updateWorkorderLLWC(llwc);
                 } else {
                     fileUploadDao.saveWorkorderLLWC(llwc);
                 }
             }
             
             //save in cmsvendor
             Long SubContractorexistsinCMSVendor =	fileUploadDao.getContractorIdByCodeInCMSVendor(subContractorCode);
             
             if(SubContractorexistsinCMSVendor == null) {
             	CMSVendor cmsvendor= new CMSVendor();
             	cmsvendor.setVendorCode(subContractorCode);
             	cmsvendor.setVendorName(contractorName);
             	cmsvendor.setVendorId(String.valueOf(contractorId));
             	  fileUploadDao.insertContractorInCMSVendor(cmsvendor);
             }else {
             	CMSVendor cmsvendor= new CMSVendor();
             	cmsvendor.setVendorCode(subContractorCode);
             	cmsvendor.setVendorName(contractorName);
             	cmsvendor.setVendorId(String.valueOf(SubContractorexistsinCMSVendor));
                 
             	fileUploadDao.updateContractorInCMSVendor(cmsvendor);
             }

                ContListForOrgEntry.add(contractor);

                Map<String, Object> map = new HashMap<>();
                map.put("workOrderNumber", csc.getWorkOrderNumber());
                map.put("plantCode", plantCode);
                map.put("organization", organization);
                map.put("contractorCode", contractorCode);
                map.put("contractorId", subContractorCode);
                map.put("contractorName", contractor.getContractorName());
                map.put("contractorAddress", contractor.getContractorAddress());
                map.put("city", contractor.getCity());
                map.put("managerNm", pemm.getManagerNm());
                map.put("totalStrength", pemm.getTotalStrength());
                map.put("maxNoEmp", pemm.getMaxNoEmp());
                map.put("licenseNumber", pemm.getLicenseNumber());
                map.put("licenseValidFrom",toSqlDateString( pemm.getLicenseValidFrom()));
                map.put("licenseValidTo", toSqlDateString(pemm.getLicenseValidTo()));
                map.put("coverage", pemm.getCoverage());
                map.put("wcCode", wc.getWcCode());
                map.put("wcFromDtm", wc.getWcFromDtm());
                map.put("wcToDtm", wc.getWcToDtm());
                map.put("wcTotal", wc.getWcTotal());
                map.put("esiwc", pemm.getEsiwc());
                map.put("esiValidFrom", toSqlDateString(pemm.getEsiValidFrom()));
                map.put("natureofWork", pemm.getNatureofWork());
                map.put("pfNum", pemm.getPfNum());
                map.put("pfApplyDt", toSqlDateString(pemm.getPfApplyDt()));

                successData.add(map);
            }  catch (Exception e) {
                errorData.add(Map.of("row", rowNum, "error", "" + e.getMessage()));
            }
        }
        this.InsertContractorOrgLevelEntry(ContListForOrgEntry);

        Map<String, Object> result = new HashMap<>();
        result.put("successData", successData);
        result.put("errorData", errorData);
        return result;
    }

    // Helper method
    private Date parseDateStrict(String value, String fieldName, Map<String, String> fieldErrors) {

        if (value == null || value.isBlank()) {
            return null;
        }

        String input = value.trim();

        String[] patterns = {
                "dd/MM/yyyy",
                "dd-MM-yyyy",
                "yyyy/MM/dd",
                "yyyy-MM-dd"
        };

        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false); // STRICT parsing
                return sdf.parse(input);
            } catch (Exception ignored) {
                // try next format
            }
        }

        fieldErrors.put(
                fieldName,
                "Invalid date format. Expected dd/MM/yyyy, dd-MM-yyyy, yyyy/MM/dd, or yyyy-MM-dd"
        );
        return null;
    }


//    private boolean isValidDate(String value) {
//        if (value == null || value.isBlank()) return false;
//        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            sdf.setLenient(false);
//            sdf.parse(value.trim());
//            return true;
//        } catch (Exception e) {
//            return false;
//        }
//    }
    private boolean isPastDate(Date inputDate) {
        if (inputDate == null) return false;

        LocalDate input = inputDate.toInstant()
                                   .atZone(ZoneId.systemDefault())
                                   .toLocalDate();

        LocalDate today = LocalDate.now();

        return input.isBefore(today);
    }

    private boolean isFutureDate(Date date) {
        return date.after(new Date());
    }
    private Date parseDateQuiet(String dateStr) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }
    private String toSqlDateString(Date date) {
        if (date == null) {
            return "";
        }
        return new java.text.SimpleDateFormat("yyyy-MM-dd").format(date);
    }

    private Integer parseIntegerSafe(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null; // or 0 if business allows
        }
        return Integer.parseInt(value.trim());
    }

//    private boolean isValidFutureMaxDate(Date date) {
//        Calendar cal = Calendar.getInstance();
//        cal.set(3000, Calendar.JANUARY, 1, 0, 0, 0);
//        return date.equals(cal.getTime());
//    }
    private static final Date ESIC_MAX_DATE;
    static {
        try {
            ESIC_MAX_DATE = new SimpleDateFormat("yyyy-MM-dd").parse("3000-01-01");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Transactional
    private Map<String, Object> processWorkorder(BufferedReader reader) throws IOException {
        List<Map<String, Object>> successData = new ArrayList<>();
        List<Map<String, Object>> errorData = new ArrayList<>();

        String line;
        int rowNum = 0;
        List<KTCWorkorderStaging> KTCWorkorderStaging = new ArrayList<>();
        String[] fieldNames = {
            "workOrderNumber", "item", "line", "lineNumber", "serviceCode", "shortText", "deliveryCompletion",
            "itemChangedON", "vendorCode", "vendorName", "vendorAddress", "blockedVendor",
            "workOrderValiditiyFrom", "workOrderValiditiyTo", "workOrderType", "plantcode", "sectionCode",
            "departmentCode", "GLCode", "costCenter", "natureofJob", "rateUnit", "quantity", "baseUnitofMeasure",
            "workOrderReleased", "PMOrderNo", "WBSElement", "qtyCompleted", "workOrderReleaseDate",
            "serviceEntryCreatedDate", "serviceEntryUpdatedDate", "purchaseOrgLevel", "companycode"
        };

        Set<String> mandatoryFields = Set.of(
            "workOrderNumber", "vendorCode", "vendorName",
            "workOrderValiditiyFrom", "workOrderValiditiyTo",
            "plantcode"
        );

        //Set<String> numericFields = Set.of("rateUnit", "quantity", "qtyCompleted");

        while ((line = reader.readLine()) != null) {
            rowNum++;
            line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");
            if (line.trim().isEmpty()) continue;

            String[] rawFields = line.split(",", -1);
            String[] fields = new String[rawFields.length];
            for (int i = 0; i < rawFields.length; i++) {
                fields[i] = rawFields[i].trim().replaceAll("\"", "");
            }

            Map<String, String> fieldErrors = new LinkedHashMap<>();

            if (fields.length < fieldNames.length) {
                errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                continue;
            }

            // Check mandatory fields
            for (int i = 0; i < fieldNames.length; i++) {
                if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
                    fieldErrors.put(fieldNames[i], "is mandatory");
                }
            }

            if (!fieldErrors.isEmpty()) {
                errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                continue;
            }
            // Check numeric fields
           // for (int i = 0; i < fieldNames.length; i++) {
             //   if (numericFields.contains(fieldNames[i]) && !fields[i].isBlank()) {
                //    try {
               //         Double.parseDouble(fields[i]);
               //     } catch (NumberFormatException ex) {
                       // fieldErrors.put(fieldNames[i], "Invalid numeric value");
                //    }
                //}
           // }
            String contractorCode = fields[8];
            String workOrder = fields[0];
            
            Integer workorderExistsForOtherContractor =fileUploadDao.IsWorkorderExistsForOtherContractor(workOrder,contractorCode);
            if (workorderExistsForOtherContractor != null) {
            	 errorData.add(Map.of("row", rowNum, "error", "Workorder: " + workOrder + " already Mapped to Other Contractor"));
                 continue;
            }
            
            if (!fieldErrors.isEmpty()) {
                errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                continue;
            }
            
            try {
            	String plantCode = fields[15];
                String item = fields[1];
                String lines = fields[2];
                String lineNumber = fields[3];
                
                KTCWorkorderStaging staging = new KTCWorkorderStaging();
                staging.setWorkOrderNumber(workOrder);
                staging.setItem(item);
                staging.setLine(lines);
                staging.setLineNumber(lineNumber);
                staging.setServiceCode(fields[4]);
                staging.setShortText(fields[5]);
                staging.setDeliveryCompletion(fields[6]);
                staging.setItemChangedON(fields[7]);
                staging.setVendorCode(contractorCode);
                staging.setVendorName(fields[9]);
                staging.setVendorAddress(fields[10]);
                staging.setBlockedVendor(fields[11]);
                staging.setWorkOrderValiditiyFrom(fields[12]);
                staging.setWorkOrderValiditiyTo(fields[13]);
                staging.setWorkOrderType(fields[14]);
                staging.setPlantcode(plantCode);
                staging.setSectionCode(fields[16]);
                staging.setDepartmentCode(fields[17]);
                staging.setGLCode(fields[18]);
                staging.setCostCenter(fields[19]);
                staging.setNatureofJob(fields[20]);
                staging.setRateUnit((fields[21] == null || fields[21].trim().isEmpty()) ? "0" : fields[21].trim());
                staging.setQuantity((fields[22] == null || fields[22].trim().isEmpty()) ? "0" : fields[22].trim());
                staging.setBaseUnitofMeasure(fields[23] != null ? fields[23] :" ");
                staging.setWorkOrderReleased(fields[24]);
                staging.setPMOrderNo(fields[25]);
                staging.setWBSElement(fields[26]);
                staging.setQtyCompleted(fields[27]);
                staging.setWorkOrderReleaseDate(fields[28]);
                staging.setServiceEntryCreatedDate(fields[29]);
                staging.setServiceEntryUpdatedDate(fields[30]);
                staging.setPurchaseOrgLevel(fields[31]);
                staging.setCompanycode(fields[32]);

                //fileUploadDao.saveWorkorderToStaging(staging);
                if (fileUploadDao.workorderExists(workOrder, contractorCode,plantCode,item,lines,lineNumber)) {
                    fileUploadDao.updateWorkorderToStaging(staging);
                } else {
                    fileUploadDao.saveWorkorderToStaging(staging);
                }
                
                KTCWorkorderStaging.add(staging);
                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (int i = 0; i < fieldNames.length; i++) {
                    rowMap.put(fieldNames[i], fields[i]);
                }
                successData.add(rowMap);

            } catch (Exception e) {
                // fallback if DB insert fails despite validations
               /// Map<String, String> dbFieldErrors = new LinkedHashMap<>();
                //for (String field : numericFields) {
                 //   dbFieldErrors.put(field, "Invalid numeric value");
                //}
                ///errorData.add(Map.of(
                 //   "row", rowNum,
                 //   "error", "-",
                 //   "fieldErrors", dbFieldErrors
               // ));
            	 errorData.add(Map.of("row", rowNum, "error", "Exception while processing row: " + e.getMessage()));
            }
			/*
			 * if (fields == null || fields.length == 0) { return null; } else {
			 * InsertWorkorderOrgLevelEntry(KTCWorkorderStaging); }
			 */
        }
    	InsertWorkorderOrgLevelEntry(KTCWorkorderStaging);
        try {
            fileUploadDao.callWorkorderProcessingSP();
        } catch (Exception e) {
            errorData.add(Map.of("row", "Procedure", "error", "Stored Procedure Failed: " + e.getMessage()));
        }

       // fileUploadDao.callWorkorderProcessingSP();
        Map<String, Object> result = new HashMap<>();
        result.put("successData", successData);
        result.put("errorData", errorData);
        return result;
    }
    
    private static final Pattern PINCODE_PATTERN = Pattern.compile("\\b[1-9][0-9]{5}\\b");
    
    private Map<String, Object> processworkmenbulkupload(BufferedReader reader) throws IOException {
        List<Map<String, Object>> successData = new ArrayList<>();
        List<Map<String, Object>> errorData = new ArrayList<>();

        String line;
        int rowNum = 0;

        String[] fieldNames = {
            "firstName", "lastName", "relationName", "dateOfBirth", "trade", "skill", "natureOfWork",
            "hazardousArea", "aadhaarNumber", "vendorCode", "gender", "doj", "department", "area",
            "workorderNumber", "pfNumber", "maritalStatus", "technical", "academic",
            "bloodGroup", "accommodation", "bankName", "accountNumber", "mobileNumber", "emergencyNumber", "policeVerificationDate",
            "healthCheckDate", "accessArea", "esicNumber", "unitCode", "organizationName",
            "EICNumber", "ECnumber", "uanNumber", "emergencyName", "pfApplicable", "specializationName", "insuranceType", "LLnumber","address","zone","idMark","employeeCode"
        };

        Set<String> mandatoryFields = Set.of(
            "firstName", "lastName", "relationName", "dateOfBirth", "trade", "skill", "natureOfWork",
            "hazardousArea", "aadhaarNumber", "vendorCode", "gender", "department", "workorderNumber",
            "maritalStatus", "technical", "accommodation", "accountNumber", "emergencyNumber",
            "accessArea", "unitCode", "EICNumber", "ECnumber", "emergencyName","address"
        );
        //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while ((line = reader.readLine()) != null) {
            rowNum++;
            line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");
            if (line.trim().isEmpty()) continue;

            String[] rawFields = line.split(",", -1);
            String[] fields = new String[rawFields.length];
            for (int i = 0; i < rawFields.length; i++) {
                fields[i] = rawFields[i].trim().replaceAll("\"", "");
            }

            Map<String, String> fieldErrors = new LinkedHashMap<>();

            if (fields.length < fieldNames.length) {
                errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                continue;
            }

            for (int i = 0; i < fieldNames.length; i++) {
                if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
                    fieldErrors.put(fieldNames[i], "is mandatory");
                }
            }
           
            // Validate Date Format
//               String[] dateFields = { fields[3], fields[11], fields[25], fields[26] }; // DOB, DOJ, PoliceVerificationDate, HealthCheckDate
//               String[] dateFieldNames = { "dateOfBirth", "doj", "policeVerificationDate", "healthCheckDate" };
//
//               for (int i = 0; i < dateFields.length; i++) {
//                   if (!dateFields[i].isBlank()) {
//                       try {
//                           LocalDate.parse(dateFields[i], dateFormatter);
//                       } catch (DateTimeParseException e) {
//                           fieldErrors.put(dateFieldNames[i], "Invalid date format, expected yyyy-MM-dd");
//                       }
//                   }
//               }

            // Lookups
            Integer unitId = fileUploadDao.getUnitIdByName(fields[29]);
            Integer contractorId = fileUploadDao.getContractorIdByName( fields[9]);
            Integer tradeId = fileUploadDao.getTradeIdByUnitId(unitId,fields[4]);
            Integer skillId = fileUploadDao.getSkillIdByTradeId(unitId,tradeId,fields[5]);
            Integer departmentId = fileUploadDao.getdepartmentIdByUnitId(unitId,fields[12]);
            Integer accessAreaId = fileUploadDao.getGeneralMasterId(fields[27]);
            Integer bloodGroupId = fileUploadDao.getGeneralMasterId(fields[19]);
            Integer areaId = fileUploadDao.getAreaByDeptID(unitId,departmentId,fields[13]);
            Integer academicId = fileUploadDao.getGeneralMasterId(fields[18]);
            Integer wcecId = fileUploadDao.getWCECId(fields[32],unitId,contractorId,fields[14]);
            //Integer workorderId = fileUploadDao.getWorkorderId(fields[14],unitId,contractorId);
            Integer workorderId = fileUploadDao.getWorkorderIdBasedonPE(fields[14],unitId);
            //Integer maritalStatusId = fileUploadDao.getGeneralMasterId(fields[16]);
            Integer genderId = fileUploadDao.getGeneralMasterId(fields[10]);
            Integer eicId = fileUploadDao.geteicId(fields[12],unitId,fields[31]);
            //Integer LlNumber = fileUploadDao.getLlNumber(fields[38],unitId,contractorId);
            Integer zoneId = null;

         // Normalize the value safely
         String zoneValue = (fields.length > 40 && fields[40] != null)
                 ? fields[40].replaceAll("[\"\\u00A0]", "").trim()
                 : "";

         // Only validate if something is truly entered
         if (!zoneValue.isBlank()) {
             zoneId = fileUploadDao.getZoneIdFromMinimumWage(zoneValue,unitId);

             if (zoneId == null) {
                 fieldErrors.put("zone", "Invalid or not found");
             }
         }

         Integer llNumber = null;

         String llValue = (fields.length > 38 && fields[38] != null)
        	        ? fields[38].replaceAll("[\"\\u00A0]", "").trim()
        	        : "";

         if (!llValue.isBlank()) {
             llNumber = fileUploadDao.getLlNumber(llValue, unitId, contractorId,fields[14]);

             if (llNumber == null) {
                 fieldErrors.put("llNumber", "Invalid or not found");
             }
         }

         //String addressValue = fields[39] != null ? fields[39].replaceAll("[\\\"\\u00A0]", "").trim() : ""; 
         String addressValue = fields[39] != null ? fields[39].trim(): "";
         if (!addressValue.isBlank()) { 
        	 Matcher pinMatcher = PINCODE_PATTERN.matcher(addressValue);
             if (!pinMatcher.find()) {
            	 fieldErrors.put( "address", "Address must contain a valid 6-digit pincode" );
            }
         }

            if (tradeId == null) fieldErrors.put("trade", "There is no mapping found for Trade and Principal Employee");
            if (skillId == null) fieldErrors.put("skill", "There is no mapping found for Skill and Principal Employee");
            if (departmentId == null) fieldErrors.put("department", "There is no mapping found for Department and Principal Employee");
            if (accessAreaId == null) fieldErrors.put("accessArea", "Invalid or not found");
            if (bloodGroupId == null) fieldErrors.put("bloodGroup", "Invalid or not found");
            if (areaId == null) fieldErrors.put("area", "There is no mapping found for Area and Principal Employee");
            if (academicId == null) fieldErrors.put("academic", "Invalid or not found");
            //if (maritalStatusId == null) fieldErrors.put("maritalStatus", "Invalid or not found");
            if (genderId == null) fieldErrors.put("Gender", "Invalid or not found");
            if (eicId == null) fieldErrors.put("EIC", "Invalid or not found");
            if (workorderId == null) fieldErrors.put("workorderId", "No Workorrder Mapped with Principal Employeer");
            if (wcecId == null) fieldErrors.put("WCESIC", "Invalid or not found");
            if (unitId == null) fieldErrors.put("unitCode", "Invalid or not found");
            if (contractorId == null) fieldErrors.put("vendorCode", "Invalid or not found");
         // Duplicate aadhar check
            String aadharNumber = fields[8];
            boolean aadharNumberExists = workmenUploadDao.isAadharExists(aadharNumber);
            if (aadharNumberExists) {
            	fieldErrors.put("aadharNumber", "Duplicate aadharNumber: " + aadharNumber + " already exists");
            }
            
            if (!fieldErrors.isEmpty()) {
                errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                continue;
            }

            try {
                
                WorkmenBulkUpload staging = new WorkmenBulkUpload();
                staging.setFirstName(fields[0]);
                staging.setLastName(fields[1]);
                staging.setRelationName(fields[2]);
                staging.setDateOfBirth(fields[3]);
                staging.setTrade(String.valueOf(tradeId));               
                staging.setSkill(String.valueOf(skillId));
                staging.setNatureOfWork(fields[6]);
                staging.setHazardousArea(fields[7]);
                staging.setAadhaarNumber(aadharNumber);
                staging.setVendorCode(String.valueOf(contractorId));
                staging.setGender(String.valueOf(genderId));
                staging.setDoj(fields[11]);
                staging.setDepartment(String.valueOf(departmentId));
                staging.setArea(String.valueOf(areaId));
                staging.setWorkorderNumber(String.valueOf(workorderId));
                staging.setPfNumber(fields[15]);
                staging.setMaritalStatus(fields[16]);
                staging.setTechnical(fields[17]);
                staging.setAcademic(String.valueOf(academicId));
                staging.setBloodGroup(String.valueOf(bloodGroupId));
                staging.setAccommodation(fields[20]);
                staging.setBankName(fields[21]);
                staging.setAccountNumber(fields[22]);
                staging.setMobileNumber(fields[23]);
                staging.setEmergencyNumber(fields[24]);
                staging.setPoliceVerificationDate(fields[25]);
                staging.setHealthCheckDate(fields[26]);
                staging.setAccessArea(String.valueOf(accessAreaId));
                staging.setEsicNumber(fields[28]);
                staging.setUnitCode(String.valueOf(unitId));
                staging.setOrganizationName(fields[30]);
                staging.setEICNumber(String.valueOf(eicId));
                staging.setECnumber(String.valueOf(wcecId));
                staging.setUanNumber(fields[33]);
                staging.setEmergencyName(fields[34]);
                staging.setPfApplicable(fields[35]);
                staging.setSpecializationName(fields[36]);
                staging.setInsuranceType(fields[37]);
                staging.setLLnumber(String.valueOf(llNumber!=null?String.valueOf(llNumber):" "));
                staging.setAddress(fields[39]);
                staging.setZone(zoneId!=null?String.valueOf(zoneId):" ");
                staging.setIdMark(fields[41]==null?"NA":fields[41]);
                staging.setEmployeeCode(fields[42]);   
                
                fileUploadDao.saveWorkmenBulkUploadToStaging(staging);

                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (int i = 0; i < fieldNames.length; i++) {
                    rowMap.put(fieldNames[i], fields[i]);
                }
                successData.add(rowMap);

            } catch (Exception e) {
                errorData.add(Map.of("row", rowNum, "error", "Exception while processing row: " + e.getMessage()));
            }
        }

       
        Map<String, Object> result = new HashMap<>();
        result.put("successData", successData);
        result.put("errorData", errorData);
        return result;
    }

    private Map<String, Object> processworkmenbulkuploaddraft(BufferedReader reader) throws IOException {
        List<Map<String, Object>> successData = new ArrayList<>();
        List<Map<String, Object>> errorData = new ArrayList<>();
        List<Integer> savedTransactionIds = new ArrayList<>();
        String line;
        int rowNum = 0;

        String[] fieldNames = {
            "firstName", "lastName", "relationName", "dateOfBirth", "trade", "skill", "natureOfWork",
            "hazardousArea", "aadhaarNumber", "vendorCode", "gender", "doj", "department", "area",
            "workorderNumber", "pfNumber", "maritalStatus", "technical", "academic",
            "bloodGroup", "accommodation", "bankName", "accountNumber", "mobileNumber", "emergencyNumber", "policeVerificationDate",
            "healthCheckDate", "accessArea", "esicNumber", "unitCode", "organizationName",
            "EICNumber", "ECnumber", "uanNumber", "emergencyName", "pfApplicable", "specializationName", "insuranceType", "LLnumber","address","zone","idMark"
        };

       // Set<String> mandatoryFields = Set.of(
       //     "firstName", "lastName", "relationName", "dateOfBirth", "trade", "skill", "natureOfWork",
       //     "hazardousArea", "aadhaarNumber", "vendorCode", "gender", "department", "workorderNumber",
       //     "maritalStatus", "technical", "accommodation", "accountNumber", "emergencyNumber",
       //     "accessArea", "unitCode", "EICNumber", "ECnumber", "emergencyName","idMark"
      //  );
        //DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        while ((line = reader.readLine()) != null) {
            rowNum++;
            line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");
            if (line.trim().isEmpty()) continue;

            String[] rawFields = line.split(",", -1);
            String[] fields = new String[fieldNames.length];
            for (int i = 0; i < fieldNames.length; i++) {
                fields[i] = (i < rawFields.length) ? rawFields[i].trim().replaceAll("\"", "") : "";
            }

            if (rawFields.length < fieldNames.length) {
                System.out.println("Row " + rowNum + " had only " + rawFields.length + " fields. Padding with empty strings.");
            }


            Map<String, String> fieldErrors = new LinkedHashMap<>();

           // if (fields.length < fieldNames.length) {
           //     errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
            //    continue;
           // }

           // for (int i = 0; i < fieldNames.length; i++) {
           //     if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
           //         fieldErrors.put(fieldNames[i], "is mandatory");
           //     }
          //  }
           
            // Validate Date Format
//            String[] dateValues = { fields[3], fields[11], fields[25], fields[26] }; // actual values
//            String[] dateFieldNames = { "dateOfBirth", "doj", "policeVerificationDate", "healthCheckDate" };
//
//            for (int i = 0; i < dateValues.length; i++) {
//                String value = dateValues[i];
//                if (!value.isBlank()) {
//                    try {
//                        LocalDate parsed = LocalDate.parse(value, dateFormatter);
//                    } catch (DateTimeException e) {
//                        fieldErrors.put(dateFieldNames[i], "Invalid date format, expected yyyy-MM-dd");
//                    }
//                }
//            }


            // Lookups
            Integer unitId = fileUploadDao.getUnitIdByName(fields[29]);
            Integer contractorId = fileUploadDao.getContractorIdByName( fields[9]);
            Integer tradeId = fileUploadDao.getTradeIdByUnitId(unitId,fields[4]);
            Integer skillId = fileUploadDao.getSkillIdByTradeId(unitId,tradeId,fields[5]);
            Integer departmentId = fileUploadDao.getdepartmentIdByUnitId(unitId,fields[12]);
            Integer accessAreaId = fileUploadDao.getGeneralMasterId(fields[27]);
            Integer bloodGroupId = fileUploadDao.getGeneralMasterId(fields[19]);
            Integer areaId = fileUploadDao.getAreaByDeptID(unitId,departmentId,fields[13]);
            Integer academicId = fileUploadDao.getGeneralMasterId(fields[18]);
            Integer wcecId = fileUploadDao.getWCECId(fields[32],unitId,contractorId,fields[14]);
            //Integer workorderId = fileUploadDao.getWorkorderId(fields[14],unitId,contractorId);
            Integer workorderId = fileUploadDao.getWorkorderIdBasedonPE(fields[14],unitId);
            //Integer zoneId = fileUploadDao.getGeneralMasterId(fields[40]);
            Integer zoneId = fileUploadDao.getZoneIdFromMinimumWage(fields[40],unitId);
            Integer genderId = fileUploadDao.getGeneralMasterId(fields[10]);
            Integer eicId = fileUploadDao.geteicId(fields[12],unitId,fields[31]);
            Integer LLNumber = fileUploadDao.getLlNumber(fields[38],unitId,contractorId,fields[14]);

          
            	Integer trade = !fields[4].isBlank()&& unitId != null ? fileUploadDao.getTradeIdByUnitId(unitId,fields[4]) : null;
                if (!fields[4].isBlank() && trade == null) fieldErrors.put("trade", "Mapping not Found for Trade with Principal Employeer");

                Integer skill = !fields[5].isBlank() && unitId != null && tradeId != null? fileUploadDao.getSkillIdByTradeId(unitId,tradeId,fields[5]) : null;
                if (!fields[5].isBlank() && skill == null) fieldErrors.put("trade", "Mapping not Found for trade with Principal Employeer");

                Integer department = !fields[12].isBlank()&& unitId != null ? fileUploadDao.getdepartmentIdByUnitId(unitId,fields[12]) : null;
                if (!fields[12].isBlank() && department == null) fieldErrors.put("department", "Mapping not Found for Department with Principal Employeer");
                
                Integer area = !fields[13].isBlank()&& unitId != null&& departmentId != null ? fileUploadDao.getAreaByDeptID(unitId,departmentId,fields[13]) : null;
                if (!fields[13].isBlank() && area == null) fieldErrors.put("department", "Mapping not Found for Department with Principal Employeer");
             
                Integer ll = (!fields[38].isBlank() && unitId != null && contractorId != null && !fields[14].isBlank())
                        ? fileUploadDao.getLlNumber(fields[38], unitId, contractorId,fields[14])
                        : null;
                    if (!fields[38].isBlank() && ll == null) fieldErrors.put("LL", "Invalid or not found");

                Integer eic = (!fields[12].isBlank() && unitId != null && !fields[31].isBlank())
                    ? fileUploadDao.geteicId(fields[12], unitId, fields[31])
                    : null;
                if (!fields[31].isBlank() && eic == null) fieldErrors.put("EIC", "Invalid or not found");

                Integer workorder = (!fields[14].isBlank() && unitId != null)
                        ? fileUploadDao.getWorkorderIdBasedonPE(fields[14], unitId)
                        : null;
                    if (!fields[14].isBlank() && workorder == null) fieldErrors.put("workorder", "No Workorrder Mapped with Principal Employeer");

                Integer wcec = (!fields[32].isBlank() && unitId != null && contractorId != null && fields[14]!=null)
                        ? fileUploadDao.getWCECId(fields[32], unitId, contractorId,fields[14])
                        : null;
                    if (!fields[32].isBlank() && wcec == null) fieldErrors.put("WCEC", "Invalid or not found");

                    Integer academic = !fields[18].isBlank() ? fileUploadDao.getGeneralMasterId(fields[18]) : null;
                    if (!fields[18].isBlank() && academic == null) fieldErrors.put("academic", "Invalid or not found");
                    
                    Integer bloodgroup = !fields[19].isBlank() ? fileUploadDao.getGeneralMasterId(fields[19]) : null;
                    if (!fields[19].isBlank() && bloodgroup == null) fieldErrors.put("bloodgroup", "Invalid or not found");

                    Integer gender = !fields[10].isBlank() ? fileUploadDao.getGeneralMasterId(fields[10]) : null;
                    if (!fields[10].isBlank() && gender == null) fieldErrors.put("gender", "Invalid or not found");

                    Integer accessArea = !fields[27].isBlank() ? fileUploadDao.getGeneralMasterId(fields[27]) : null;
                    if (!fields[27].isBlank() && accessArea == null) fieldErrors.put("accessArea", "Invalid or not found");

                    Integer zone = !fields[40].isBlank() && unitId != null ? fileUploadDao.getZoneIdFromMinimumWage(fields[40],unitId) : null;
                    if (!fields[40].isBlank() && zone == null) fieldErrors.put("zone", "Invalid or not found");
        
            
            /*	Integer academic = !fields[18].isBlank() ? fileUploadDao.getGeneralMasterId(fields[18]) : null;
                if (!fields[18].isBlank() && academic == null) fieldErrors.put("academic", "Invalid or not found");
                
                Integer bloodgroup = !fields[19].isBlank() ? fileUploadDao.getGeneralMasterId(fields[19]) : null;
                if (!fields[19].isBlank() && bloodgroup == null) fieldErrors.put("bloodgroup", "Invalid or not found");

                Integer gender = !fields[10].isBlank() ? fileUploadDao.getGeneralMasterId(fields[10]) : null;
                if (!fields[10].isBlank() && gender == null) fieldErrors.put("gender", "Invalid or not found");

                Integer accessArea = !fields[27].isBlank() ? fileUploadDao.getGeneralMasterId(fields[27]) : null;
                if (!fields[27].isBlank() && accessArea == null) fieldErrors.put("accessArea", "Invalid or not found");

                Integer zone = !fields[40].isBlank() ? fileUploadDao.getGeneralMasterId(fields[40]) : null;
                if (!fields[40].isBlank() && zone == null) fieldErrors.put("zone", "Invalid or not found");
   
            */
                 // Duplicate aadhar check
                    String aadharNumber = fields[8];
                    boolean aadharNumberExists = fileUploadDao.isAadharNumberExists(aadharNumber);
                    if (aadharNumberExists) {
                    	fieldErrors.put("aadharNumber", "Duplicate aadharNumber: " + aadharNumber + " already exists");
                    }

            if (!fieldErrors.isEmpty()) {
                errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                continue;
            }

            try {
                WorkmenBulkUpload staging = new WorkmenBulkUpload();
                staging.setFirstName(fields[0]);
                staging.setLastName(fields[1]);
                staging.setRelationName(fields[2]);
                staging.setDateOfBirth(fields[3]);
                staging.setTrade(tradeId!=null?String.valueOf(tradeId):null);               
                staging.setSkill(skillId!=null?String.valueOf(skillId):null);
                staging.setNatureOfWork(fields[6]);
                staging.setHazardousArea(fields[7]);
                staging.setAadhaarNumber(aadharNumber);
                staging.setVendorCode(contractorId!=null?String.valueOf(contractorId):null);
                staging.setGender(genderId!=null?String.valueOf(genderId):null);
                staging.setDoj(fields[11]);
                staging.setDepartment(departmentId!=null?String.valueOf(departmentId):null);
                staging.setArea(areaId!=null?String.valueOf(areaId):null);
                staging.setWorkorderNumber(workorderId!=null?String.valueOf(workorderId):null);
                staging.setPfNumber(fields[15]);
                staging.setMaritalStatus(fields[16]);
                staging.setTechnical(fields[17]);
                staging.setAcademic(academicId!=null?String.valueOf(academicId):null);
                staging.setBloodGroup(bloodGroupId!=null?String.valueOf(bloodGroupId):null);
                staging.setAccommodation(fields[20]);
                staging.setBankName(fields[21]);
                staging.setAccountNumber(fields[22]);
                staging.setMobileNumber(fields[23]);
                staging.setEmergencyNumber(fields[24]);
                staging.setPoliceVerificationDate(fields[25]);
                staging.setHealthCheckDate(fields[26]);
                staging.setAccessArea(accessAreaId!=null?String.valueOf(accessAreaId):null);
                staging.setEsicNumber(fields[28]);
                staging.setUnitCode(unitId!=null?String.valueOf(unitId):null);
                staging.setOrganizationName(fields[30]);
                staging.setEICNumber(eicId!=null?String.valueOf(eicId):null);
                staging.setECnumber(wcecId!=null?String.valueOf(wcecId):null);
                staging.setUanNumber(fields[33]);
                staging.setEmergencyName(fields[34]);
                staging.setPfApplicable(fields[35]);
                staging.setSpecializationName(fields[36]);
                staging.setInsuranceType(fields[37]);
                staging.setLLnumber(LLNumber!=null?String.valueOf(LLNumber):null);
                staging.setAddress(fields[39]);
                staging.setZone(zoneId!=null?String.valueOf(zoneId):" ");
                staging.setIdMark(fields[41]);

                int transactionId = fileUploadDao.saveWorkmenBulkDraftUploadToStaging(staging); // RETURN the transaction ID
                savedTransactionIds.add(transactionId); // collect for post validation
 
                // For success list
                Map<String, Object> rowMap = new LinkedHashMap<>();
                for (int i = 0; i < fieldNames.length; i++) {
                    rowMap.put(fieldNames[i], fields[i]);
                }
                successData.add(rowMap);

                fileUploadDao.saveToGatePassMain(staging);
                
            } catch (Exception e) {
                errorData.add(Map.of("row", rowNum, "error", "Exception while processing row: " + e.getMessage()));
            }
        }
		/*
		 * try { fileUploadDao.saveToGatePassMain(staging); } catch (Exception e) {
		 * errorData.add(Map.of("row", "Procedure", "error", "Stored Procedure Failed: "
		 * + e.getMessage())); }
		 */
        // ✅ Automatically validate & save to GATEPASSMAIN
       // if (!savedTransactionIds.isEmpty()) {
       //     Map<String, Object> gatePassResult = validateAndSaveSelectedWorkmen(savedTransactionIds);
       //     errorData.addAll((List<Map<String, Object>>) gatePassResult.get("errorData")); // append validation errors
       // }

       
        Map<String, Object> result = new HashMap<>();
        result.put("successData", successData);
        result.put("errorData", errorData);
        return result;
    }

   // @Override
	//public List<WorkmenBulkUpload> getAllWorkmenBulkDraftUploadData() {
	//	return fileUploadDao.getAllWorkmenBulkDraftUploadData();
	//}
    //@Override
	public Map<String, Object> validateAndSaveSelectedWorkmen(List<Integer> transactionIds) {
	    List<Map<String, Object>> successData = new ArrayList<>();
	    List<Map<String, Object>> errorData = new ArrayList<>();

	    for (Integer txnId : transactionIds) {
	        WorkmenBulkUpload record = fileUploadDao.getByTransactionId(txnId);

	        if (record == null) {
	            String errMsg = "No record found for transactionId: " + txnId;
	            errorData.add(Map.of("transactionId", txnId, "error", errMsg));
	            fileUploadDao.updateRecordStatusByTransactionId(txnId, errMsg);
	            continue;
	        }

	        Map<String, String> fieldErrors = new LinkedHashMap<>();

		     try {
		            record.setUnitCode(String.valueOf(fileUploadDao.getUnitIdByName(record.getUnitCode())));
		            record.setVendorCode(String.valueOf(fileUploadDao.getContractorIdByName(record.getVendorCode())));
		            record.setWorkorderNumber(String.valueOf(fileUploadDao.getWorkorderIdBasedonPE(record.getWorkorderNumber(),Integer.parseInt(record.getUnitCode()))));
		            record.setTrade(String.valueOf(fileUploadDao.getTradeIdByName(record.getTrade())));
		            record.setSkill(String.valueOf(fileUploadDao.getSkillIdByName(record.getSkill())));
		           // record.setDepartment(String.valueOf(fileUploadDao.getGeneralMasterId(record.getDepartment())));
		            record.setArea(String.valueOf(fileUploadDao.getGeneralMasterId(record.getArea())));
		            record.setAccessArea(String.valueOf(fileUploadDao.getGeneralMasterId(record.getAccessArea())));
		            record.setBloodGroup(String.valueOf(fileUploadDao.getGeneralMasterId(record.getBloodGroup())));
		            record.setAcademic(String.valueOf(fileUploadDao.getGeneralMasterId(record.getAcademic())));
		            record.setECnumber(String.valueOf(fileUploadDao.getWCECId(record.getECnumber(),Integer.parseInt(record.getUnitCode()),Integer.parseInt(record.getVendorCode()),record.getWorkorderNumber())));
		            record.setZone(String.valueOf(fileUploadDao.getGeneralMasterId(record.getZone())));
		            record.setGender(String.valueOf(fileUploadDao.getGeneralMasterId(record.getGender())));
		            record.setEICNumber(String.valueOf(fileUploadDao.geteicId(record.getDepartment(),Integer.parseInt(record.getUnitCode()),record.getEICNumber())));
		            record.setDepartment(String.valueOf(fileUploadDao.getGeneralMasterId(record.getDepartment())));
		            
		        } catch (Exception e) {
		            fieldErrors.put("IDMapping", "Failed to resolve IDs: " + e.getMessage());
		        }
	        if (!fieldErrors.isEmpty()) {
	            String combinedErrors = fieldErrors.entrySet().stream()
	                .map(e -> e.getKey() + ": " + e.getValue())
	                .collect(Collectors.joining("; "));

	            errorData.add(Map.of("transactionId", txnId, "fieldErrors", fieldErrors));
	            fileUploadDao.updateRecordStatusByTransactionId(txnId, combinedErrors);
	        } else {
	        	fileUploadDao.saveToGatePassMain(record);
	            successData.add(Map.of("transactionId", txnId));
	           // fileUploadDao.updateRecordProcessedByTransactionId(txnId);
	        }
	    }

	    return Map.of("successData", successData, "errorData", errorData);
	}

	@Override
    public String getTemplateCSV(String templateType) {
        return fileUploadDao.getCSVHeaders(templateType);
    }
	@Transactional
	public boolean InsertPEOrgLevelEntry(List<PrincipalEmployer> list) {

	    if (list == null || list.isEmpty()) return true;

	    try {
	        long orgLevelDefId = fileUploadDao.getOrgLevelDefId("principal employer");

	        if (orgLevelDefId <= 0) return false;

	        Set<String> existingCodes =
	                fileUploadDao.getExistingPECodes(list, orgLevelDefId);

	        List<PrincipalEmployer> newList = list.stream()
	                .filter(pe -> pe.getCode() != null &&
	                        !existingCodes.contains(pe.getCode().trim().toUpperCase()))
	                .toList();

	        if (newList.isEmpty()) return true;

	        return fileUploadDao.SavePEOrglevelEntry(newList, orgLevelDefId);

	    } catch (Exception e) {
	        log.error("ORGLEVELENTRY insert failed", e);
	        return false;
	    }
	}

	
//	@Transactional
//	public boolean InsertContractorOrgLevelEntry(List<Contractor> list) {
//
//	    if (list == null || list.isEmpty()) {
//	        return true;
//	    }
//
//	    try {
//	        long orgLevelDefId = fileUploadDao.getOrgLevelDefId("contractor");
//
//	        if (orgLevelDefId <= 0) {
//	            return false;
//	        }
//
//	        boolean exists = fileUploadDao.codeExistsInOrgLevelEntry(list, orgLevelDefId);
//
//	        if (exists) {
//	            return true; // Data already exists → skip insert
//	        }
//
//	        return fileUploadDao.SaveContOrglevelEntry(list, orgLevelDefId);
//
//	    } catch (Exception e) {
//	        log.error("ORGLEVELENTRY insert failed", e);
//	        return false;
//	    }
//	}
	@Transactional
	public boolean InsertContractorOrgLevelEntry(List<Contractor> list) {

	    if (list == null || list.isEmpty()) {
	        return true;
	    }

	    try {
	        long orgLevelDefId = fileUploadDao.getOrgLevelDefId("contractor");

	        if (orgLevelDefId <= 0) {
	            return false;
	        }

	        // ✅ Get existing codes
	        Set<String> existingCodes =
	                fileUploadDao.getExistingContractorCodes(list, orgLevelDefId);

	        // ✅ Filter only NEW contractor codes
	        List<Contractor> newList = list.stream()
	                .filter(c -> c.getContractorCode() != null &&
	                        !existingCodes.contains(c.getContractorCode()))
	                .toList();

	        // ✅ If nothing to insert → skip
	        if (newList.isEmpty()) {
	            return true;
	        }

	        // ✅ Insert only missing records
	        return fileUploadDao.SaveContOrglevelEntry(newList, orgLevelDefId);

	    } catch (Exception e) {
	        log.error("ORGLEVELENTRY insert failed", e);
	        return false;
	    }
	}

	@Transactional
	public boolean InsertWorkorderOrgLevelEntry(List<KTCWorkorderStaging> list) {

		 if (list == null || list.isEmpty()) {
		        return true; // nothing to insert
		    }
		 
	    try {
	        long orgLevelDefId = fileUploadDao.getOrgLevelDefId("work order");

	        if (!logAndCheck("ORGLEVELDEF", orgLevelDefId > 0)) {
	            return false;
	        }

	        boolean saved = fileUploadDao.SaveWorkorderOrglevelEntry(list, orgLevelDefId);

	        if (!logAndCheck("ORGLEVELENTRY", saved)) {
	            return false;
	        }

	        return true;

	    } catch (Exception e) {
	        log.error("ORGLEVELENTRY Batch Insert FAILED : " + e.getMessage(), e);
	        return false;
	    }
	}

	@Transactional
	public boolean batchInsertDeptOrgLevelEntry(List<DeptMapping> list, String type) {

	    if (list == null || list.isEmpty()) return true;

	    try {
	        String orgType = type.equalsIgnoreCase("Department") ? "Dept" : "Area";
	        long orgLevelDefId = fileUploadDao.getOrgLevelDefId(orgType);

	        List<DeptMapping> finalList = new ArrayList<>();

	        for (DeptMapping dm : list) {

	            String name = orgType.equals("Dept")
	                    ? dm.getDepartment()
	                    : dm.getSubDepartment();

	            if (name == null || name.isBlank()) continue;

	            boolean exists = fileUploadDao.existsInOrgLevelEntry(name, orgLevelDefId);

	            if (!exists) {
	                finalList.add(dm);   // add only unique & not existing in DB
	            }
	        }

	        if (finalList.isEmpty()) {
	            return true;
	        }

	        if (orgType.equals("Dept")) {
	            return fileUploadDao.saveDeptOrgLevelEntry(finalList, orgLevelDefId);
	        } else {
	            return fileUploadDao.saveAreaOrgLevelEntry(finalList, orgLevelDefId);
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}

	private boolean logAndCheck(String label, boolean success) {
	    log.info(label + " : " + (success ? "SUCCESS" : "FAILED"));
	    return success;
	}
	private Map<String, Object> processBulkCancel(BufferedReader reader,String userAccount,String createdBy) throws IOException {
   	 List<Map<String, Object>> successData = new ArrayList<>();
        List<Map<String, Object>> errorData = new ArrayList<>();

        String line;
        int rowNum = 0;
        String[] fieldNames = {"gatepassNumber", "cancelReason"};
               
        Set<String> mandatoryFields = Set.of("gatepassNumber", "cancelReason");

            while ((line = reader.readLine()) != null) {
                rowNum++;
                line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");

                if (line.trim().isEmpty()) continue;

                String[] rawFields = line.split(",", -1);
                String[] fields = new String[rawFields.length];
                for (int i = 0; i < rawFields.length; i++) {
                    fields[i] = rawFields[i].trim().replaceAll("\"", "");
                }

                Map<String, String> fieldErrors = new LinkedHashMap<>();

                if (fields.length < 2) {
                    errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                    continue;
                }

                // Check mandatory fields
                for (int i = 0; i < fieldNames.length; i++) {
                    if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
                        fieldErrors.put(fieldNames[i], "is mandatory");
                    }
                }
                String cancelReason = fields[1];
                
                boolean gatepassNumber = fileUploadDao.gatepassNumberExists(fields[0]);
                if (!gatepassNumber) {
               	 fieldErrors.put("gatepassNumber" ,"Gatepass Number is not Found");
               	 
                }
                
                Integer cancelReasonId = fileUploadDao.getGeneralMasterId(cancelReason);
                if (cancelReasonId == null || cancelReasonId == 0) {
               	 fieldErrors.put("cancelReason" ,"Cancel of Reason is not Found"); 
                }
                

                if (!fieldErrors.isEmpty()) {
                    errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                    continue;
                }
                try {
                	 List<String> gpm = fileUploadDao.getContractorDetailsForCancel(fields[0]);
                	 if (gpm == null) {
                		 throw new IllegalArgumentException("gatepassNumber" +   "Gatepass Details not Found");
                        // fieldErrors.put("gatepassNumber", "Gatepass Details not Found");
                     }
                	 String gpUnitId = gpm.get(0);
                     String gpContId = gpm.get(1);
                     String gpDeptId = gpm.get(2);
                     
                     List<PersonOrgLevel> orgLevel = commonService.getPersonOrgLevelDetails(userAccount);
                 	Map<String,List<PersonOrgLevel>> groupedByLevelDef = orgLevel.stream()
                 			.collect(Collectors.groupingBy(PersonOrgLevel::getLevelDef));
                 	List<PersonOrgLevel> peList = groupedByLevelDef.getOrDefault("Principal Employer", new ArrayList<>());
                	List<PersonOrgLevel> departments = groupedByLevelDef.getOrDefault("Dept", new ArrayList<>());
                	List<PersonOrgLevel> contractor = groupedByLevelDef.getOrDefault("Contractor", new ArrayList<>());
                	
                	Set<String> userPEIds = peList.stream().map(PersonOrgLevel::getId).collect(Collectors.toSet());
                	Set<String> userDeptIds = departments.stream().map(PersonOrgLevel::getId).collect(Collectors.toSet());
                	Set<String> userContIds = contractor.stream().map(PersonOrgLevel::getId).collect(Collectors.toSet());

                	boolean hasPEAccess   = userPEIds.contains(gpUnitId);
                	boolean hasDeptAccess = userDeptIds.contains(gpDeptId);
                	boolean hasContAccess = userContIds.contains(gpContId);
                	
                	if (!hasPEAccess &&! hasDeptAccess && !hasContAccess) {
                		 throw new IllegalArgumentException("Logged-in user does not have access to cancel gatepass");
                       }

                 BulkCancel bc = new BulkCancel();
               	 bc.setGatepassNumber(fields[0]);
               	 bc.setCancelReason(String.valueOf(cancelReasonId));
               	 
                 fileUploadDao.updateGatepass(bc,createdBy);

           // Map each POJO to a simple map for JSON-friendly structure
           Map<String, Object> map = new HashMap<>();
           map.put("gatepassNumber", bc.getGatepassNumber());
           map.put("cancelReason",cancelReason);
           
           successData.add(map);
           //make api call to UKG
           api.updateEmpStatusTerOrAct(bc.getGatepassNumber(),EmployeeStatusType.CANCEL);
                } catch (Exception e) {
                    errorData.add(Map.of("row", rowNum, "error", "" + e.getMessage()));
                }
            }

            Map<String, Object> result = new HashMap<>();
            result.put("successData", successData);
            result.put("errorData", errorData);
            return result;
   }
	private Map<String, Object> processBulkRenew(BufferedReader reader, String userAccount, String createdBy) throws IOException {

	    List<Map<String, Object>> successData = new ArrayList<>();
	    List<Map<String, Object>> errorData = new ArrayList<>();

	    String line;
	    int rowNum = 0;

	    String[] fieldNames = {"gatepassNumber", "workorderNumber", "wcNumber", "llNumber"};
	    Set<String> mandatoryFields = Set.of("gatepassNumber", "workorderNumber", "wcNumber");

	    while ((line = reader.readLine()) != null) {
	        rowNum++;
	        line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");
	        if (line.trim().isEmpty()) continue;

	        String[] rawFields = line.split(",", -1);
	        String[] fields = new String[rawFields.length];
	        for (int i = 0; i < rawFields.length; i++) {
	            fields[i] = rawFields[i].trim().replaceAll("\"", "");
	        }

	        Map<String, String> fieldErrors = new LinkedHashMap<>();

	        // ================= Field Count Check =================
	        if (fields.length < 4) {
	            errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
	            continue;
	        }

	        String gatepassNumber = fields[0];
	        String workorderNumber = fields[1];
	        String wcNumber = fields[2];
	        String llNumber = fields[3];

	        // ================= 1️ Mandatory Fields Check =================
	        for (int i = 0; i < fieldNames.length; i++) {
	            if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
	                fieldErrors.put(fieldNames[i], "is mandatory");
	            }
	        }

	        if (!fieldErrors.isEmpty()) {
	            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
	            continue;
	        }

	        // ================= 2️ Gatepass Validation =================
	        boolean gatepassExists = fileUploadDao.gatepassNumberExists(gatepassNumber);
	        if (!gatepassExists) {
	            fieldErrors.put("gatepassNumber", "Gatepass Number not Found");
	            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
	            continue;
	        }

	        // ================= Fetch Gatepass Details =================
	        List<String> gpm = fileUploadDao.getContractorDetailsForCancel(gatepassNumber);
	        if (gpm == null) {
	            fieldErrors.put("gatepassNumber", "Gatepass Details not Found");
	            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
	            continue;
	        }

	        String gpUnitId = gpm.get(0);
	        String gpContId = gpm.get(1);
	        String gpDeptId = gpm.get(2);

	        // ================= 3️ Workorder Mapping Check =================
//	        Integer woid = fileUploadDao.WorkorderExists(workorderNumber, gpContId);
//	        if (woid == null) {
//	            fieldErrors.put("workorderNumber", "Workorder Number not Mapped to Contractor");
//	            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
//	            continue;
//	        }
	        Map<String, Object> woData = fileUploadDao.workorderExists(workorderNumber, gpContId);

	        if (woData == null) {
	            fieldErrors.put("workorderNumber", "Workorder Number not Mapped to Contractor");
	            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
	            continue;
	        }

	        Integer woid = ((Number) woData.get("WORKORDERID")).intValue();
	        Date validDt = (Date) woData.get("VALIDDT");
	        
	        if (validDt == null || !validDt.after(new Date())) {
	            fieldErrors.put("workorderNumber", "Workorder Expiry Date must be in future");
	        }
	        // ================= 4️ WC / LL Mapping Check =================
	        Integer wcesicid = null;
	        if (wcNumber != null && !wcNumber.isBlank()) {
	            wcesicid = fileUploadDao.WCESICExists(workorderNumber, wcNumber);
	            if (wcesicid == null) {
	                fieldErrors.put("wcNumber", "WC/ESIC Number not Mapped to Workorder");
	            }
	        }

	        Integer llid = null;
	        if (llNumber != null && !llNumber.isBlank()) {
	            llid = fileUploadDao.LLExists(workorderNumber, llNumber);
	            if (llid == null) {
	                fieldErrors.put("llNumber", "LL Number not Mapped to Workorder");
	            }
	        }

	        if (!fieldErrors.isEmpty()) {
	            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
	            continue;
	        }

	        // ================= 5️ Logged-in User Access Check =================
	        List<PersonOrgLevel> orgLevel = commonService.getPersonOrgLevelDetails(userAccount);
	        Map<String, List<PersonOrgLevel>> groupedByLevelDef =
	                orgLevel.stream().collect(Collectors.groupingBy(PersonOrgLevel::getLevelDef));

	        Set<String> userPEIds = groupedByLevelDef.getOrDefault("Principal Employer", List.of())
	                .stream().map(PersonOrgLevel::getId).collect(Collectors.toSet());

	        Set<String> userDeptIds = groupedByLevelDef.getOrDefault("Dept", List.of())
	                .stream().map(PersonOrgLevel::getId).collect(Collectors.toSet());

	        Set<String> userContIds = groupedByLevelDef.getOrDefault("Contractor", List.of())
	                .stream().map(PersonOrgLevel::getId).collect(Collectors.toSet());

	        if (!userPEIds.contains(gpUnitId) && !userDeptIds.contains(gpDeptId) && !userContIds.contains(gpContId)) {
	            fieldErrors.put("access", "Logged-in user does not have access to renew gatepass");
	            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
	            continue;
	        }

	        // ================= Build GatePass Object =================
	        GatePassMain gm = new GatePassMain();
	        gm.setGatePassId(gatepassNumber);
	        gm.setWorkorder(String.valueOf(woid));
	        gm.setWcEsicNo(String.valueOf(wcesicid));
	        gm.setLlNo(llid != null ? String.valueOf(llid) : "");

	        String dot = this.getDOT(gm, gpUnitId);

	        // ================= 6️ License Validation =================
	        Map<String, String> licenseErrors = this.workmenCountCheck(gm, gpUnitId, gpContId);

	        if (!licenseErrors.isEmpty()) {
	            for (Map.Entry<String, String> entry : licenseErrors.entrySet()) {
	                fieldErrors.put(entry.getKey(), "GatePass " + gatepassNumber + " : " + entry.getValue());
	            }
	            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
	            continue;
	        }
      try {
	        // ================= 7️ Update Gatepass =================
	        fileUploadDao.updateGatepassBulkRenew(gm, createdBy, dot);

	        // Success Map
	        Map<String, Object> map = new HashMap<>();
	        map.put("gatepassNumber", gatepassNumber);
	        map.put("workorderNumber", workorderNumber);
	        map.put("wcNumber", wcNumber);
	        map.put("llNumber", llNumber);

	        successData.add(map);
       }catch (Exception ex) {

            log.error("Rollback happened for GatePassId: {}", gatepassNumber, ex);

    //  Add rollback error message
        errorData.add(Map.of("row", rowNum,"gatepassNumber", gatepassNumber,"error", "Failed Bulk Renew for GatePassId: " + gatepassNumber));
    continue; // move to next row
   }
 }

	    Map<String, Object> result = new HashMap<>();
	    result.put("successData", successData);
	    result.put("errorData", errorData);
	    return result;
	}
	public Map<String, String> workmenCountCheck(GatePassMain gatePassMain,String gpUnitId,String gpContId) {
		Map<String, String> errors = new LinkedHashMap<>();
		
		 // ❗ If plant is NOT allowed → return error immediately
		 String plantValidation = plantCountCheck(gpUnitId);
		 String contractorBlockedValidation = contractorBlockedCheck(gpContId);

		    if (!ALLOW.equalsIgnoreCase(plantValidation)) {
		        errors.put("plantCount",plantValidation);
		        return errors;
		    }
		    if (!CONTRACTORALLOW.equalsIgnoreCase(contractorBlockedValidation)) {
		        errors.put("contractorBlocked",contractorBlockedValidation);
		        return errors;
		    }
	    
		int activeCount = workmenDao.getActiveWorkmenCount(gpUnitId, gpContId, GatePassStatus.APPROVED.getStatus(), GatePassType.CREATE.getStatus());
		int llDeployedCount = workmenDao.getLLDeploymentCountByUnitId(gpUnitId);
		if(activeCount < llDeployedCount) {
			return errors;
		}else {
			return licenseExistsAndCount(gatePassMain,gpUnitId,gpContId, activeCount);
			
		}
	}
	private static final String ALLOW = "ALLOW";
	private static final String EXCEEDED = "Plant maximum contract workmen count exceeded";
	
	private String plantCountCheck(String principalEmployer) {

	    Map<String, Integer> counts = workmenDao.getPlantCounts(principalEmployer);

	    int maxCount = counts.get("maxCount");
	    int activeCount = counts.get("activeCount");

	    if (activeCount >= maxCount) {
	        return EXCEEDED;
	    }

	    return ALLOW;
	}
	
	private static final String CONTRACTORALLOW = "CONTRACTORALLOW";
	private static final String CONTRACTORBLOCKED = "Contractor is Blocked";

	private String contractorBlockedCheck(String contractorId) {

	    boolean isBlocked = workmenDao.checkContractorBlocked(contractorId);

	    if (isBlocked) {
	        return CONTRACTORBLOCKED;
	    }

	    return CONTRACTORALLOW;
	}
	public Map<String, String> licenseExistsAndCount(GatePassMain gatePassMain,String gpUnitId,String gpContId, int activeCount) {

	    Map<String, String> errors = new LinkedHashMap<>();

	    String unit =gpUnitId ;
	    String contractor = gpContId;
	    String workorder = gatePassMain.getWorkorder();

	    // ---------- Fetch Results ----------
	    Map<String, Object> llResult =
	            workmenDao.licenseExistsAndCount(unit, contractor, workorder, "LL", gatePassMain.getLlNo());

	    Map<String, Object> wcResult =
	            workmenDao.licenseExistsAndCount(unit, contractor, workorder, "WC", gatePassMain.getWcEsicNo());

	    Map<String, Object> esicResult = null;
	    boolean hasEsic =
	            gatePassMain.getEsicNumber() != null &&
	            !gatePassMain.getEsicNumber().trim().isEmpty() &&
	            !"NULL".equalsIgnoreCase(gatePassMain.getEsicNumber().trim());


	    if (hasEsic) {
	        esicResult =
	                workmenDao.licenseExistsAndCount(unit, contractor, workorder, "ESIC", gatePassMain.getWcEsicNo());
	    }

	    // ---------- Extract LL ----------
	    boolean llExists = (Boolean) llResult.get("exists");
	    int llCount = (Integer) llResult.get("count");
	    Date llExpiry = (Date) llResult.get("expiryDate");

	    // ---------- Extract WC ----------
	    boolean wcExists = (Boolean) wcResult.get("exists");
	    int wcCount = (Integer) wcResult.get("count");
	    Date wcExpiry = (Date) wcResult.get("expiryDate");

	    // ---------- Extract ESIC ----------
	    boolean esicExists = false;
	    int esicCount = 0;
	    Date esicExpiry = null;

	    if (hasEsic && esicResult != null) {
	        esicExists = (Boolean) esicResult.get("exists");
	        esicCount = (Integer) esicResult.get("count");
	        esicExpiry = (Date) esicResult.get("expiryDate");
	    }

	    Date today = new Date();

	    // ---------- LL Mandatory ----------
	    boolean llMandatory =
	            !llExists ||
	            (
	                (llExpiry != null && llExpiry.before(today)) ||
	                (llExpiry != null && !llExpiry.before(today) && llCount == 0)
	            );

	    // ---------- WC / ESIC Mandatory (either one is enough) ----------
	    boolean wcInvalid =
	            !wcExists ||
	            (wcExpiry != null && wcExpiry.before(today)) ||
	            (wcExpiry != null && !wcExpiry.before(today) && wcCount == 0);

	    boolean esicInvalid = hasEsic &&
	            (
	                !esicExists ||
	                (esicExpiry != null && esicExpiry.before(today)) ||
	                (esicExpiry != null && !esicExpiry.before(today) && esicCount == 0)
	            );

	    boolean wcEsicMandatory = wcInvalid && esicInvalid;

	    // ================= Mandatory Validation =================
	    if (llMandatory || wcEsicMandatory) {
	    	 if (llMandatory && wcEsicMandatory) {
	        errors.put("llNumber", "LL and WC/ESIC are mandatory");
	    }if (llMandatory) {
	    	 errors.put("llNumber", "LL is mandatory");
	    }if (wcEsicMandatory) {
	    errors.put("wcNumber", "WC/ESIC is mandatory");
	    }
	   }
	    if (!wcExists && !esicExists) {
	        errors.put("wcNumber", "WC/ESIC is mandatory");
	    }
	    if (llExists && llExpiry != null && llExpiry.before(today)) {
	        errors.put("llExpiryDate", "LL should have Future Dates");
	    }
	    if (wcExists && wcExpiry != null && wcExpiry.before(today)) {
	        errors.put("wcExpiryDate", "WC should have Future Dates");
	    }

	    if (hasEsic && esicExists && esicExpiry != null && esicExpiry.before(today)) {
	        errors.put("esicExpiryDate", "ESIC should have Future Dates");
	    }

	    // ================= Capacity Validation =================

	    if (llExists && activeCount >= llCount) {
	        errors.put("llNumber", "LL capacity exceeded");
	    }

	    if (wcExists && activeCount >= wcCount) {
	        errors.put("wcNumber", "WC capacity exceeded");
	    }

	    if (hasEsic && esicExists && activeCount >= esicCount) {
	        errors.put("esicNumber", "ESIC capacity exceeded");
	    }

	    return errors;
	}
	 private String getDOT(GatePassMain gatePassMain,String gpUnitId) {

		    String dot = null;
		    int dotTypeId = gatePassMain.getDotType();

		    if(dotTypeId == 0) {
		    	
		    	 dotTypeId = workmenDao.getDOTTYpe(gpUnitId);
		    }
		    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		    // ---------------- Retirement handling ----------------
		    if (dotTypeId == DotType.RETIRE.getStatus()) {
		        int retirementAge = 60;
		        LocalDate dob = LocalDate.parse(gatePassMain.getDateOfBirth(), formatter);
		        return dob.plusYears(retirementAge).format(formatter);
		    }

		    // ---------------- Fetch validity dates ----------------
		    Map<String, LocalDate> validityDates =
		            workmenDao.getValidityDates(
		                    gatePassMain.getWorkorder(),
		                    gatePassMain.getWcEsicNo(),
		                    gatePassMain.getLlNo()   // may be null
		            );

		    LocalDate woDate = validityDates.get("WO");
		    LocalDate wcDate = validityDates.get("WC");
		    LocalDate llDate = validityDates.get("LL"); // may be null

		    // ---------------- DOT calculation ----------------
		    if (dotTypeId == DotType.LLWCWO.getStatus()) {

		        dot = getEarliestDate(Arrays.asList(woDate, wcDate, llDate), formatter);

		    } else if (dotTypeId == DotType.LLWC.getStatus()) {

		        dot = getEarliestDate(Arrays.asList(wcDate, llDate), formatter);

		    } else if (dotTypeId == DotType.LLWO.getStatus()) {

		        dot = getEarliestDate(Arrays.asList(woDate, llDate), formatter);

		    } else if (dotTypeId == DotType.WCWO.getStatus()) {

		        dot = getEarliestDate(Arrays.asList(woDate, wcDate), formatter);

		    } else if (dotTypeId == DotType.LL.getStatus()) {

		        dot = getEarliestDate(Collections.singletonList(llDate), formatter);

		    } else if (dotTypeId == DotType.WC.getStatus()) {

		        dot = getEarliestDate(Collections.singletonList(wcDate), formatter);

		    } else if (dotTypeId == DotType.WO.getStatus()) {

		        dot = getEarliestDate(Collections.singletonList(woDate), formatter);
		    }

		    return dot;
		}
	 private String getEarliestDate(List<LocalDate> dates, DateTimeFormatter formatter) {
		    return dates.stream()
		            .filter(Objects::nonNull)   // 🔑 LL null? silently ignored
		            .min(LocalDate::compareTo)
		            .map(d -> d.format(formatter))
		            .orElse("No valid date available");
		}
	 @Transactional
	 private Map<String, Object> processUser(BufferedReader reader) throws IOException {

		    List<Map<String, Object>> successData = new ArrayList<>();
		    List<Map<String, Object>> errorData = new ArrayList<>();

		    String line;
		    int rowNum = 0;

		    String[] fieldNames = {"firstName","lastName","userAccount","password","email","mobileNumber","plantCode","organisation","department","area","role","SAPVendorCode"};

		    Set<String> mandatoryFields = Set.of("firstName","lastName","userAccount","password","email","mobileNumber","plantCode","organisation","department","area","role");

		    while ((line = reader.readLine()) != null) {

		        rowNum++;
		        line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");

		        if (line.trim().isEmpty()) continue;

		        String[] rawFields = line.split(",", -1);
		        String[] fields = new String[rawFields.length];

		        for (int i = 0; i < rawFields.length; i++) {
		            fields[i] = rawFields[i].trim().replaceAll("\"", "");
		        }

		        Map<String, String> fieldErrors = new LinkedHashMap<>();

		        if (fields.length < fieldNames.length) {
		            errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
		            continue;
		        }

		        // Extract fields
		        String firstName = fields[0];
		        String lastName = fields[1];
		        String userAccount = fields[2];
		        String password = fields[3];
		        String email = fields[4];
		        String mobileNumber = fields[5];
		        String plantCode = fields[6];
		        String organisation = fields[7];
		        String department = fields[8];
		        String area = fields[9];
		        String role = fields[10];
		        String SAPVendorCode = fields[11];

		        // ✅ 1. MANDATORY FIELD VALIDATION

		        for (int i = 0; i < fieldNames.length; i++) {
		            if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
		                fieldErrors.put(fieldNames[i], "is mandatory");
		            }
		        }

		        if (!fieldErrors.isEmpty()) {
		            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
		            continue;
		        }
		        
		        // ✅ 2.CHECK USER EXISTS
		        
		        Integer userId=fileUploadDao.isUserExists(userAccount);
		        
		     // ✅ 3. ROLE VALIDATION (MULTIPLE ROLES)

		        String[] roleArray = role.split(";");
		        List<Long> roleIds = new ArrayList<>();
		        boolean isContractorRole = false;

		        for (String r : roleArray) {

		            String trimmedRole = r.trim();

		            Integer roleId = fileUploadDao.getGeneralMasterId("Role", trimmedRole);

		            if (roleId == null || roleId == 0) {
		            	 errorData.add(Map.of("row", rowNum, "error", "Role not found: " + trimmedRole));
		            	 continue;
		            } else {
		                roleIds.add(roleId.longValue());
		            }

		            // Contractor role check (case-insensitive)
		            if (trimmedRole.equalsIgnoreCase("CONTRACTOR") ||
		                trimmedRole.equalsIgnoreCase("CONTRACTOR SUPERVISOR")) {
		                isContractorRole = true;
		            }
		        }

		        // ================================
		        // ✅ 3. SAP VALIDATION
		        // ================================
		        if (isContractorRole && (SAPVendorCode == null || SAPVendorCode.isBlank())) {
		        	 errorData.add(Map.of("row", rowNum,
			                  "error", "SAPVendorCode is mandatory for Contractor roles"));
		        	 continue;
		        }
		        
		     Map<String, Long> orgDefMap = fileUploadDao.getAllOrgLevelDefIds();

		     // Resolve required IDs once
		     Long plantDefId = findDefId(orgDefMap, "principal");
		     Long deptDefId = findDefId(orgDefMap, "dept");
		     Long areaDefId = findDefId(orgDefMap, "area");
		     Long contractorDefId = findDefId(orgDefMap, "contractor");
		    
		  // ✅ 4. plant VALIDATION
		        Long plantId = fileUploadDao.getOrgLevelEntryId(plantCode,plantDefId);
             if (plantId == null || plantId == 0) {
             	 errorData.add(Map.of("row", rowNum,
		                    "error", "Plant not found: " + plantCode));
		            continue;
             }
             
		     // ✅ 5. department VALIDATION
		        Long departmentId = fileUploadDao.getOrgLevelEntryId(department,deptDefId);
                if (departmentId == null || departmentId == 0) {
                	 errorData.add(Map.of("row", rowNum,
 		                    "error", "Department not found: " + department));
 		            continue;
                }

                // ✅ 6. area VALIDATION
              
                Long  areaId = fileUploadDao.getOrgLevelEntryId(area,areaDefId);
                    if (areaId == null || areaId == 0) {
                    	 errorData.add(Map.of("row", rowNum,
     		                    "error", "Area not found: " + area));
     		            continue;
                    }
                 
		        
                 // ✅ 7. EMAIL FORMAT VALIDATION
                    
		        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

		        if (email == null || email.isBlank() || !email.matches(emailRegex)) {
		            errorData.add(Map.of("row", rowNum,
		                    "error", "Email should be in standard format (e.g., name@gmail.com)"));
		            continue;
		        }
		        
		        if (!fieldErrors.isEmpty()) {
		            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
		            continue;
		        }
		        // ✅ 8. SAVE DATA
		      
		        try {
		    	  UserImport user = new UserImport();

		    	    user.setFirstName(firstName);
		    	    user.setLastName(lastName);
		    	    user.setUserAccount(userAccount);
		    	    user.setPassword(password);
		    	    user.setEmail(email);
		    	    user.setMobileNumber(mobileNumber);

		    	    if (userId == null) {
		    	        fileUploadDao.saveuserImport(user, roleIds);
		    	    } else {
		    	        user.setUserId(userId);
		    	        fileUploadDao.updateuserImport(user, userId, roleIds);
		    	    }
		    	    
		    	    // Collect all org entries
				    // List<Long> orgEntryIds = List.of(plantId, departmentId, areaId,contractorId);

				     List<Long> orgEntryIds = new ArrayList<>();

				  // Always required
				  orgEntryIds.add(plantId);
				  orgEntryIds.add(departmentId);
				  orgEntryIds.add(areaId);

				  // OPTIONAL → SAP Vendor Code
				  if (SAPVendorCode != null && !SAPVendorCode.trim().isEmpty()) {

				      Long contractorId = fileUploadDao.getOrgLevelEntryId(SAPVendorCode.trim(),contractorDefId);

				      if (contractorId != null) {
				          orgEntryIds.add(contractorId);
				      } else {
				          // OPTIONAL: log only (no error)
				    	  throw new IllegalArgumentException("SAP Vendor Code not found '" + SAPVendorCode + "'");
				      }
				  }
				  
				 Long orgSetId=fileUploadDao.getOrgAccountSetIdFromSet(userAccount);
                 if(orgSetId == null || orgSetId == 0) {
				  // Insert mapping
				     orgSetId = fileUploadDao.insertUserOrgAccountSet(user.getUserAccount());
                 }
                 
                 List<Long> existingIds = fileUploadDao.getExistingOrgMappings(orgSetId);

                 Set<Long> existingSet = new HashSet<>(existingIds);

                 // Find missing IDs
                 List<Long> missingIds = orgEntryIds.stream()
                         .filter(id -> !existingSet.contains(id))
                         .toList();

                 // Insert only missing
                 if (!missingIds.isEmpty()) {
                     fileUploadDao.insertUserOrgMapping(missingIds, orgSetId);
                 }
				  
		            Map<String, Object> success = new LinkedHashMap<>();
		            success.put("firstName", firstName);
		            success.put("lastName", lastName);
		            success.put("userAccount", userAccount);
		            success.put("password", password);
		            success.put("email", email);
		            success.put("mobileNumber", mobileNumber);
		            success.put("plantCode", plantCode);
		            success.put("organisation", organisation);
		            success.put("department", department);
		            success.put("area", area);
		            success.put("role", role);
		            success.put("SAPVendorCode", SAPVendorCode);

		            successData.add(success);

		        } catch (Exception e) {
		            errorData.add(Map.of("row", rowNum,
		                    "error", "Exception while processing row: " + e.getMessage()));
		        }
		    }

		    Map<String, Object> result = new HashMap<>();
		    result.put("successData", successData);
		    result.put("errorData", errorData);

		    return result;
		}  
	 private Long findDefId(Map<String, Long> map, String keyword) {

		    return map.entrySet().stream()
		            .filter(e -> e.getKey().contains(keyword.toLowerCase()))
		            .map(Map.Entry::getValue)
		            .findFirst()
		            .orElse(null);
		}

private Map<String, Object> processIntraPlantTransfer(BufferedReader reader, String userAccount, String createdBy) throws IOException {

    List<Map<String, Object>> successData = new ArrayList<>();
    List<Map<String, Object>> errorData = new ArrayList<>();

    String line;
    int rowNum = 0;

    String[] fieldNames = {"gatepassNumber", "unitCode", "contractorCode", "department","area","eicNumber","workorder","wcesic","llNumber","esic","effectiveFromDate"};
    Set<String> mandatoryFields = Set.of("gatepassNumber", "unitCode", "contractorCode", "department","area","eicNumber","workorder","wcesic","effectiveFromDate");

    while ((line = reader.readLine()) != null) {
        rowNum++;
        line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");
        if (line.trim().isEmpty()) continue;

        String[] rawFields = line.split(",", -1);
        String[] fields = new String[rawFields.length];
        for (int i = 0; i < rawFields.length; i++) {
            fields[i] = rawFields[i].trim().replaceAll("\"", "");
        }

        Map<String, String> fieldErrors = new LinkedHashMap<>();

        // ================= Field Count Check =================
        if (fields.length < 10) {
            errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
            continue;
        }

        String gatepassNumber = fields[0];
        String unitCode = fields[1];
        String contractorCode = fields[2];
        String department = fields[3];
        String area = fields[4];
        String eicNumber = fields[5];
        String workorderNumber = fields[6];
        String wcesicNumber = fields[7];
        String llNumber = fields[8];
        String esicNumber = fields[9];
        String effectiveFromDate = fields[10];
      

        // ================= 1️ Mandatory Fields Check =================
        for (int i = 0; i < fieldNames.length; i++) {
            if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
                fieldErrors.put(fieldNames[i], "is mandatory");
            }
        }

        if (!fieldErrors.isEmpty()) {
            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
            continue;
        }

        // ================= 2️ Gatepass Validation =================
        boolean gatepassExists = fileUploadDao.activeGatepassExists(gatepassNumber);
        if (!gatepassExists) {
            fieldErrors.put("gatepassNumber", "Gatepass Number is not Active");
            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
            continue;
        }
        
        String gpDOJ =null;
        String gpUnitId = null;
        String gpContId = null;
        String gpDeptId = null;
        // ================= Fetch Gatepass Details =================
        if(gatepassExists) {
        List<String> gpm = fileUploadDao.getContractorDetailsForIntraPlantTransfer(gatepassNumber);
        if (gpm == null) {
            fieldErrors.put("gatepassNumber", "Gatepass Details not Found");
            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
            continue;
        }

         gpUnitId = gpm.get(0);
         gpContId = gpm.get(1);
         gpDeptId = gpm.get(2);
         gpDOJ = gpm.get(3);
        // =================  Logged-in User Access Check =================
        List<PersonOrgLevel> orgLevel = commonService.getPersonOrgLevelDetails(userAccount);
        Map<String, List<PersonOrgLevel>> groupedByLevelDef =
                orgLevel.stream().collect(Collectors.groupingBy(PersonOrgLevel::getLevelDef));

        Set<String> userPEIds = groupedByLevelDef.getOrDefault("Principal Employer", List.of())
                .stream().map(PersonOrgLevel::getId).collect(Collectors.toSet());

        Set<String> userDeptIds = groupedByLevelDef.getOrDefault("Dept", List.of())
                .stream().map(PersonOrgLevel::getId).collect(Collectors.toSet());

        Set<String> userContIds = groupedByLevelDef.getOrDefault("Contractor", List.of())
                .stream().map(PersonOrgLevel::getId).collect(Collectors.toSet());

        if (!userPEIds.contains(gpUnitId) && !userDeptIds.contains(gpDeptId) && !userContIds.contains(gpContId)) {
            fieldErrors.put("access", "Logged-in user does not have access to Do Intra Plant Transfer");
            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
            continue;
        }
       } 

        //check plant exists
        Integer unitId = fileUploadDao.getUnitIdByName(unitCode);
        if (unitId == null || unitId == 0) {
            errorData.add(Map.of("row", rowNum, "error", "PlantCode: " + unitCode + " Not exists"));
            continue;
        }
        
        Integer departmentId = fileUploadDao.getGeneralMasterId("Department", department);
        Integer  subDepartmentId = fileUploadDao.getGeneralMasterId("Area", area);
        
        if (departmentId == null || departmentId == 0) {
            errorData.add(Map.of("row", rowNum, "error", "Department: " + department + " Not exists"));
            continue;
        }
        if (subDepartmentId == null || subDepartmentId == 0) {
            errorData.add(Map.of("row", rowNum, "error", "Area: " + area + " Not exists"));
            continue;
        }
        
        boolean mappingExists = deptMapDao.trioexistsMapping(unitId, departmentId,subDepartmentId);
        if (!mappingExists) {
            errorData.add(Map.of("row", rowNum, "error", "Mapping not exists for '" +department+"' and '"+area+"' with "+unitCode));
            continue;
        }
        Integer contractorId = fileUploadDao.getContractorIdByName(contractorCode);
        
        if (contractorId == null || contractorId == 0) {
            errorData.add(Map.of("row", rowNum, "error", "Contractor: " + contractorCode + " Not exists"));
            continue;
        }
        
        Map<String, Object> woData = fileUploadDao.workorderExistsForPlantAndContractor(workorderNumber,unitId);

        if (woData == null) {
            errorData.add(Map.of("row", rowNum, "error",  "Workorder Number not Mapped to Plant"));
            continue;
        }

        Integer woid = ((Number) woData.get("WORKORDERID")).intValue();
        Date validDt = (Date) woData.get("VALIDDT");
        
        if (validDt == null || !validDt.after(new Date())) {
            fieldErrors.put("workorderNumber", "Workorder Expiry Date must be in future");
        }
        
        // ================= 4️ WC / LL Mapping Check =================
        Map<String, Object> WCESIClicenseData =fileUploadDao.licenseExistsWithWorkorder(workorderNumber, wcesicNumber);

        //  FIRST: null check
        if (WCESIClicenseData == null) {
        	errorData.add(Map.of("row", rowNum,"error", "Invalid WC/ESIC Number or not mapped to Workorder"));
            continue;
        }

        //  Safe extraction AFTER null check
        Integer wcesicid = WCESIClicenseData.get("WCID") != null? ((Number) WCESIClicenseData.get("WCID")).intValue(): null;

        Date wcesicvalidFrom = (Date) WCESIClicenseData.get("VALIDFROM");
        Date wcesicvalidTo = (Date) WCESIClicenseData.get("VALIDTO");
        String licenseType = (String) WCESIClicenseData.get("LICENSETYPE");

        //  Validation checks
        if (wcesicid == null || wcesicid == 0) {
            fieldErrors.put("wcesicNumber", "WC/ESIC Number not mapped to Workorder");
        }

        if (wcesicvalidTo == null || !wcesicvalidTo.after(new Date())) {
            fieldErrors.put("wcesicNumber", "WC/ESIC is expired");
        }

        // FIX: use .equals() instead of ==
        if ("ESIC".equalsIgnoreCase(licenseType)) {
            if (esicNumber == null || esicNumber.isBlank()) {
                errorData.add(Map.of("row", rowNum,"error", "If WC/ESIC Type is ESIC then ESIC Number is mandatory"));
                continue;
            }
        }
        Integer llid = null;
        Date llvalidFrom =null;
        Date llvalidTo =null;
      if(llNumber!=null && ! llNumber.isBlank()) {
        Map<String, Object> licenseData =fileUploadDao.licenseExistsWithWorkorder(workorderNumber, llNumber);

        //  FIRST: null check
        if (licenseData == null) {
        	errorData.add(Map.of("row", rowNum,"error", "Invalid LL Number or not mapped to Workorder"));
            continue;
        }

        //  Safe extraction AFTER null check
         llid = licenseData.get("WCID") != null? ((Number) licenseData.get("WCID")).intValue(): null;
         
          llvalidFrom = (Date) licenseData.get("VALIDFROM");
         llvalidTo = (Date) licenseData.get("VALIDTO");
        String llType = (String) licenseData.get("LICENSETYPE");

        //  Validation checks
        if (llid == null || llid == 0) {
            fieldErrors.put("llNumber", "LL Number not mapped to Workorder");
        }

        if (llvalidTo == null || !llvalidTo.after(new Date())) {
            fieldErrors.put("llNumber", "LL Number is expired");
        }
  }      
//	        Date effectiveFrom = parseDateStrict(fields[9], "EffectiveFrom", fieldErrors);
//
//	        
//	        // Convert to LocalDate for easy comparison
//	        LocalDate valideffectiveFrom = new java.sql.Date(effectiveFrom.getTime()).toLocalDate();
//
//	        LocalDate today = LocalDate.now();
//	        LocalDate maxDate = today.plusDays(7);
//
//	        //  Validate range (today to +7 days)
//	        if (valideffectiveFrom.isBefore(today) || valideffectiveFrom.isAfter(maxDate)) {
//	            fieldErrors.put("EffectiveFrom","Effective From date must be between today and next 7 days");
//	        }
      Date effectiveFrom = parseDateStrict(fields[10], "EffectiveFrom", fieldErrors);

      //if (effectiveFrom != null) {
      LocalDate effectiveDate = new java.sql.Date(effectiveFrom.getTime()).toLocalDate();
    	  LocalDate today = LocalDate.now();
    	  LocalDate minDate = today.minusDays(7);
    	  LocalDate maxDate = today.plusDays(7);

    	  if (effectiveDate.isBefore(minDate) || effectiveDate.isAfter(maxDate)) {

    	      fieldErrors.put("EffectiveFrom","Effective From date must be between past and future 7 days");
    	  }

          // 2. Check WC/ESIC license validity range
          if (wcesicvalidFrom != null && wcesicvalidTo != null) {

              LocalDate wcFrom = new java.sql.Date(wcesicvalidFrom.getTime()).toLocalDate();

              LocalDate wcTo = new java.sql.Date(wcesicvalidTo.getTime()).toLocalDate();

              if (effectiveDate.isBefore(wcFrom)|| effectiveDate.isAfter(wcTo)) {

                  fieldErrors.put("EffectiveFrom","Effective From date must be between WC/ESIC license valid from and valid to dates");
              }
          }

          // 3. Check LL license validity range (only if LL exists)
          if (llNumber != null && !llNumber.isBlank() && llvalidFrom != null && llvalidTo != null) {

              LocalDate llFrom = new java.sql.Date(llvalidFrom.getTime()).toLocalDate();

              LocalDate llTo = new java.sql.Date(llvalidTo.getTime()).toLocalDate();

              if (effectiveDate.isBefore(llFrom)|| effectiveDate.isAfter(llTo)) {

                  fieldErrors.put("EffectiveFrom","Effective From date must be between LL license valid from and valid to dates");
              }
          }
          
       // ================= DOJ Validation =================
          if (gpDOJ != null && !gpDOJ.isBlank()) {

              try {
                  // Convert DOJ String to LocalDate
                  LocalDate dojDate = LocalDate.parse(gpDOJ);
                  // Effective From must be >= DOJ
                  if (effectiveDate.isBefore(dojDate)) {
                      fieldErrors.put("EffectiveFrom","Effective From date must be greater than or equal to Date Of Joining");
                  }

              } catch (DateTimeParseException e) {
                  fieldErrors.put("DOJ","Invalid DOJ date format");
              }
          }
          String lastEffectiveValidFrom = fileUploadDao.getLastEffectiveFromDateFromJobHist(gatepassNumber,effectiveFrom);
          if(lastEffectiveValidFrom != null) {
        	  fieldErrors.put("EffectiveFrom","Effective from must be greater than last effective date: " + lastEffectiveValidFrom);
          }
          
          Integer eicId = fileUploadDao.geteicId(department,unitId,eicNumber);
          if (eicId == null || eicId == 0) {
              errorData.add(Map.of("row", rowNum, "error", "EIC Number: " + eicNumber + " Not exists with Department"));
              continue;
          }
      //}     
        if (!fieldErrors.isEmpty()) {
            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
            continue;
        }

        // ================= Build GatePass Object =================
        GatePassMain gm = new GatePassMain();
        gm.setGatePassId(gatepassNumber);
        gm.setUnitId(String.valueOf(unitId));
        gm.setContractor(String.valueOf(contractorId));
        gm.setDepartment(String.valueOf(departmentId));
        gm.setSubdepartment(String.valueOf(subDepartmentId));
        gm.setEic(String.valueOf(eicId));
        gm.setWorkorder(String.valueOf(woid));
        gm.setWcEsicNo(String.valueOf(wcesicid));
        gm.setLlNo(llid != null ? String.valueOf(llid) : "");
        gm.setEsicNumber(esicNumber!=null?esicNumber:"");
        gm.setWcEsicId(String.valueOf(wcesicNumber));
        gm.setLlId(String.valueOf(llNumber));
        gm.setEffectiveFromDate(effectiveFromDate);
        gm.setCreatedBy(createdBy);
        gm.setReasoning("Intra Plant Transfer");
        String dot = this.getDOT(gm, String.valueOf(unitId));

        // ================= 6️ License Validation =================
       Map<String, String> licenseErrors = this.workmenCountCheck(gm, String.valueOf(unitId), String.valueOf(contractorId));

        if (!licenseErrors.isEmpty()) {
            for (Map.Entry<String, String> entry : licenseErrors.entrySet()) {
                fieldErrors.put(entry.getKey(), "GatePass " + gatepassNumber + " : " + entry.getValue());
            }
            errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
            continue;
        }

        // ================= 7️ Update Gatepass =================
        
        try {
        	 boolean samePlant = String.valueOf(unitId).equals(gpUnitId);

        	 boolean sameContractor = String.valueOf(contractorId).equals(gpContId);
        	    
        	if(samePlant && !sameContractor) {
        		GatePassMain gatepassOld =fileUploadDao.getAllDeatilsOfWorkmenBasedOnGatePass(gatepassNumber);

        		if (gatepassOld != null) {

        		    gm.setAadhaarNumber(gatepassOld.getAadhaarNumber());
        		    gm.setFirstName(gatepassOld.getFirstName());
        		    gm.setLastName(gatepassOld.getLastName());
        		    gm.setDateOfBirth(gatepassOld.getDateOfBirth());
        		    gm.setGender(gatepassOld.getGender());
        		    gm.setRelationName(gatepassOld.getRelationName());
        		    gm.setMobileNumber(gatepassOld.getMobileNumber());
        		    gm.setUanNumber(gatepassOld.getUanNumber());
        		    gm.setPfNumber(gatepassOld.getPfNumber());
        		    gm.setAccountNumber(gatepassOld.getAccountNumber());
        		    gm.setIfscCode(gatepassOld.getIfscCode());
        		    gm.setDoj(gatepassOld.getDoj());
        		    gm.setBloodGroup(gatepassOld.getBloodGroup());
        		    gm.setHazardousArea(gatepassOld.getHazardousArea());
        		    gm.setGender(gatepassOld.getGender());
        		    gm.setAcademic(gatepassOld.getAcademic());
        		    gm.setAccommodation(gatepassOld.getAccommodation());
        		    gm.setEmergencyName(gatepassOld.getEmergencyName());
        		    gm.setEmergencyNumber(gatepassOld.getEmergencyNumber());
        		    gm.setAccessArea(gatepassOld.getAccessArea());
        		    gm.setIdMark(gatepassOld.getIdMark());
        		    gm.setTrade(gatepassOld.getTrade());
        		    gm.setSkill(gatepassOld.getSkill());
        		    gm.setPoliceVerificationDate(gatepassOld.getPoliceVerificationDate());
        		    gm.setHealthCheckDate(gatepassOld.getHealthCheckDate());
        		    gm.setMaritalStatus(gatepassOld.getMaritalStatus());
        		    gm.setNatureOfJob(gatepassOld.getNatureOfJob());
        		    gm.setTechnical(gatepassOld.getTechnical());
        		    gm.setComments(gatepassOld.getComments());
        		    gm.setAddress(gatepassOld.getAddress());
        		    gm.setWorkmenType(gatepassOld.getWorkmenType());
        		    gm.setDisability(gatepassOld.getDisability());
        		    gm.setTransactionId(gatepassOld.getTransactionId());
                    gm.setWageCategory(gatepassOld.getWageCategory());
                    gm.setBonusPayout(gatepassOld.getBonusPayout());
                    gm.setZone(gatepassOld.getZone());
                    gm.setBasic(gatepassOld.getBasic());
                    gm.setDa(gatepassOld.getDa());
                    gm.setHra(gatepassOld.getHra());
                    gm.setWashingAllowance(gatepassOld.getWashingAllowance());
                    gm.setOtherAllowance(gatepassOld.getOtherAllowance());
                    gm.setUniformAllowance(gatepassOld.getUniformAllowance());
                    gm.setPfCap(gatepassOld.getPfCap());
                    gm.setWorkFlowType(1);
                    gm.setPfApplicable(gatepassOld.getPfApplicable());
                    gm.setGatePassType(gatepassOld.getGatePassType());
                    gm.setOnboardingType(gatepassOld.getOnboardingType());

        		}
        		
              fileUploadDao.insertSamePlantDiffContIntraPlantTransfer(gm, createdBy, dot);
        	} else if (!samePlant && sameContractor) {
                fileUploadDao.insertIntraPlantTransferTemp(gm,createdBy,dot);
            }else {

                errorData.add(Map.of("row", rowNum,"gatepassNumber", gatepassNumber,"error", "Go with Bulk Renew Process"));
                                 continue;
            }

            // ✅ Success
            Map<String, Object> map = new HashMap<>();
            map.put("gatepassNumber",gatepassNumber );
            map.put("unitCode", unitCode);
            map.put("contractorCode", contractorCode);
            map.put("department", department);
            map.put("area", area);
            map.put("eicNumber", eicNumber);
            map.put("workorderNumber", workorderNumber);
            map.put("wcesicNumber", wcesicNumber);
            map.put("llNumber", llNumber);
            map.put("esicNumber", esicNumber);
            map.put("effectiveFrom", toSqlDateString(effectiveFrom));

            successData.add(map);

        } catch (Exception ex) {

            log.error("Rollback happened for GatePassId: {}", gatepassNumber, ex);

            //  Add rollback error message
            errorData.add(Map.of("row", rowNum,"gatepassNumber", gatepassNumber,"error", "Failed Intra Plant Transfer for GatePassId: " + gatepassNumber));

            continue; // move to next row
        }
    }

    Map<String, Object> result = new HashMap<>();
    result.put("successData", successData);
    result.put("errorData", errorData);
    return result;
}
private Map<String, Object> processSafetyTraining(BufferedReader reader,String userAccount,String createdBy) throws IOException {
  	 List<Map<String, Object>> successData = new ArrayList<>();
       List<Map<String, Object>> errorData = new ArrayList<>();

       String line;
       int rowNum = 0;
       String[] fieldNames = {"plantCode", "department","trainingType","trainingName"};
              
       Set<String> mandatoryFields = Set.of("plantCode", "department","trainingType","trainingName");

           while ((line = reader.readLine()) != null) {
               rowNum++;
               line = line.replaceAll("[\\x00-\\x1F\\x7F]", "");

               if (line.trim().isEmpty()) continue;

               String[] rawFields = line.split(",", -1);
               String[] fields = new String[rawFields.length];
               for (int i = 0; i < rawFields.length; i++) {
                   fields[i] = rawFields[i].trim().replaceAll("\"", "");
               }

               Map<String, String> fieldErrors = new LinkedHashMap<>();

               if (fields.length < 4) {
                   errorData.add(Map.of("row", rowNum, "error", "Insufficient number of fields"));
                   continue;
               }

               // Check mandatory fields
               for (int i = 0; i < fieldNames.length; i++) {
                   if (mandatoryFields.contains(fieldNames[i]) && fields[i].isBlank()) {
                       fieldErrors.put(fieldNames[i], "is mandatory");
                   }
               }
              
               if (!fieldErrors.isEmpty()) {
                   errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                   continue;
               }
                   String plantCode = fields[0];
                   String department = fields[1];
                   String trainingType = fields[2];
                   String trainingName = fields[3];
                   try {
                   // Get unitId from plant code
                   Integer unitId = fileUploadDao.getUnitIdByName(plantCode);
                   if (unitId == null || unitId == 0) {
                       fieldErrors.put("PlantCode" ,"Plant Code "+ plantCode + " not found");
                   }

                   // Check  department exists 
                   Integer departmentId = fileUploadDao.getGeneralMasterId("Department", department);
                   if (departmentId == null || departmentId == 0) {
                   	//departmentId = fileUploadDao.insertGeneralMaster("Department", department);
                	   fieldErrors.put("Department" ,"Department "+ department  + " not found");
                   }

                   // Check plant department mapping exists
                   if (unitId != null && unitId != 0 && departmentId != null && departmentId != 0) {
                    boolean mappingExists = deptMapDao.plantDepartmentMappingExists(unitId, departmentId);
                    if (!mappingExists) {
                       fieldErrors.put("PlantCode,Department" ,"Mapping not exists for "+ department  + " with " + plantCode);
                    }
                   } 
                   //check training details mapping exists
                   if (unitId != null && unitId != 0 && departmentId != null && departmentId != 0) {
                    boolean trainingDetailsExists = fileUploadDao.checkTrainingDetailsExists(unitId, department,trainingType,trainingName);
                    if (trainingDetailsExists) {
                	   //errorData.add(Map.of( "row", rowNum, "error", "Training details already exists for PlantCode '" + plantCode + "', Department '" + department + "', TrainingType '" + trainingType + "', TrainingName '" + trainingName + "'" )); 
                       fieldErrors.put("Duplicates" ,"Training details already exists for PlantCode "+ plantCode + ", Department " + department + ", TrainingType " + trainingType + ", TrainingName " + trainingName);
                    }
                   }
                   if (!fieldErrors.isEmpty()) {
                       errorData.add(Map.of("row", rowNum, "fieldErrors", fieldErrors));
                       continue;
                   }
                	   
                   fileUploadDao.saveTrainingDetails(unitId, department,trainingType,trainingName);
                   
                   // ✅ Success
                   Map<String, Object> map = new HashMap<>();
                   map.put("plantCode",plantCode );
                   map.put("department", department);
                   map.put("trainingType", trainingType);
                   map.put("trainingName", trainingName);

                   successData.add(map);
                   
               }catch(Exception e) {
            	   errorData.add(Map.of( "row", rowNum, "error", "Error processing row: " + e.getMessage() ));
               }
           }
           Map<String, Object> result = new HashMap<>();
           result.put("successData", successData);
           result.put("errorData", errorData);
           return result;
  }
}