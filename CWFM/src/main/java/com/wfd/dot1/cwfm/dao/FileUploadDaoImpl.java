package com.wfd.dot1.cwfm.dao;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.wfd.dot1.cwfm.controller.CreateEmpFetchByGatePassAPICALL;
import com.wfd.dot1.cwfm.dto.CMSPerson;
import com.wfd.dot1.cwfm.dto.MinimumWageDTO;
import com.wfd.dot1.cwfm.dto.PersonStatusIds;
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
import com.wfd.dot1.cwfm.pojo.CMSWorkorderLN;
import com.wfd.dot1.cwfm.pojo.CmsContractorWC;
import com.wfd.dot1.cwfm.pojo.CmsGeneralMaster;
import com.wfd.dot1.cwfm.pojo.Contractor;
import com.wfd.dot1.cwfm.pojo.ContractorWorkorderTYP;
import com.wfd.dot1.cwfm.pojo.DeptMapping;
import com.wfd.dot1.cwfm.pojo.GatePassMain;
import com.wfd.dot1.cwfm.pojo.KTCWorkorderStaging;
import com.wfd.dot1.cwfm.pojo.MimumWageMasterTemplate;
import com.wfd.dot1.cwfm.pojo.PrincipalEmployer;
import com.wfd.dot1.cwfm.pojo.UserImport;
import com.wfd.dot1.cwfm.pojo.WorkmenBulkUpload;
import com.wfd.dot1.cwfm.pojo.Workorder;
import com.wfd.dot1.cwfm.util.QueryFileWatcher;

@Repository
public class FileUploadDaoImpl implements FileUploadDao {
	private static final Logger log = LoggerFactory.getLogger(FileUploadDaoImpl.class);
	
	@Autowired
	WorkmenDao workmenDao;
	
	@Autowired
    private BCryptPasswordEncoder passwordEncoder;
	
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	@Autowired
	CreateEmpFetchByGatePassAPICALL api;
	
	public String getWFDIntegration() {
		return QueryFileWatcher.getQuery("WFD_INTEGRATION");
	}
	
	 public String saveGeneralMasterTemplate() {
		    return QueryFileWatcher.getQuery("SAVE_GENERAL_MASTER_TEMPLATE");
		}
	 public String isGmNameGmDescriptionExists() {
		    return QueryFileWatcher.getQuery("GMNAME_GMDESCRIPION_EXISTS");
		}
	 public String savePrincipalEmployer() {
		    return QueryFileWatcher.getQuery("SAVE_PRINCIPALEMPLOYER_TEMPLATE");
		}
	 public String saveContractorTemplate() {
		    return QueryFileWatcher.getQuery("SAVE_CONTRACTOR_TEMPLATE");
		}
	 public String getUnitIdByPlantCodeAndOrg() {
		    return QueryFileWatcher.getQuery("GET_UNITID_BY_PLANTCODE_ORG");
		}
	 public String savePemmForContTemplate() {
		    return QueryFileWatcher.getQuery("SAVE_PEMM_FOR_CONT_TEMPLATE");
		}
	 public String saveWCForContTemplate() {
		    return QueryFileWatcher.getQuery("SAVE_WC_FOR_CONT_TEMPLATE");
		}
	 public String saveCMSSUBCONTForContTemplate() {
		    return QueryFileWatcher.getQuery("SAVE_CMSSUBCONT_FOR_CONT_TEMPLATE");
		}
	 public String getContractorIdbyUnitId() {
		    return QueryFileWatcher.getQuery("GET_CONTRACTORID_BY_UNITID");
		}
	 public String isPrincipalEmployerCodeExists() {
		    return QueryFileWatcher.getQuery("IS_PRINCIPALEMPLOYER_EXISTS");
		}
	 public String isContractorCodeExists() {
		    return QueryFileWatcher.getQuery("IS_CONTRACTORCODE_EXISTS");
		}
	 public String getStateIdByName() {
		    return QueryFileWatcher.getQuery("GET_STATEID_BY_NAME");
		}
	 public String savePEState() {
		    return QueryFileWatcher.getQuery("SAVE_PE_STATE");
		}
	 public String saveWorkorderToStaging() {
		    return QueryFileWatcher.getQuery("SAVE_WORKORDER_TO_STAGGING");
		}
	 public String getTradeIdByName() {
		    return QueryFileWatcher.getQuery("GET_TRADEID_BY_NAME");
		}
	 public String getGeneralMasterId() {
		    return QueryFileWatcher.getQuery("GET_GENERALMASTER_ID");
		}
	 public String getWCECId() {
		    return QueryFileWatcher.getQuery("GET_WCECID");
		}
	 public String getUnitIdByName() {
		    return QueryFileWatcher.getQuery("GET_UNITID_BY_NAME");
		}
	 public String getContractorIdByName() {
		    return QueryFileWatcher.getQuery("GET_CONTRACTORID_BY_NAME");
		}
	 public String getSkillIdByName() {
		    return QueryFileWatcher.getQuery("GET_SKILLID_BY_NAME");
		}
	 public String getLlNumber() {
		    return QueryFileWatcher.getQuery("GET_LLNUMBER");
		}
	 public String geteicId() {
		    return QueryFileWatcher.getQuery("GET_EICID");
		}
	 public String getWorkorderId() {
		    return QueryFileWatcher.getQuery("GET_WORKORDER_ID");
		}
	 public String saveWorkmenBulkDraftUploadToStaging() {
		    return QueryFileWatcher.getQuery("SAVE_WORKMEN_BULK_DRAFT_UPLOAD_STAGGING");
		}
	 public String saveWorkmenBulkUploadToStaging() {
		    return QueryFileWatcher.getQuery("SAVE_WORKMEN_BULK_UPLOAD_STAGGING");
		}
	 public String getTransactionIdOfDraft() {
		    return QueryFileWatcher.getQuery("GET_TRANSACTIONID_OF_WORKMENDRAFT");
		}
	 public String saveToGatePassMain() {
		    return QueryFileWatcher.getQuery("SAVE_WORKMEN_DRAFT_BULK_IN_GATEPASS");
		}
	 public String updateRecordStatusByTransactionId() {
		    return QueryFileWatcher.getQuery("UPDATE_RECORDSTATUS_BY_TRANSACTIONID");
		}
	 public String isAadharNumberExistsInWorkmenDraft() {
		    return QueryFileWatcher.getQuery("IS_AADAHAR_EXISTS_IN_WORKMEN_DRAFT");
		}
	 public String isAadharNumberExistsInGatepass() {
		    return QueryFileWatcher.getQuery("IS_AADAHAR_EXISTS_IN_GATEPASS");
		}
	 public String saveFileUploadGeneralMaster() {
		    return QueryFileWatcher.getQuery("SAVE_FILEUPLOAD_GENERALMASTER");
		}
	 public String saveFileUploadWorkorder() {
		    return QueryFileWatcher.getQuery("SAVE_FILEUPLOAD_WORKORDER");
		}
	 public String saveFileUploadWorkorderLN() {
		    return QueryFileWatcher.getQuery("SAVE_FILEUPLOAD_WORKORDERLN");
		}
	 public String saveFileUploadWorkorderTyp() {
		    return QueryFileWatcher.getQuery("SAVE_FILEUPLOAD_WORKORDERTYP");
		}
	 public String getWorkorderIdBySapNumber() {
		    return QueryFileWatcher.getQuery("GET_WORKORDER_BY_SAP_NUMBER");
		}
	 public String getdepartmentIdByUnitId() {
		    return QueryFileWatcher.getQuery("GET_DEPARTMENTID_BY_UNITID");
		}
	 public String getAreaByDeptID() {
		    return QueryFileWatcher.getQuery("GET_AREA_BY_DEPARTMENTID");
		}
	 public String getTradeIdByUnitId() {
		    return QueryFileWatcher.getQuery("GET_TRADEID_BY_UNITID");
		}
	 public String getSkillIdByTradeId() {
		    return QueryFileWatcher.getQuery("GET_SKILLID_BY_TRADEID");
		}
	 public String getGeneralMastersId() {
		    return QueryFileWatcher.getQuery("GET_GENERALMASTERID");
		}
	 public String getGMTypeId() {
		    return QueryFileWatcher.getQuery("GET_GMTYPEID");
		}
	 public String insertGeneralMaster() {
		    return QueryFileWatcher.getQuery("INSERT_GENERALMASTER");
		}
	 public String getGMID() {
		    return QueryFileWatcher.getQuery("GET_GMID");
		}
	 public String existsUnitTradeSkillMapping() {
		    return QueryFileWatcher.getQuery("GET_EXISTS_UNIT_TRADE_SKILL_MAPPING");
		}
	 public String insertUnitTradeSkillMapping() {
		    return QueryFileWatcher.getQuery("INSERT_FILEUPLOAD_TRADE_SKILL_MAPPING");
		}
	 public String insertUnitDepartmentSubDepartmentMapping() {
		    return QueryFileWatcher.getQuery("INSERT_FILEUPLOAD_UNIT_AREA_MAPPING");
		}
	 public String getOrgLevelDefId() {
		    return QueryFileWatcher.getQuery("GET_ORGLEVEL_DEFID");
		}
	 public String SavePEOrglevelEntry() {
		    return QueryFileWatcher.getQuery("SAVE_PE_ORGLEVELENTRY");
		}
	 public String SaveContOrglevelEntry() {
		    return QueryFileWatcher.getQuery("SAVE_CONT_ORGLEVELENTRY");
		}
	 public String SaveWorkorderOrglevelEntry() {
		    return QueryFileWatcher.getQuery("SAVE_WORKORDER_ORGLEVELENTRY");
		}
	 public String saveDeptOrgLevelEntry() {
		    return QueryFileWatcher.getQuery("SAVE_DEPARTMENT_ORGLEVELENTRY");
		}
	 public String saveAreaOrgLevelEntry() {
		    return QueryFileWatcher.getQuery("SAVE_AREA_ORGLEVELENTRY");
		}
	 public String existsInOrgLevelEntry() {
		    return QueryFileWatcher.getQuery("IS_ORGLEVEL_EXISTS_IN_ORGLEVELENTRY");
		}
    @Override
    public void saveData(String[] data) {
    	 String rowData = String.join(",", data); // Convert array to CSV format
         String sql = "INSERT INTO CsvData (rowData) VALUES (?)";
         jdbcTemplate.update(sql, rowData);
		
        System.out.println("Saving data to the database: " + String.join(",", data));
    }
    
    @Override
    public void saveGeneralMaster(CmsGeneralMaster gm) {
        
    	String sql=saveFileUploadGeneralMaster();
         //String sql = "insert into CMSGENERALMASTER(GMNAME,GMDESCRIPTION,GMTYPEID,UPDATEDBY) values (?,?,?,'Admin')";
        jdbcTemplate.update(sql, gm.getGmName(), gm.getGmDescription(), gm.getGmTypeId());
    }

    @Override
	public boolean isGmNameGmDescriptionExists(String gmName, String gmDescription) {
    	String sql=isGmNameGmDescriptionExists();
		// String sql = "select count (*) from CMSGENERALMASTER where GMNAME=? and GMDESCRIPTION =?";
		    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, gmName,gmDescription);
		    return count != null && count > 0;
	}
    
    @Override
    public Long insertIntoWageTable(MinimumWageDTO dto) {
        // Manually generate next wage ID
        Long nextWageId = jdbcTemplate.queryForObject("SELECT ISNULL(MAX(WAGEID), 0) + 1 FROM CMSWAGE", Long.class);

        String sql = "INSERT INTO CMSWAGE (WAGEID, BASIC, DA, ALLOWANCE) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql, nextWageId, dto.getBasic(), dto.getDa(), dto.getOtherAllowance());

        return nextWageId;
    }

    @Override
    public void insertIntoMinimumWageTable(LocalDate fromDate, Long wageId) {
        String sql = "INSERT INTO CMSMINIMUMWAGE (WAGEID, FRMDTM) VALUES (?, ?)";
        jdbcTemplate.update(sql, wageId, Date.valueOf(fromDate));
    }
    
    @Override
    public Long savePrincipalEmployer(PrincipalEmployer p,String createdBy) {
    	 KeyHolder keyHolder = new GeneratedKeyHolder();
    	 String sql=savePrincipalEmployer();
        //String sql = "INSERT INTO CMSPRINCIPALEMPLOYER (ORGANIZATION,CODE,NAME,ADDRESS,MANAGERNAME,MANAGERADDRS,BUSINESSTYPE,MAXWORKMEN,MAXCNTRWORKMEN,BOCWAPPLICABILITY,ISMWAPPLICABILITY,LICENSENUMBER,PFCODE,WCNUMBER,FACTORYLICENCENUMBER) VALUES (?,?, ?, ?, ?,?,?, ?, ?, ?,?,?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        ps.setString(1, p.getOrganization());
	        ps.setString(2, p.getCode());
	        ps.setString(3, p.getName());
	        ps.setString(4, p.getAddress());
	        ps.setString(5, p.getManagerName());
	        ps.setString(6, p.getManagerAddrs());
	        ps.setString(7, p.getBusinessType());
	        ps.setInt(8, p.getMaxWorkmen());
	        ps.setInt(9, p.getMaxCntrWorkmen());
	        ps.setInt(10, p.getBocwApplicability());
	        ps.setLong(11, p.getIsMwApplicability());
	        ps.setString(12,p.getLicenseNumber());
	        ps.setString(13, p.getPfCode());
	        ps.setString(14, p.getWcNumber());
	        ps.setString(15,p.getFactoryLicenseNumber());
	        ps.setString(16, createdBy); 
	        ps.setString(17, p.getStateNM());
	        return ps;
	    }, keyHolder);

	    return keyHolder.getKey().longValue();  // This is your auto-generated unitId
	}

	@Override
	public void saveMinimumWage(MimumWageMasterTemplate mw) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Long saveContractor(Contractor contractor) {
	    KeyHolder keyHolder = new GeneratedKeyHolder();
	    String sql=saveContractorTemplate();
	    //String sql = "INSERT INTO CMSCONTRACTOR(name, ADDRESS, city,ISBLOCKED, CODE) VALUES (?, ?, ?,0, ?)";

	    jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	        ps.setString(1, contractor.getContractorName());
	        ps.setString(2, contractor.getContractorAddress());
	        ps.setString(3, contractor.getCity());
	        //ps.setString(4, contractor.getReference());
	        //ps.setLong(5, contractor.getMobileNumber());
	        ps.setString(4, contractor.getContractorCode());
	        return ps;
	    }, keyHolder);

	    return keyHolder.getKey().longValue();  // This is your auto-generated contractorId
	}

	@Override
	public Long getUnitIdByPlantCodeAndOrg(String plantCode, String organization) {
		String sql=getUnitIdByPlantCodeAndOrg();
	   // String sql = "select unitid from CMSPRINCIPALEMPLOYER where code = ? and ORGANIZATION =? ";
	    try {
	        return jdbcTemplate.queryForObject(sql, new Object[]{plantCode, organization}, Long.class);
	    } catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}
	
	@Override
	public void savePemm(CMSContrPemm pemm) {
		String sql=savePemmForContTemplate();
//	    String sql = "INSERT INTO CMSCONTRPEMM (CONTRACTORID, UNITID, MANAGERNM, LICENSENUM, VALIDFROMDT, VALIDTODT, COVERAGE, TOTALSTRENGTH, MAXNOEMP, NATUREOFWORK, PFNUM, PFAPPLYDT, ESIWC, ESIVALIDFROM, ESIVALIDTO) " +
//	                 "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

	    jdbcTemplate.update(connection -> {
	        PreparedStatement ps = connection.prepareStatement(sql);
	        ps.setLong(1, pemm.getContractorId());
	        ps.setLong(2, pemm.getUnitId());
	        ps.setString(3, pemm.getManagerNm());
	        ps.setString(4, pemm.getLicenseNumber());
	        if (pemm.getLicenseValidFrom() != null) {
	            ps.setTimestamp(5,
	                new java.sql.Timestamp(pemm.getLicenseValidFrom().getTime()));
	        } else {
	            ps.setNull(5, Types.TIMESTAMP);
	        }
	        if (pemm.getLicenseValidTo() != null) {
	            ps.setTimestamp(6,
	                new java.sql.Timestamp(pemm.getLicenseValidTo().getTime()));
	        } else {
	            ps.setNull(6, Types.TIMESTAMP);
	        }
	        ps.setString(7, pemm.getCoverage());
	        ps.setInt(8, pemm.getTotalStrength());
	        ps.setInt(9, pemm.getMaxNoEmp());
	        ps.setString(10, pemm.getNatureofWork());
	        ps.setString(11, pemm.getPfNum());
	        if (pemm.getPfApplyDt() != null) {
	            ps.setTimestamp(12,
	                new java.sql.Timestamp(pemm.getPfApplyDt().getTime()));
	        } else {
	            ps.setNull(12, Types.TIMESTAMP);
	        }
	        ps.setString(13, pemm.getEsiwc());
	        if (pemm.getEsiValidFrom() != null) {
	            ps.setTimestamp(14,
	                new java.sql.Timestamp(pemm.getEsiValidFrom().getTime()));
	        } else {
	            ps.setNull(14, Types.TIMESTAMP);
	        }
	        if (pemm.getEsiValidTo() != null) {
	            ps.setTimestamp(15,
	                new java.sql.Timestamp(pemm.getEsiValidTo().getTime()));
	        } else {
	            ps.setNull(15, Types.TIMESTAMP);
	        }
	        return ps;
	    });

	   // explicitly returning 0 as unitId since it's hardcoded in the insert
	}

	@Override
	public void savewc(CmsContractorWC wc) {
		String sql=saveWCForContTemplate();
	    //String sql = "INSERT INTO CMSCONTRACTOR_WC(CONTRACTORID, UNITID, WC_CODE, WC_FROM_DTM, WC_TO_DTM, WC_TOTAL, LICENCE_TYPE) VALUES (?, ?, ?, ?, ?, ?,'wc')";
	    jdbcTemplate.update(sql,
	        wc.getContractorId(),
	        wc.getUnitId(),
	        wc.getWcCode(),
	        wc.getWcFromDtm(),
	        wc.getWcToDtm(),
	        wc.getWcTotal(),
	        wc.getLicenceType() // if this should be NULL, pass `null` here
	    );
	}

    @Override
    public void savecsc(CMSSubContractor csc) {
    	String sql=saveCMSSUBCONTForContTemplate();
        //String sql = "insert into CMSSUBCONTRACTOR(ID,CONTRACTOR_ID,SUB_CONTRACTOR_ID,WORKORDER_NO,UNITID)values( (SELECT COALESCE(MAX(ID), 0) + 1 FROM CMSSUBCONTRACTOR),?,?,?,?)";
        jdbcTemplate.update(sql,csc.getContractorId(),csc.getSubContractId(),csc.getWorkOrderNumber(),csc.getUnitId());
    }

    @Override
	public Long getContractorIdbyUnitId(Long unitId ) {
    	String sql=getContractorIdbyUnitId();
	    //String sql = "select unitid from CMSPRINCIPALEMPLOYER where code = ? and ORGANIZATION =? ";
	    try {
	        return jdbcTemplate.queryForObject(sql, new Object[]{unitId}, Long.class);
	    } catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}

	@Override
	public Long saveWorkorder(Workorder workorder) {
		 KeyHolder keyHolder = new GeneratedKeyHolder();
		 String sql=saveFileUploadWorkorder();
		    //String sql = "insert into CMSWORKORDER(UNITID,CONTRACTORID,name,VALIDFROM,VALIDDT,TYPEID,DEPID,SECID,GLCODE,COSTCENTER,STATUS,RELEASED_DATE,SAP_WORKORDER_NUM)values(2,2,'nam',?,?,1,1,1,?,?,1,?,?)";

		    jdbcTemplate.update(connection -> {
		        PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		        ps.setString(1, workorder.getValidFrom());
		        ps.setString(2, workorder.getValidTo());
		        ps.setString(3, workorder.getGlCode());
		        ps.setString(4, workorder.getCostCenter());
		        ps.setString(5, workorder.getReleasedDate());
		        ps.setString(6, workorder.getSapWorkorderNumber());
		       
		        return ps;
		    }, keyHolder);

		    return keyHolder.getKey().longValue();  // This is your auto-generated contractorId
		
	}

	@Override
	public void saveWorkorderLN(CMSWorkorderLN woln) {
		 String sql=saveFileUploadWorkorderLN();
		// String sql = "insert into CMSWORKORDERLN(WORKORDERID,ITEM_NUM,DELIVERY_COMPLETED_SW,CHANGED_ON,JOB,RATE,QTY,PM_ORDER_NUM,WBS_ELEMENT,QTY_COMPLETED,SE_ENTRY_CREATED_ON,SE_ENTRY_UPDATED_ON)values(?,?,?,?,?,?,?,?,?,?,?,?)";
		 		
		    jdbcTemplate.update(sql,
		    		//woln.getw(),
		    		woln.getWorkorderid(),
		    		woln.getItemNum(),
		    		woln.getDeliveryCompletion(),
		    		woln.getChangedon(),
		    		woln.getJob(),
		    		woln.getRate(),
		    		woln.getQty(),
		    		woln.getPmOrderNum(),
		    		woln.getWbsElement(),
		    		woln.getQtyCompleted(),
		    		woln.getSeCreatedOn(),
		    		woln.getSeUpdatedOn() // if this should be NULL, pass `null` here
		    );
		
	}

	@Override
	public void saveWorkorderTyp(ContractorWorkorderTYP wotyp) {
		String sql=saveFileUploadWorkorderTyp();
		//String sql = "insert into CMSWORKORDERTYP(name,SAP_TYPE,SHORT_DESC)values('nam',?,?)";
	 		
		    jdbcTemplate.update(sql,
		    		//wotyp.getName(),
		    		wotyp.getSapType(),
		    		wotyp.getShortName()
		    		
		    );
	}
	public Long getWorkorderIdBySapNumber(String sapWorkorderNumber) {
		String sql=getWorkorderIdBySapNumber();
	    //String sql = "SELECT workorderid FROM CMSWORKORDER WHERE sapWorkorderNumber = ?";
	    try {
	        return jdbcTemplate.queryForObject(sql, new Object[]{sapWorkorderNumber}, Long.class);
	    } catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}
	
	@Override
    public String getCSVHeaders(String templateType) {
        switch (templateType) {
            case "Data-General Master":
                return "GM Name,GM Description,GM Type\n";

            case "Data-Principal Employer":
                return "Organization,Plant Code,Name,Address,Manager Name,Manager Address,Business Type,Max Workmen,Max Contract Workmen,BOCW Applicability,"
                		+ "Is MW Applicability,License Number,PF Code,ESWC,Factory License Number,State\n";

            case "Data-Contractor":
                return "Work Order Number,Plant Code,Organisation,Main Contractor Code,Contractor Code,Contractor Name,Contractor Address,City,Contractor Manager Name,Total Workmen Strength,Maximum Number of Workmen,Labour License Number,License Valid From,License Valid To,"
                        + "License Coverage,WC Number,WC Valid From,WC Valid To,WC Coverage,ESIC Number,ESIC Valid From,Nature of Work,"
                        + "PF Number,PF Apply Date\n";
            case "Data-Work Order":
            	return "Work Order Number,Item,Line,Line Number,Service Code,Short Text,Delivery Completion,Item Changed On,Vendor Code,Vendor Name,Vendor Address,Blocked Vendor,Work Order Validity From,Work Order Validity To,Work Order Type,Plant Code,Section Code,Department Code,G/L Code,Cost Center,Nature of Job,Rate/Unit,Quantity,Base Unit of Measure,Work Order Released,PM Order No,WBS Element,Qty Completed,Work Order Release Date,Service Entry Created Date,Service Entry Updated Date,Purchase Org Level,Company Code";
            case "Data-Workmen Bulk Upload":
            	return "First Name*,Last Name*,Father's Name or Husband's Name*,Date of Birth*,Trade*,Skill*,Nature of Work*,Hazardous Area*,"
            			+               		"Aadhar/Id Proof Number*,Vendor Code*,Gender*,Date of Joining,Department*,Area,Work Order Number*,PF A/C Number,Marital Status*,"
            			+              		"Technical/Non Technical*,Academic,Blood Group,Accommodation*,Bank Branch Name,Account Number,"
            			+               		"Mobile Number,Emergency Contact Number*,Police Verification Date,Health Chekup Date,Access Levels*,ESIC Number,Unit Code*,Organization Name,"
            			+                		"EIC Number*,EC Number*,UAN Number,Emergency Contact Person*,Is Eligible for PF,SpecializationName,Insurance Type,LL Number,Address*,Zone,IdMark*,Employee Code\n";
            case "Data-Workmen Bulk Upload Draft":
              return      "First Name,Last Name,Father's Name or Husband's Name,Date of Birth,Trade,Skill,Nature of Work,Hazardous Area,"
              		+ "Aadhar/Id Proof Number,Vendor Code,Gender,Date of Joining,Department,Area,Work Order Number,PF A/C Number,Marital Status,"
              		+ "Technical/Non Technical,Academic,Blood Group,Accommodation,Bank Branch Name,Account Number,"
              		+ "Mobile Number,Emergency Contact Number,Police Verification Date,Health Chekup Date,Access Levels,ESIC Number,Unit Code,Organization Name,"
              		+ "EIC Number,EC Number,UAN Number,Emergency Contact Person,Is Eligible for PF,SpecializationName,Insurance Type,LL Number,Address,Zone,IdMark\n";
            case "Data-Trade Skill":
            	return "Plant Code,Trade,Skill,Workmen Count";
            case "Data-Department Area":
            	return "Plant Code,Department,Sub Department";
            case "Data-Bulk Cancel":
            	return "Gatepass Number,Bulk Cancel Reason";
            case "Data-Bulk Renew":
            	return "Gatepass Number,WorkOrder Number,WC/ESIC Number,LL Number";
            case "Data-Minimum Wage":
            	return "Unit Code,State Name,Zone Name,Skill Name,Basic,DA,Other Allowances,From Date";
            case "Data-User":
            	return "First Name,Last Name,Login Id,Password,Email Address,Mobile Number,Plant Code,Organisation,Department,Area,Role,SAP Vendor Code";
            case "Data-Intra Plant Transfer":
            	return "GatepassId,Plant Code,Contractor Code,Department,Area,EIC Number,Workorder,WC/ESIC,LL Number,ESIC,Effective From Date";
            case "Data-Safety Training":
            	return "Plant Code,Department,Training Type,Training Name";
            
            default:
                // fallback/default template
                return "Template is Not Found to Download";
        }
    }
	
	@Override
	public boolean isPrincipalEmployerCodeExists(String code) {
		String sql=isPrincipalEmployerCodeExists();
	   // String sql = "SELECT COUNT(*) FROM CMSPRINCIPALEMPLOYER WHERE code = ?";
	    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, code);
	    return count != null && count > 0;
	}

	@Override
	public boolean isContractorCodeExists(String contractorCode) {
		String sql=isContractorCodeExists();
		 //String sql = "SELECT COUNT(*) FROM CMSCONTRACTOR WHERE code = ?";
		    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, contractorCode);
		    return count != null && count > 0;
	}

	 @Override
	    public Long getStateIdByName(String stateName) {
			String sql=getStateIdByName();
	        //String sql = "select STATEID from CMSSTATE WHERE STATENM = ?";
	        try {
	            return jdbcTemplate.queryForObject(sql, Long.class, stateName);
	        } catch (EmptyResultDataAccessException e) {
	            return null; // State not found
	        }
	    }

	 @Override
	    public void savePEState(Long unitId, Long stateId) {
		 String sql=savePEState();
	        //String sql = "INSERT INTO CMSPESTATE (UNITID,STATEID ) VALUES (?, ?)";
	        jdbcTemplate.update(sql, unitId, stateId);
	    }
	 
	 private Date parseSqlDate(String input) {

		    if (input == null || input.trim().isEmpty()) {
		        return null;
		    }

		    try {
		        input = input.trim();
		        DateTimeFormatter formatter;

		        if (input.matches("\\d{2}\\.\\d{2}\\.\\d{4}")) {
		            formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		        }
		        else if (input.matches("\\d{2}/\\d{2}/\\d{4}")) {
		            formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		        }
		        else if (input.matches("\\d{4}-\\d{2}-\\d{2}")) {
		            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		        }
		        else if (input.matches("\\d{2}-\\d{2}-\\d{4}")) {
		            formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		        }
		        else {
		            return null; // unsupported format
		        }

		        LocalDate localDate = LocalDate.parse(input, formatter);
		        return Date.valueOf(localDate); // ✅ THIS WAS MISSING

		    } catch (Exception e) {
		        return null;
		    }
		}


	 private java.sql.Date parseSqlDate(LocalDate date) {
		    return date == null ? null : java.sql.Date.valueOf(date);
		}

		private BigDecimal parseBigDecimal(String val) {
		    if (val == null || val.trim().isEmpty()) {
		        return null;
		    }
		    return new BigDecimal(val);
		}

//	 @Override
//	 public void saveWorkorderToStaging(KTCWorkorderStaging workorder) {
//		 String sql=saveWorkorderToStaging();
//	     //String sql = "insert into KTC_WORKORDER_STAGING_ON_REQ(WORKORDER_NUM,ITEM_NUM,SVC_LN_ITEM_DEL,SVC_LN_ITEM_NUM,SVC_NUM,SVC_LN_ITEM_NAME,DELV_COMPLETION_SW,ITEM_CHANGED_ON_DATE,\r\n"
//	     //		+ "VENDOR_CODE,VENDOR_NAME,VENDOR_ADDRESS,BLOCKED_PO,WORKORDER_VALID_FROM,WORKORDER_VALID_TO,SAP_WORKORDER_TYPE,UNIT_CODE,SEC_NAME,DEPT_NAME,\r\n"
//	     //		+ "GL_CODE,COST_CENTRE_CODE,JOB_NAME,RATE,QTY,UOM,WORKORDER_RELEASED_SW,PM_WORKORDER_NUM,WBS_ELEMENT,QTY_COMPLETED,WORKORDER_RELEASED_DATE,\r\n"
//	     //		+ "SERVICE_ENTRY_CREATE_DATE,SERVICE_ENTRY_UPDATED_DATE,PURCHASE_ORG_LEVEL,COMPANY_CODE,EIC_NUM,RECORD_CREATED_ON,RECORD_UPDATED_ON,\r\n"
//	     //		+ "RECORD_PROCESSED,RECORD_STATUS,NATURE_OF_JOB)values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,?,?,?,?,?,null,null,null,null,null,null)\r\n";
//		 int result =jdbcTemplate.update(sql,
//	         workorder.getWorkOrderNumber(),
//	         workorder.getItem(),
//	         workorder.getLine(),
//	         workorder.getLineNumber(),
//	         workorder.getServiceCode(),
//	         workorder.getShortText(),
//	         workorder.getDeliveryCompletion(),
//	         parseSqlDate(workorder.getItemChangedON()),
//	         workorder.getVendorCode(),
//	         workorder.getVendorName(),
//	         workorder.getVendorAddress(),
//	         workorder.getBlockedVendor(),
//	         parseSqlDate(workorder.getWorkOrderValiditiyFrom()),
//	         parseSqlDate(workorder.getWorkOrderValiditiyTo()),
//	         workorder.getWorkOrderType(),
//	         workorder.getPlantcode(),
//	         workorder.getSectionCode(),
//	         workorder.getDepartmentCode(),
//	         workorder.getGLCode(),
//	         workorder.getCostCenter(),
//	         workorder.getNatureofJob(),
//	         workorder.getRateUnit(),
//	         workorder.getQuantity(),
//	         workorder.getBaseUnitofMeasure(),
//	         workorder.getWorkOrderReleased(),
//	         workorder.getPMOrderNo(),
//	         workorder.getWBSElement(),
//	         parseSqlDate(workorder.getWorkOrderReleaseDate()),
//	         parseSqlDate(workorder.getServiceEntryCreatedDate()),
//	         parseSqlDate(workorder.getServiceEntryUpdatedDate()),
//	         workorder.getPurchaseOrgLevel(),
//	         workorder.getCompanycode()
//	     );
//		 
//	 }hema code
		
		@Override
		public void saveWorkorderToStaging(KTCWorkorderStaging w) {

		    String sql =saveWorkorderToStaging();
//		        "INSERT INTO KTC_WORKORDER_STAGING_ON_REQ ("
//		      + " WORKORDER_NUM, ITEM_NUM, SVC_LN_ITEM_NUM, SVC_LN_ITEM_DEL, "
//		      + " SVC_NUM, SVC_LN_ITEM_NAME, DELV_COMPLETION_SW, ITEM_CHANGED_ON_DATE, "
//		      + " VENDOR_CODE, VENDOR_NAME, VENDOR_ADDRESS, BLOCKED_PO, "
//		      + " WORKORDER_VALID_FROM, WORKORDER_VALID_TO, SAP_WORKORDER_TYPE, "
//		      + " UNIT_CODE, SEC_NAME, DEPT_NAME, GL_CODE, COST_CENTRE_CODE, "
//		      + " JOB_NAME, RATE, QTY, UOM, WORKORDER_RELEASED_SW, "
//		      + " PM_WORKORDER_NUM, WBS_ELEMENT, QTY_COMPLETED, NATURE_OF_JOB, "
//		      + " WORKORDER_RELEASED_DATE, SERVICE_ENTRY_CREATE_DATE, "
//		      + " SERVICE_ENTRY_UPDATED_DATE, PURCHASE_ORG_LEVEL, COMPANY_CODE, "
//		      + " EIC_NUM, RECORD_CREATED_ON, RECORD_UPDATED_ON, "
//		      + " RECORD_PROCESSED, RECORD_STATUS ) "
//		      + "VALUES ("
//		      + " ?,?,?,?,?,?,?,?,?,?,"
//		      + " ?,?,?,?,?,?,?,?,?,?,"
//		      + " ?,?,?,?,?,?,?,?,?,?,"
//		      + " ?,?,?,?,?,?,?,?,?"
//		      + ")";

		    jdbcTemplate.update(sql,
		        /* 01 */ w.getWorkOrderNumber(),
		        /* 02 */ w.getItem(),
		        /* 03 */ w.getLineNumber(),           // INT → SVC_LN_ITEM_NUM
		        /* 04 */ w.getLine(),                 // NVARCHAR → SVC_LN_ITEM_DEL
		        /* 05 */ w.getServiceCode(),
		        /* 06 */ w.getShortText(),
		        /* 07 */ w.getDeliveryCompletion(),
		        /* 08 */ parseSqlDate(w.getItemChangedON()),

		        /* 09 */ w.getVendorCode(),
		        /* 10 */ w.getVendorName(),
		        /* 11 */ w.getVendorAddress(),
		        /* 12 */ w.getBlockedVendor(),

		        /* 13 */ parseSqlDate(w.getWorkOrderValiditiyFrom()),
		        /* 14 */ parseSqlDate(w.getWorkOrderValiditiyTo()),
		        /* 15 */ w.getWorkOrderType(),
		        /* 16 */ w.getPlantcode(),
		        /* 17 */ w.getSectionCode(),
		        /* 18 */ w.getDepartmentCode(),
		        /* 19 */ w.getGLCode(),
		        /* 20 */ w.getCostCenter(),

		        /* 21 */ w.getNatureofJob(),
		        /* 22 */ parseBigDecimal(w.getRateUnit()),
		        /* 23 */ parseBigDecimal(w.getQuantity()),
		        /* 24 */ w.getBaseUnitofMeasure(),
		        /* 25 */ w.getWorkOrderReleased(),
		        /* 26 */ w.getPMOrderNo(),
		        /* 27 */ w.getWBSElement(),

		        /* 28 */ parseBigDecimal(w.getQtyCompleted()),
		        /* 29 */ w.getNatureofJob(),

		        /* 30 */ parseSqlDate(w.getWorkOrderReleaseDate()),
		        /* 31 */ parseSqlDate(w.getServiceEntryCreatedDate()),
		        /* 32 */ parseSqlDate(w.getServiceEntryUpdatedDate()),

		        /* 33 */ w.getPurchaseOrgLevel(),
		        /* 34 */ w.getCompanycode(),
		        /* 35 */ null,                        // EIC_NUM

		        /* 36 */ new Timestamp(System.currentTimeMillis()),
		        /* 37 */ null,
		        /* 38 */ "N",
		        /* 39 */ "NEW"
		    );
		}



	 @Override
	 public void callWorkorderProcessingSP() {
	     jdbcTemplate.execute("EXEC CMS_PROCESS_WORKORDERS_ON_REQ");
	 }
	 
	 @Override
	 public Integer getTradeIdByName(String name) {
		    if (name == null || name.trim().isEmpty()) return null;
		    String sql=getTradeIdByName();
		   // String sql = "SELECT TRADEID FROM CMSTRADE WHERE NAME = ?";
		    List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, name.trim());
		    return result.isEmpty() ? null : result.get(0);
		}

	 @Override
	 public Integer getGeneralMasterId(String gmName) {
		    if (gmName == null || gmName.trim().isEmpty()) return null;
		    String sql=getGeneralMasterId();
		    //String sql = "SELECT GMID FROM CMSGENERALMASTER WHERE GMNAME = ?";
		    List<Integer> result = jdbcTemplate.query(sql, new Object[]{gmName.trim()},
		        (rs, rowNum) -> rs.getInt("GMID"));
		    return result.isEmpty() ? null : result.get(0);
		}

	 @Override
	 public Integer getWCECId(String ECNumber, Integer unitId, Integer contractorId,String workorderNumber) {
		    if (ECNumber == null || ECNumber.trim().isEmpty()) return null;
		    String sql=getWCECId();
		    //String sql = "SELECT WCID FROM CMSCONTRACTOR_WC WHERE WC_CODE =? and UNITID=? and CONTRACTORID=?  and (LICENCE_TYPE='WC' or LICENCE_TYPE='ESIC')";
		    List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, ECNumber.trim(),unitId,contractorId,workorderNumber);
		    return result.isEmpty() ? null : result.get(0);
		}

		@Override
		public Integer getUnitIdByName(String unitCode) {
		    if (unitCode == null || unitCode.trim().isEmpty()) return null;
		    String sql=getUnitIdByName();
		    //String sql = "SELECT UNITID FROM CMSPRINCIPALEMPLOYER WHERE CODE = ?";
		    List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, unitCode.trim());
		    return result.isEmpty() ? null : result.get(0);
		}

		@Override
		public Integer getContractorIdByName(String vendorCode) {
		    if (vendorCode == null || vendorCode.trim().isEmpty()) return null;
		    String sql=getContractorIdByName();
		    //String sql = "SELECT CONTRACTORID FROM CMSCONTRACTOR WHERE CODE = ?";
		    List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, vendorCode.trim());
		    return result.isEmpty() ? null : result.get(0);
		}

		@Override
		public Integer getSkillIdByName(String skill) {
		    if (skill == null || skill.trim().isEmpty()) return null;
		    String sql=getSkillIdByName();
		    //String sql = "SELECT SKILLID FROM CMSSKILL WHERE SKILLNM = ?";
		    List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, skill.trim());
		    return result.isEmpty() ? null : result.get(0);
		}

		@Override
		public Integer getLlNumber(String LLNumber, Integer unitId, Integer contractorId,String workorderNumber) {
		    if (LLNumber == null || LLNumber.trim().isEmpty()) return null;
		    String sql=getLlNumber();
		    //String sql = "SELECT WCID FROM CMSCONTRACTOR_WC WHERE WC_CODE =? and UNITID=? and CONTRACTORID=?  and LICENCE_TYPE='LL'";
		    List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, LLNumber.trim(),unitId,contractorId,workorderNumber);
		    return result.isEmpty() ? null : result.get(0);
		}
		
		@Override
		public Integer geteicId(String department, Integer unitId, String ECnumber) {
		    if (department == null || department.trim().isEmpty() ||
		        unitId == null || ECnumber == null || ECnumber.trim().isEmpty()) return null;
		    String sql=geteicId();
			/*
			 * String sql = "SELECT DISTINCT mu.UserId FROM ORGLEVELENTRY ole " +
			 * "JOIN ORGLEVELDEF old ON old.ORGLEVELDEFID = ole.ORGLEVELDEFID " +
			 * "JOIN OLACCTSETMM oasm ON oasm.ORGLEVELENTRYID = ole.ORGLEVELENTRYID " +
			 * "JOIN ORGACCTSET oas ON oas.ORGACCTSETID = oasm.ORGACCTSETID " +
			 * "JOIN MASTERUSER mu ON mu.userAccount = oas.SHORTNM " +
			 * "JOIN UserRoleMapping urm ON urm.UserId = mu.UserId " +
			 * "JOIN CMSGENERALMASTER cgm ON cgm.GMID = urm.RoleId " +
			 * "LEFT JOIN CMSPRINCIPALEMPLOYER cpe ON cpe.CODE = ole.NAME AND old.NAME LIKE 'Principal%' "
			 * + "WHERE cgm.GMNAME IN ('EIC') " +
			 * "AND ((old.NAME LIKE 'Dep%' AND ole.NAME = ?) OR (cpe.UNITID = ?)) " +
			 * "AND mu.userAccount = ?";
			 */
		    List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class,
		            department.trim(), unitId, ECnumber.trim());
		    return result.isEmpty() ? null : result.get(0);
		}

		@Override
		public Integer getWorkorderId(String workorderNumber,Integer unitId, Integer contractorId) {
		    if (workorderNumber == null || workorderNumber.trim().isEmpty()) return null;
		    String sql=getWorkorderId();
		    //String sql = "SELECT WORKORDERID FROM CMSWORKORDER WHERE NAME = ? and UNITID=? and CONTRACTORID=?";
		    List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, workorderNumber.trim(),unitId,contractorId);
		    return result.isEmpty() ? null : result.get(0);
		}
		
	@Override
	public int saveWorkmenBulkDraftUploadToStaging(WorkmenBulkUpload staging) {
		String sql=saveWorkmenBulkDraftUploadToStaging();
		/*
		 * String sql =
		 * "insert into CMSRequestItemDraftBulkUpload(TransactionID,GatePassStatus,AadharNumber,FirstName,LastName,DOB,Gender,RelativeName,IdMark,MobileNumber,\r\n"
		 * +
		 * "MaritalStatus,UnitId,ContractorId,WorkorderId,TradeId,SkillId,DepartmentId,AreaId,EicId,NatureOfJob,WcEsicNo,HazardousArea,AccessAreaId,\r\n"
		 * +
		 * "UanNumber,HealthCheckDate,BloodGroupId,Accommodation,AcademicId,Technical,IfscCode,AccountNumber,EmergencyContactNumber,EmergencyContactName,\r\n"
		 * +
		 * "WorkmenWageCategoryId,PfCap,AadharDocName,PoliceVerificationDocName,UpdatedDate,Address,DOJ,pfnumber,esicNumber,policeverificationDate,specialization,\r\n"
		 * +
		 * "LLNumber,pfapplicable,RecordProcessed,RecordStatus,organizationname,insurencetype,gatepasstypeid,Gatepassid,zoneid)values((SELECT ISNULL(MAX(TransactionID),0)+1 FROM CMSRequestItemDraftBulkUpload),1,?,?,?,?,?,?,?,?,?,?,\r\n"
		 * +
		 * "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,'Yes',null,null,getdate(),?,?,?,?,?,?,?,?,'N',null,?,?,1,null,?)\r\n";
		 */
				 jdbcTemplate.update(sql,
						 staging.getAadhaarNumber(),
						 staging.getFirstName(),
						 staging.getLastName(),
						 parseSqlDate(staging.getDateOfBirth()),
						 staging.getGender(),
						 staging.getRelationName(),
						 staging.getIdMark(),
						 staging.getMobileNumber(),
						 staging.getMaritalStatus(),
						 staging.getUnitCode(),
						 staging.getVendorCode(),
						 staging.getWorkorderNumber(),
						 staging.getTrade(),
						 staging.getSkill(),
						 staging.getDepartment(),
						 staging.getArea(),
						 staging.getEICNumber(),
						 staging.getNatureOfWork(),
						 staging.getECnumber(),
						 staging.getHazardousArea(),
						 staging.getAccessArea(),
						 staging.getUanNumber(),
						 parseSqlDate(staging.getHealthCheckDate()),
						 staging.getBloodGroup(),
						 staging.getAccommodation(),
						 staging.getAcademic(),
						 staging.getTechnical(),
						 staging.getBankName(),
						 staging.getAccountNumber(),
						 staging.getEmergencyNumber(),
						 staging.getEmergencyName(),
						 staging.getAddress(),
						 parseSqlDate(staging.getDoj()),
						 staging.getPfNumber(),
						 staging.getEsicNumber(),
						 parseSqlDate(staging.getPoliceVerificationDate()),
						 staging.getSpecializationName(),
						 staging.getLLnumber(),
						 staging.getPfApplicable(),
						 staging.getOrganizationName(),
						 staging.getInsuranceType(),
						 staging.getZone());
				 return jdbcTemplate.queryForObject("SELECT MAX(TransactionID) FROM CMSRequestItemDraftBulkUpload", Integer.class);
		 
	}

	@Override
	public void saveWorkmenBulkUploadToStaging(WorkmenBulkUpload staging) {
		String sql=saveWorkmenBulkUploadToStaging();
		//String sql = "insert into CMSRequestItemBulkUpload(TransactionID,GatePassStatus,AadharNumber,FirstName,LastName,DOB,Gender,RelativeName,IdMark,MobileNumber,\r\n"
		//		+ "MaritalStatus,UnitId,ContractorId,WorkorderId,TradeId,SkillId,DepartmentId,AreaId,EicId,NatureOfJob,WcEsicNo,HazardousArea,AccessAreaId,\r\n"
		//		+ "UanNumber,HealthCheckDate,BloodGroupId,Accommodation,AcademicId,Technical,IfscCode,AccountNumber,EmergencyContactNumber,EmergencyContactName,\r\n"
		//		+ "WorkmenWageCategoryId,PfCap,AadharDocName,PoliceVerificationDocName,UpdatedDate,Address,DOJ,pfnumber,esicNumber,policeverificationDate,specialization,\r\n"
		//		+ "LLNumber,pfapplicable,RecordProcessed,RecordStatus,organizationname,insurencetype,gatepasstypeid,Gatepassid,zoneid)values((SELECT ISNULL(MAX(TransactionID),0)+1 FROM CMSRequestItemBulkUpload),1,?,?,?,?,?,?,?,?,?,?,\r\n"
		//		+ "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,null,'Yes',null,null,getdate(),?,?,?,?,?,?,?,?,'N',null,?,?,1,null,?)\r\n";
				 jdbcTemplate.update(sql,
						 staging.getAadhaarNumber(),
						 staging.getFirstName(),
						 staging.getLastName(),
						 parseSqlDate( staging.getDateOfBirth()),
						 staging.getGender(),
						 staging.getRelationName(),
						 staging.getIdMark(),
						 staging.getMobileNumber(),
						 staging.getMaritalStatus(),
						 staging.getUnitCode(),
						 staging.getVendorCode(),
						 staging.getWorkorderNumber(),
						 staging.getTrade(),
						 staging.getSkill(),
						 staging.getDepartment(),
						 staging.getArea(),
						 staging.getEICNumber(),
						 staging.getNatureOfWork(),
						 staging.getECnumber(),
						 staging.getHazardousArea(),
						 staging.getAccessArea(),
						 staging.getUanNumber(),
						 parseSqlDate(staging.getHealthCheckDate()),
						 staging.getBloodGroup(),
						 staging.getAccommodation(),
						 staging.getAcademic(),
						 staging.getTechnical(),
						 staging.getBankName(),
						 staging.getAccountNumber(),
						 staging.getEmergencyNumber(),
						 staging.getEmergencyName(),
						 staging.getAddress(),
						 parseSqlDate(staging.getDoj()),
						 staging.getPfNumber(),
						 staging.getEsicNumber(),
						 parseSqlDate(staging.getPoliceVerificationDate()),
						 staging.getSpecializationName(),
						 staging.getLLnumber(),
						 staging.getPfApplicable(),
						 staging.getOrganizationName(),
						 staging.getInsuranceType(),
						 staging.getZone(),
						 staging.getEmployeeCode());
		 
	}

	@Override
	public void callWorkmenBulkUploadDraftProcessingSP() {
		jdbcTemplate.execute("EXEC KTC_ENTRYPASS_BULK_UPLOAD");
	 }

	@Override
	public WorkmenBulkUpload getByTransactionId(int transactionId) {
		String sql=getTransactionIdOfDraft();
	    //String sql = "select cribu.AadharNumber as aadhaarNumber,cribu.FirstName as firstName,cribu.LastName as lastName,cribu.DOB as dateOfBirth,cgmg.GMNAME as gender,\r\n"
	    //		+ "cribu.RelativeName as relationName,cribu.IdMark as idMark,cribu.MobileNumber as mobileNumber,   \r\n"
	    //		+ "cribu.MaritalStatus as maritalStatus,cpe.CODE as unitCode,cmsc.CODE as vendorCode,cmswo.NAME as workorderNumber,   \r\n"
	   // 		+ "cmst.NAME as trade,cmss.SKILLNM as skill,cmsgmdep.GMNAME as department,cmsgma.GMNAME as area,mu.userAccount  AS EICNumber,cribu.NatureOfJob as natureOfWork,   \r\n"
	  //  		+ "ccwc.WC_CODE as ECnumber,cribu.HazardousArea as hazardousArea,cmsgmaa.GMNAME as accessArea,   \r\n"
	  //  		+ "cribu.uanNumber,cribu.healthCheckDate,cmsgmb.GMNAME as bloodGroup,cribu.Accommodation as accommodation,cmsgmac.GMNAME as academic,cribu.Technical as technical,   \r\n"
	 //   		+ "cribu.IfscCode as bankName,cribu.AccountNumber as accountNumber,cribu.EmergencyContactNumber as emergencyNumber,cribu.EmergencyContactName  as emergencyName   \r\n"
	 //   		+ ",cribu.doj,cribu.pfNumber,cribu.esicNumber,cribu.policeVerificationDate,cribu.pfApplicable,cmsgmz.GMNAME as zone,cribu.Address as address from CMSRequestItemDraftBulkUpload cribu   \r\n"
	  //  		+ "left join CMSPRINCIPALEMPLOYER cpe on cpe.unitid=cribu.UnitId left join CMSCONTRACTOR cmsc on cmsc.CONTRACTORID=cribu.ContractorId left join CMSWORKORDER cmswo on cmswo.WORKORDERID=cribu.WorkorderId \r\n"
	  //  		+ "left join CMSTRADE cmst on cmst.TRADEID = cribu.TradeId   left join CMSSKILL cmss on cmss.skillid=cribu.SkillId \r\n"
	  //  		+ "left join CMSGENERALMASTER cmsgmdep on cmsgmdep.GMID=cribu.DepartmentId left join CMSGENERALMASTER cmsgma on cmsgma.GMID=cribu.AreaId \r\n"
	   // 		+ "left join CMSGENERALMASTER cmsgmaa on cmsgmaa.GMID=cribu.AccessAreaId left join CMSGENERALMASTER cmsgmb on cmsgmb.GMID=cribu.BloodGroupId \r\n"
	   // 		+ "left join CMSGENERALMASTER cmsgmac on cmsgmac.GMID=cribu.AcademicId left join CMSGENERALMASTER cmsgmz on cmsgmz.GMID=cribu.zoneid LEFT JOIN CMSGENERALMASTER cgmg ON cgmg.GMID = cribu.gender  \r\n"
	   // 		+ "LEFT JOIN MASTERUSER mu ON mu.UserId = cribu.EicId left join CMSCONTRACTOR_WC ccwc on ccwc.WCID=cribu.WcEsicNo where TransactionID=?";
	    return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(WorkmenBulkUpload.class), transactionId);
	}
	 
	 public String getMaxGatePassIdQuery() {
		 return QueryFileWatcher.getQuery("GET_NEXT_GATEPASSID_SEQ");
	 }
	 
	private String generateGatePassId() {
	    String gatePassId = null;
	    String maxTestReqId = null;	
	    DecimalFormat decimalFormat = new DecimalFormat("00");
	    try {
	    	String query = getMaxGatePassIdQuery();
	        SqlRowSet rs = jdbcTemplate.queryForRowSet(query);
	        if(rs.next()){
				
				maxTestReqId=String.valueOf(rs.getInt(1));
				//log.info("maxTestReqId"+maxTestReqId);
				
			}
	        if(maxTestReqId==null  || maxTestReqId.equals("0")){
				
	        	gatePassId ="GP700001";
			}else{
							
				long incrMaxId = Long.parseLong(maxTestReqId)+1;
				gatePassId = "GP" + decimalFormat.format(incrMaxId);
			}
	    } catch (Exception e) {
	       // log.error("Error generating GatePassId", e);
	    }
	    return gatePassId;
	}
	
	public String getSaveContractWorkmen() {
		 return QueryFileWatcher.getQuery("SAVE_CONTRACT_WORKMEN"); 
	}
	public String getNextTransactionId() {
		String transactionId=null;
		try {
			 transactionId = jdbcTemplate.queryForObject("EXEC GetNextGatepassTransactionId", String.class);


	}catch(Exception e) {
		 System.out.println("Failed to fetch transaction ID: " + e.getMessage());
		e.printStackTrace();
	}
	    return transactionId;
	}

//	@Override
//	public void saveToGatePassMain(WorkmenBulkUpload data) {
//		String gatePassId = this.generateGatePassId();
//		
//		String sql = this.getSaveContractWorkmen();
//	   String transId = this.getNextTransactionId();
//	   
//	   Object[] parameters = this.prepareGatePassDraftParameters(transId, data); 
//
//       try {
//       	String query = this.getSaveContractWorkmen();
//           int result = jdbcTemplate.update(query, parameters);
//           if (result > 0) {
//              // log.info("GatePass drafted successfully for transId: " + transId);
//           } else {
//               //log.warn("Failed to draft GatePass for transId: " + transId);
//           }
//       } catch (Exception e) {
//          // log.error("Error saving GatePass for transId: " + transId, e);
//          // return null;
//       }
//       
//	    }
	
	@Override
	public void saveToGatePassMain(WorkmenBulkUpload data) {
		//String gatePassId = this.generateGatePassId();
		String sql=saveToGatePassMain();
		String transId = this.getNextTransactionId();
	    //String sql = "INSERT INTO  GATEPASSMAIN (TransactionId, GatePassId, GatePassTypeId, GatePassStatus, AadharNumber, FirstName, LastName, DOB, Gender, RelativeName, IdMark, MobileNumber,\r\n"
	    //		+ "MaritalStatus, UnitId, ContractorId, WorkorderId, TradeId, SkillId, DepartmentId, AreaId, EicId, NatureOfJob, WcEsicNo, HazardousArea  \r\n"
	    //		+ ",  AccessAreaId ,  UanNumber,  HealthCheckDate,  BloodGroupId,  Accommodation,  AcademicId ,  Technical ,  IfscCode,  AccountNumber,  EmergencyContactNumber  \r\n"
	    //		+ ",  EmergencyContactName, WorkmenWageCategoryId, BonusPayoutId, ZoneId, Basic, DA, HRA, WashingAllowance, OtherAllowance  \r\n"
	    //		+ ",  UniformAllowance,  PfCap,  AadharDocName ,  PhotoName ,  BankDocName ,  PoliceVerificationDocName,  IdProof2DocName,  MedicalDocName,  EducationDocName,  Form11DocName  \r\n"
	    //		+ ",  TrainingDocName,  OtherDocName,  UpdatedDate,  UpdatedBy,  WorkFlowType,  Comments,  Address,  DOJ,  DOT,  pfnumber,  esicNumber,  policeverificationDate  \r\n"
	    //		+ ",  OnboardingType ,  pfapplicable,LLNo )\r\n"
	    //		+ "VALUES ( ?,?,1,1,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,\r\n"
	    //		+ "?,?,?,?,?,?,?,?,?,?,?,null,null,?,'0.00','0.00','0.00','0.00','0.00','0.00','Yes',null,null,null,null,null,null,null,null,\r\n"
	    //		+ "null,null,getdate(),7,null,null,?,?,null,?,?,?,'regular',?,?)";
	    jdbcTemplate.update(sql, transId,
	    		data.getGatepassid()!=null? data.getGatepassid():" ",
	    		data.getAadhaarNumber()!=null? data.getAadhaarNumber():" ", 
	    		data.getFirstName()!=null? data.getFirstName():" ",
	    		data.getLastName()!=null? data.getLastName():" ",
	    		data.getDateOfBirth()!=null? data.getDateOfBirth():" ",
	    		data.getGender()!=null? data.getGender():" ",
	    		data.getRelationName()!=null? data.getRelationName():" ",
	    		data.getIdMark()!=null? data.getIdMark():" ",
	    		data.getMobileNumber()!=null? data.getMobileNumber():" ",
	            data.getMaritalStatus()!=null? data.getMaritalStatus():" ",
	            data.getUnitCode()!=null && !data.getUnitCode().trim().isEmpty()? data.getUnitCode():" ", 
	            data.getVendorCode()!=null&& !data.getVendorCode().trim().isEmpty()? data.getVendorCode():" ", 
	            data.getWorkorderNumber()!=null&& !data.getWorkorderNumber().trim().isEmpty()? data.getWorkorderNumber():" ",
	            data.getTrade()!=null&& !data.getTrade().trim().isEmpty()? data.getTrade():" ",
	            data.getSkill()!=null&& !data.getSkill().trim().isEmpty()? data.getSkill():" ",
	            data.getDepartment()!=null&& !data.getDepartment().trim().isEmpty()? data.getDepartment():" ",
	            data.getArea()!=null&& !data.getArea().trim().isEmpty()? data.getArea():" ",
	            data.getEICNumber()!=null&& !data.getEICNumber().trim().isEmpty()? data.getEICNumber():" ",
	            data.getNatureOfWork()!=null? data.getNatureOfWork():" ",
	            data.getECnumber()!=null&& !data.getECnumber().trim().isEmpty()? data.getECnumber():" ",
	            data.getHazardousArea()!=null? data.getHazardousArea():" ",
	            data.getAccessArea()!=null&& !data.getAccessArea().trim().isEmpty()? data.getAccessArea():" ",
	            data.getUanNumber()!=null? data.getUanNumber():" ",
	            data.getHealthCheckDate()!=null? data.getHealthCheckDate():" ",
	            data.getBloodGroup()!=null&& !data.getBloodGroup().trim().isEmpty()? data.getBloodGroup():" ",
	            data.getAccommodation()!=null? data.getAccommodation():" ",
	            data.getAcademic()!=null&& !data.getAcademic().trim().isEmpty()? data.getAcademic():" ",
	            data.getTechnical()!=null? data.getTechnical():" ",
	            data.getBankName()!=null? data.getBankName():" ",
	            data.getAccountNumber()!=null? data.getAccountNumber():" ",
	            data.getEmergencyNumber()!=null? data.getEmergencyNumber():" ",
	            data.getEmergencyName()!=null? data.getEmergencyName():" ",
	            data.getZone()!=null? data.getZone():" ",
	            data.getAddress()!=null? data.getAddress():" ",
	            data.getDoj()!=null? data.getDoj():" ",
	            data.getPfNumber()!=null? data.getPfNumber():" ",
	            data.getEsicNumber()!=null? data.getEsicNumber():" ",
	            data.getPoliceVerificationDate()!=null? data.getPoliceVerificationDate():" ",
	            data.getPfApplicable()!=null? data.getPfApplicable():" ",
	            data.getLLnumber()!=null? data.getLLnumber():" ");
	}

	@Override
	public void updateRecordStatusByTransactionId(int txnId, String combinedErrors) {
		String sql=updateRecordStatusByTransactionId();
	   // String sql = "UPDATE CMSRequestItemDraftBulkUpload SET RecordStatus = ? WHERE TransactionID = ?";
	    jdbcTemplate.update(sql, combinedErrors, txnId);
	}
	@Override
	public boolean isAadharNumberExists(String aadharNumber) {
		String sql1=isAadharNumberExistsInWorkmenDraft();
		String sql2=isAadharNumberExistsInGatepass();
		 //String sql1 = "SELECT COUNT(*) FROM CMSRequestItemDraftBulkUpload WHERE AadharNumber = ?";
		   // String sql2 = "SELECT COUNT(*) FROM GatePassMain WHERE AadharNumber = ?";

		    Integer count1 = jdbcTemplate.queryForObject(sql1, new Object[]{aadharNumber}, Integer.class);
		    Integer count2 = jdbcTemplate.queryForObject(sql2, new Object[]{aadharNumber}, Integer.class);

		    return (count1 != null && count1 > 0) || (count2 != null && count2 > 0);
	}
	//@Override
	//public void updateRecordProcessedByTransactionId(Integer txnId) {
	///    String sql = "UPDATE CMSRequestItemBulkUpload SET RecordProcessed = 'Y' WHERE TransactionID = ?";
	//    jdbcTemplate.update(sql, txnId);
	//}
	@Override
	public Integer getdepartmentIdByUnitId(Integer unitId, String department) {
		String sql=getdepartmentIdByUnitId();
		//String sql=" select udm.departmentid from UnitDepartmentMapping udm\r\n"
		//		+ "join CMSGENERALMASTER cgm on  cgm.GMID = udm.departmentId where udm.principalEmployerId=? and cgm.GMNAME=?";
		 List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class,unitId, department.trim());
		    return result.isEmpty() ? null : result.get(0);
		}
	@Override
	public Integer getAreaByDeptID(Integer unitId, Integer departmentId, String area) {
		String sql=getAreaByDeptID();
		//String sql = "select udm.subDepartmentId from UnitDepartmentMapping udm\r\n"
				//+ "join CMSGENERALMASTER cgm on  cgm.GMID = udm.subDepartmentId where udm.principalEmployerId=? and udm.departmentId=? and cgm.GMNAME=?";
	
		List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class,unitId, departmentId,area.trim());
	    return result.isEmpty() ? null : result.get(0);
	}
	@Override
	public Integer getTradeIdByUnitId(Integer unitId, String trade) {
		String sql=getTradeIdByUnitId();
		//String sql="select utm.TradeId from UnitTradeSkillMapping utm\r\n"
				//+ "join CMSGENERALMASTER cgm on  cgm.GMID = utm.TradeId where utm.principalEmployerId=? and cgm.GMNAME=?";
		 List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class,unitId, trade.trim());
		    return result.isEmpty() ? null : result.get(0);
		}
	@Override
	public Integer getSkillIdByTradeId(Integer unitId, Integer tradeId, String skill) {
		String sql=getSkillIdByTradeId();
		//String sql="select utm.SkillId from UnitTradeSkillMapping utm\r\n"
		//		+ "join CMSGENERALMASTER cgm on  cgm.GMID = utm.SkillId where utm.principalEmployerId=? and TradeId=? and cgm.GMNAME=?";
		 List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class,unitId, tradeId,skill.trim());
		    return result.isEmpty() ? null : result.get(0);
		}
	
	    @Override
	    public Integer getGeneralMasterId(String gmType, String gmName) {
	        try {
	        	String sql=getGeneralMastersId();
	            //String sql = "SELECT GM.GMID  FROM CMSGENERALMASTER GM  JOIN CMSGMTYPE GT ON GT.GMTYPEID = GM.GMTYPEID  WHERE GT.GMTYPE = ? AND GM.GMNAME = ? and ISACTIVE=1";
	            return jdbcTemplate.queryForObject(sql, Integer.class, gmType, gmName);
	        } catch (EmptyResultDataAccessException e) {
	            return null;
	        }
	    }

	    @Override
	    public Integer insertGeneralMaster(String gmType, String gmName) {
	        // First get the GMTYPEID
	    	String getTypeIdSql=getGMTypeId();
	    	String insertSql=insertGeneralMaster();
	    	String fetchIdSql=getGMID();
	    	
	       // String getTypeIdSql = "SELECT GMTYPEID FROM CMSGMTYPE WHERE GMTYPE = ?";
	        Integer gmTypeId = jdbcTemplate.queryForObject(getTypeIdSql, Integer.class, gmType);

	        //String insertSql = "INSERT INTO CMSGENERALMASTER ( GMNAME,GMDESCRIPTION,GMTYPEID,UPDATEDBY) VALUES (?, ?, ?,'Admin')";
	        jdbcTemplate.update(insertSql, gmName,gmName,gmTypeId);

	        // Return newly inserted GMID
	       // String fetchIdSql = " SELECT GMID FROM CMSGENERALMASTER \r\n"
	        //		+ "	            WHERE GMNAME = ? AND GMTYPEID = ? and ISACTIVE=1 ORDER BY GMID DESC ";
	        return jdbcTemplate.queryForObject(fetchIdSql, Integer.class, gmName, gmTypeId);
	    }

	    @Override
	    public boolean existsUnitTradeSkillMapping(Integer unitId, Integer tradeId, Integer skillId) {
	    	String sql=existsUnitTradeSkillMapping();
	        //String sql = "SELECT COUNT(*) FROM UnitTradeSkillMapping  WHERE PrincipalEmployerId = ? AND TRADEID = ? AND SKILLID =?";
	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, unitId, tradeId, skillId);
	        return count != null && count > 0;
	    }

	    @Override
	    public void insertUnitTradeSkillMapping(Integer unitId, Integer tradeId, Integer skillId,String workmenCount) {
	    	String sql=insertUnitTradeSkillMapping();
	      //  String sql = "INSERT INTO UnitTradeSkillMapping (PrincipalEmployerId, TRADEID, SKILLID) VALUES (?, ?, ?)";
	        jdbcTemplate.update(sql, unitId, tradeId, skillId,workmenCount);
	    }
	    
	    @Override
	    public void insertUnitDepartmentSubDepartmentMapping(Integer unitId, Integer departmentId, Integer subDepartmentId) {
	    	String sql=insertUnitDepartmentSubDepartmentMapping();
	    	//String sql = "INSERT INTO UnitDepartmentMapping (principalEmployerId, departmentId, subDepartmentId) VALUES (?, ?, ?)";
	        jdbcTemplate.update(sql, unitId, departmentId, subDepartmentId);
	    }
	    @Override
	    public long getOrgLevelDefId(String name) {
	    	String sql=getOrgLevelDefId();
	       // String sql = "SELECT ORGLEVELDEFID FROM ORGLEVELDEF WHERE LOWER(NAME) = LOWER(?)";

	        try {
	            return jdbcTemplate.queryForObject(sql, new Object[]{name}, Long.class);
	        } catch (EmptyResultDataAccessException e) {
	            return 0;   // Not found
	        }
	    }

		@Override
		public boolean SavePEOrglevelEntry(List<PrincipalEmployer> list, long orgLevelDefId) {
			
			String sql=SavePEOrglevelEntry();

//		    String sql = "INSERT INTO ORGLEVELENTRY " +
//		            "(ORGLEVELDEFID, NAME, DESCRIPTION, INACTIVE, UPDATE_DTM, UPDATEDBYUSRACCTID, VERSION) " +
//		            "VALUES (?, ?, ?, 1, GETDATE(), 0, NULL)";

		    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

		        @Override
		        public void setValues(PreparedStatement ps, int i) throws SQLException {
		            PrincipalEmployer pe = list.get(i);

		            ps.setLong(1, orgLevelDefId);
		            ps.setString(2, pe.getCode().trim());     // ✅ normalized
		            ps.setString(3, pe.getName().trim());
		        }

		        @Override
		        public int getBatchSize() {
		            return list.size();
		        }
		    });

		    return true;
		}
		@Override
		public boolean SaveContOrglevelEntry(List<Contractor> list, long orgLevelDefId) {
			String sql=SaveContOrglevelEntry();
		   // String sql = "INSERT INTO ORGLEVELENTRY " +
		    //        "(ORGLEVELDEFID, NAME, DESCRIPTION, INACTIVE, UPDATE_DTM, UPDATEDBYUSRACCTID, VERSION) " +
		    //        "VALUES (?, ?, ?, 1, getdate(), 0, NULL)";

		    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

		        @Override
		        public void setValues(PreparedStatement ps, int i) throws SQLException {
		        	Contractor con = list.get(i);

		            ps.setLong(1, orgLevelDefId);
		            ps.setString(2, con.getContractorCode()); // Using Address as DESCRIPTION
		            ps.setString(3, con.getContractorName());
		           
		        }

		        @Override
		        public int getBatchSize() {
		            return list.size();
		        }
		    });

		    return true;
		}

		@Override
		public boolean SaveWorkorderOrglevelEntry(List<KTCWorkorderStaging> list, long orgLevelDefId) {
			String sql=SaveWorkorderOrglevelEntry();
		   // String sql = "INSERT INTO ORGLEVELENTRY " +
		   //         "(ORGLEVELDEFID, NAME, DESCRIPTION, INACTIVE, UPDATE_DTM, UPDATEDBYUSRACCTID, VERSION) " +
		  //          "VALUES (?, ?, ?, 1, getdate(), 0, NULL)";

		    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

		        @Override
		        public void setValues(PreparedStatement ps, int i) throws SQLException {
		        	KTCWorkorderStaging con = list.get(i);

		            ps.setLong(1, orgLevelDefId);
		            ps.setString(2, con.getWorkOrderNumber());
		            ps.setString(3, con.getWorkOrderType()); // Using Address as DESCRIPTION
		        }

		        @Override
		        public int getBatchSize() {
		            return list.size();
		        }
		    });

		    return true;
		}
		@Override
		public boolean saveDeptOrgLevelEntry(List<DeptMapping> list, long orgLevelDefId) {
			String sql=saveDeptOrgLevelEntry();
		   // String sql = "INSERT INTO ORGLEVELENTRY " +
		    //        "(ORGLEVELDEFID, NAME, DESCRIPTION, INACTIVE, UPDATE_DTM, UPDATEDBYUSRACCTID, VERSION) " +
		    //        "VALUES (?, ?, ?, 1, getdate(), 0, NULL)";

		    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

		        @Override
		        public void setValues(PreparedStatement ps, int i) throws SQLException {
		        	DeptMapping area = list.get(i);

		            ps.setLong(1, orgLevelDefId);
		            ps.setString(2, area.getDepartment());
		            ps.setString(3, area.getDepartment()); 
		        }

		        @Override
		        public int getBatchSize() {
		            return list.size();
		        }
		    });

		    return true;
		}
		@Override
		public boolean saveAreaOrgLevelEntry(List<DeptMapping> list, long orgLevelDefId) {
			String sql=saveAreaOrgLevelEntry();
		   //String sql = "INSERT INTO ORGLEVELENTRY " +
		   //       "(ORGLEVELDEFID, NAME, DESCRIPTION, INACTIVE, UPDATE_DTM, UPDATEDBYUSRACCTID, VERSION) " +
		    //      "VALUES (?, ?, ?, 1, getdate(), 0, NULL)";

		    jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

		        @Override
		        public void setValues(PreparedStatement ps, int i) throws SQLException {
		        	DeptMapping dept = list.get(i);

		            ps.setLong(1, orgLevelDefId);
		            ps.setString(2, dept.getSubDepartment());
		            ps.setString(3, dept.getSubDepartment()); 
		        }

		        @Override
		        public int getBatchSize() {
		            return list.size();
		        }
		    });

		    return true;
		}
		@Override
		public boolean existsInOrgLevelEntry(String name, long orgLevelDefId) {
			String sql=existsInOrgLevelEntry();
		   // String sql = "SELECT COUNT(*) FROM ORGLEVELENTRY WHERE NAME = ? AND ORGLEVELDEFID = ?";
		    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, name, orgLevelDefId);
		    return count != null && count > 0;
		}
		public String getContractorIdByCode() {
			return QueryFileWatcher.getQuery("GET_CONTRACTORID_BY_CODE");
		}
		@Override
		public Long getContractorIdByCode(String subContractorCode) {
			String sql=getContractorIdByCode();
		   // String sql = "select contractorid from CMSCONTRACTOR where CODE= ?";
		    		
		    try {
		        return jdbcTemplate.queryForObject(sql, new Object[]{subContractorCode}, Long.class);
		    } catch (EmptyResultDataAccessException e) {
		        return null;
		    }
		}
		public String hasActiveWorkorder() {
			return QueryFileWatcher.getQuery("HAS_ACTIVE_WORKORDER_FOR_CONTRACTOR");
		}
		public String updateContractor() {
			return QueryFileWatcher.getQuery("UPDATE_CMSCONTRACTOR");
		}
		public String pemmExists() {
			return QueryFileWatcher.getQuery("CHECK_CONTRACTOR_EXISTS_IN_PEMM");
		}
		public String updatePemm() {
			return QueryFileWatcher.getQuery("UPDATE_CONTRACTOR_IN_PEMM");
		}
		public String wcExists() {
			return QueryFileWatcher.getQuery("CHECK_CONTRACTOR_EXISTS_IN_WC");
		}
		@Override
		public boolean hasActiveWorkorder(Long unitId, Long contractorId,String workOrder) {
			String sql=hasActiveWorkorder();
		    //String sql = "SELECT COUNT(1) FROM CMSWORKORDER WHERE UNITID = ? AND CONTRACTORID = ? and SAP_WORKORDER_NUM=? AND VALIDDT > GETDATE()";

		    Integer count = jdbcTemplate.queryForObject(sql,Integer.class,unitId,contractorId,workOrder);
		    return count != null && count > 0;
		}
		@Override
		public void updateContractor(Contractor c) {
			String sql=updateContractor();
			//"UPDATE CMSCONTRACTOR SET NAME=?, ADDRESS=?, CITY=? WHERE CONTRACTORID=?",
		    jdbcTemplate.update(sql,
		        c.getContractorName(),
		        c.getContractorAddress(),
		        c.getCity(),
		        c.getContractorId()
		    );
		}
		@Override
		public boolean pemmExists(Long contractorId, Long unitId) {
			String sql=pemmExists();
		    //String sql = "SELECT COUNT(*) FROM CMSCONTRPEMM WHERE CONTRACTORID=? AND UNITID=?";
		    return jdbcTemplate.queryForObject(sql, Integer.class, contractorId, unitId) > 0;
		}
		@Override
		public void updatePemm(CMSContrPemm p) {
			String sql=updatePemm();
			 //"UPDATE CMSCONTRPEMM SET MANAGERNM=?, LICENSENUM=?, VALIDFROMDT=?, VALIDTODT=?, COVERAGE=?, TOTALSTRENGTH=?, MAXNOEMP=?, NATUREOFWORK=?,  PFNUM=?, PFAPPLYDT=?, ESIWC=?, ESIVALIDFROM=?, ESIVALIDTO=? WHERE CONTRACTORID=? AND UNITID=?",
		    jdbcTemplate.update(sql,
		        p.getManagerNm(),
		        p.getLicenseNumber(),
		        p.getLicenseValidFrom(),
		        p.getLicenseValidTo(),
		        p.getCoverage(),
		        p.getTotalStrength(),
		        p.getMaxNoEmp(),
		        p.getNatureofWork(),
		        p.getPfNum(),
		        p.getPfApplyDt(),
		        p.getEsiwc(),
		        p.getEsiValidFrom(),
		        p.getEsiValidTo(),
		        p.getContractorId(),
		        p.getUnitId()
		    );
		}
		@Override
		public boolean wcExists(Long contractorId, Long unitId,String wcCode,String licenceType) {
			String sql=wcExists();
		    //String sql = "SELECT COUNT(*) FROM CMSCONTRACTOR_WC WHERE CONTRACTORID=? AND UNITID=? and WC_CODE=? AND LICENCE_TYPE = ?";
		    return jdbcTemplate.queryForObject(sql, Integer.class, contractorId, unitId,wcCode,licenceType) > 0;
		}
		public String updatewc() {
			return QueryFileWatcher.getQuery("UPDATE_CONTRACTOR_IN_WC");
		}
		public String subContractorExists() {
			return QueryFileWatcher.getQuery("CHECK_SUBCONTRACTOR_EXISTS");
		}
		public String updatecsc() {
			return QueryFileWatcher.getQuery("UPDATE_SUBCONTRACTOR");
		}
		@Override
		public void updatewc(CmsContractorWC wc) {
			String sql=updatewc();
			 // "UPDATE CMSCONTRACTOR_WC SET  WC_FROM_DTM=?, WC_TO_DTM=?, WC_TOTAL=?,DELETE_SW=0 WHERE CONTRACTORID=? AND UNITID=? AND WC_CODE=? AND LICENCE_TYPE=?",
		    jdbcTemplate.update(sql,
		        wc.getWcFromDtm(),
		        wc.getWcToDtm(),
		        wc.getWcTotal(),
		        wc.getContractorId(),
		        wc.getUnitId(),
		        wc.getWcCode(),
		        wc.getLicenceType()
		        
		    );
		}
		@Override
		public boolean subContractorExists(String contractorCode, Long unitId, String workOrder,String subContractorCode) {
			String sql=subContractorExists();
		   // String sql = "SELECT COUNT(*) FROM CMSSUBCONTRACTOR WHERE CONTRACTOR_ID=? AND UNITID=? AND WORKORDER_NO=? AND SUB_CONTRACTOR_ID=?";
		    return jdbcTemplate.queryForObject(sql, Integer.class, contractorCode, unitId, workOrder,subContractorCode) > 0;
		}
		@Override
		public void updatecsc(CMSSubContractor c) {
			String sql=updatecsc();
			  //"UPDATE CMSSUBCONTRACTOR SET SUB_CONTRACTOR_ID=?,WORKORDER_NO=? ,CONTRACTOR_ID=? WHERE  UNITID=?",
		    jdbcTemplate.update(sql,
		        c.getSubContractId(),
		        c.getWorkOrderNumber(),
		        c.getContractorId(),
		        c.getUnitId()
		    );
		}
		public String saveWorkorderLLWC() {
			return QueryFileWatcher.getQuery("SAVE_WORKORDER_LLWC");
		}
		public String updateWorkorderLLWC() {
			return QueryFileWatcher.getQuery("UPDATE_WORKORDER_LLWC");
		}
		public String llwcExists() {
			return QueryFileWatcher.getQuery("CHECK_LLWC_EXISTS");
		}
		public String isLicenseMappedToOtherContractor() {
			return QueryFileWatcher.getQuery("IS_LICENSE_MAPPED_OTHER_CONTRACTOR");
		}
		@Override
		public void saveWorkorderLLWC(CMSWorkorderLLWC llwc) {
			String sql=saveWorkorderLLWC();
		   // String sql = "INSERT INTO CMSWORKORDER_LLWC(WONUMBER, LICENSE_NUMBER, LICENSE_TYPE)VALUES (?, ?, ?)";
		    jdbcTemplate.update(sql,llwc.getWorkorderNumber(),llwc.getLicenseNumber(),llwc.getLicenseType());
		}

		@Override
		public void updateWorkorderLLWC(CMSWorkorderLLWC llwc) {
			String sql=updateWorkorderLLWC();
		  //  String sql = "UPDATE CMSWORKORDER_LLWC SET LICENSE_NUMBER = ? WHERE WONUMBER = ? AND LICENSE_TYPE = ?";
		    jdbcTemplate.update(sql,llwc.getLicenseNumber(),llwc.getWorkorderNumber(),llwc.getLicenseType());
		}

		@Override
		public boolean llwcExists(String workOrderNumber, String licenseType,String license) {
			String sql=llwcExists();
		   // String sql = "SELECT COUNT(1)FROM CMSWORKORDER_LLWC WHERE WONUMBER = ? AND LICENSE_TYPE = ? and LICENSE_NUMBER=?";
		    Integer count = jdbcTemplate.queryForObject(sql,Integer.class,workOrderNumber,licenseType,license);
		    return count != null && count > 0;
		}

		@Override
		public boolean isLicenseMappedToOtherContractor(Long contractorId,String licenseNumber,String licenseType) {
			String sql=isLicenseMappedToOtherContractor();
		   // String sql = "SELECT COUNT(1)FROM CMSCONTRACTOR_WC WHERE WC_CODE = ? AND LICENCE_TYPE = ? AND CONTRACTORID != ?";
		    Integer count = jdbcTemplate.queryForObject(sql,Integer.class,licenseNumber,licenseType,contractorId);

		    return count != null && count > 0;
		}
		@Override
		public boolean codeExistsInOrgLevelEntry(String contractorCode, long orgLevelDefId) {
			String sql=existsInOrgLevelEntry();
			   // String sql = "SELECT COUNT(*) FROM ORGLEVELENTRY WHERE NAME = ? AND ORGLEVELDEFID = ?";
			    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, contractorCode, orgLevelDefId);
			    return count != null && count > 0;
		}
		@Override
		public boolean codeExistsInOrgLevelEntry(List<Contractor> list, long orgLevelDefId) {

		    if (list == null || list.isEmpty()) {
		        return false;
		    }

		    List<String> contractorCodes = list.stream()
		            .map(Contractor::getContractorCode)
		            .filter(Objects::nonNull)
		            .distinct()
		            .toList();

		    if (contractorCodes.isEmpty()) {
		        return false;
		    }

		    String placeholders = contractorCodes.stream()
		            .map(c -> "?")
		            .collect(Collectors.joining(","));

		    String sql =
		        "SELECT COUNT(*) " +
		        "FROM ORGLEVELENTRY " +
		        "WHERE ORGLEVELDEFID = ? " +
		        "AND NAME IN (" + placeholders + ")";

		    List<Object> params = new ArrayList<>();
		    params.add(orgLevelDefId);
		    params.addAll(contractorCodes);

		    Integer count = jdbcTemplate.queryForObject(
		            sql,
		            params.toArray(),
		            Integer.class
		    );

		    return count != null && count > 0;
		}
		@Override
		public Set<String> getExistingContractorCodes(List<Contractor> list, long orgLevelDefId) {

		    if (list == null || list.isEmpty()) {
		        return Collections.emptySet();
		    }

		    List<String> contractorCodes = list.stream()
		            .map(Contractor::getContractorCode)
		            .filter(Objects::nonNull)
		            .distinct()
		            .toList();

		    if (contractorCodes.isEmpty()) {
		        return Collections.emptySet();
		    }

		    String placeholders = contractorCodes.stream()
		            .map(c -> "?")
		            .collect(Collectors.joining(","));

		    String sql =
		        "SELECT NAME FROM ORGLEVELENTRY " +
		        "WHERE ORGLEVELDEFID = ? " +
		        "AND NAME IN (" + placeholders + ")";

		    List<Object> params = new ArrayList<>();
		    params.add(orgLevelDefId);
		    params.addAll(contractorCodes);

		    List<String> existing = jdbcTemplate.queryForList(
		            sql,
		            params.toArray(),
		            String.class
		    );

		    return new HashSet<>(existing);
		}
		public String workorderExistsInStagging() {
			return QueryFileWatcher.getQuery("WORKORDER_EXISTS_IN_WORKORDER_STAGGING");
		}
		public String updateWorkorderToStaging() {
			return QueryFileWatcher.getQuery("UPDATE_WORKORDER_STAGGING");
		}
		@Override
		public boolean workorderExists(String workOrder, String contractorCode, String plantCode,String item,String lines,String lineNumber) {
			String sql =workorderExistsInStagging();
			//String sql = "select COUNT(*) from KTC_WORKORDER_STAGING_ON_REQ where WORKORDER_NUM=? and VENDOR_CODE=? and UNIT_CODE=? and ITEM_NUM=? and SVC_LN_ITEM_DEL=? and SVC_LN_ITEM_NUM=? ";
		    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, workOrder, contractorCode,plantCode,item,lines,lineNumber);
		    return count != null && count > 0;
		}
		@Override
		public void updateWorkorderToStaging(KTCWorkorderStaging w) {
			String sql =updateWorkorderToStaging();
//			String sql =
//				    "UPDATE KTC_WORKORDER_STAGING_ON_REQ SET " +
//				    " SVC_LN_ITEM_DEL = ?, " +
//				    " SVC_NUM = ?, " +
//				    " SVC_LN_ITEM_NAME = ?, " +
//				    " DELV_COMPLETION_SW = ?, " +
//				    " ITEM_CHANGED_ON_DATE = ?, " +
//				    " VENDOR_NAME = ?, " +
//				    " VENDOR_ADDRESS = ?, " +
//				    " BLOCKED_PO = ?, " +
//				    " WORKORDER_VALID_FROM = ?, " +
//				    " WORKORDER_VALID_TO = ?, " +
//				    " SAP_WORKORDER_TYPE = ?, " +
//				    " SEC_NAME = ?, " +
//				    " DEPT_NAME = ?, " +
//				    " GL_CODE = ?, " +
//				    " COST_CENTRE_CODE = ?, " +
//				    " JOB_NAME = ?, " +
//				    " RATE = ?, " +
//				    " QTY = ?, " +
//				    " UOM = ?, " +
//				    " WORKORDER_RELEASED_SW = ?, " +
//				    " PM_WORKORDER_NUM = ?, " +
//				    " WBS_ELEMENT = ?, " +
//				    " QTY_COMPLETED = ?, " +
//				    " WORKORDER_RELEASED_DATE = ?, " +
//				    " SERVICE_ENTRY_CREATE_DATE = ?, " +
//				    " SERVICE_ENTRY_UPDATED_DATE = ?, " +
//				    " PURCHASE_ORG_LEVEL = ?, " +
//				    " COMPANY_CODE = ?, " +
//				    " RECORD_UPDATED_ON = ?, " +
//				    " RECORD_STATUS = ? " +
//				    "WHERE WORKORDER_NUM = ? " +
//				    "  AND ITEM_NUM = ? " +
//				    "  AND SVC_LN_ITEM_NUM = ? " +
//				    "  AND VENDOR_CODE = ? " +
//				    "  AND UNIT_CODE = ?";

		    jdbcTemplate.update(sql,
		        /* SET values */
		        w.getLine(),
		        w.getServiceCode(),
		        w.getShortText(),
		        w.getDeliveryCompletion(),
		        parseSqlDate(w.getItemChangedON()),

		        w.getVendorName(),
		        w.getVendorAddress(),
		        w.getBlockedVendor(),

		        parseSqlDate(w.getWorkOrderValiditiyFrom()),
		        parseSqlDate(w.getWorkOrderValiditiyTo()),
		        w.getWorkOrderType(),

		        w.getSectionCode(),
		        w.getDepartmentCode(),
		        w.getGLCode(),
		        w.getCostCenter(),

		        w.getNatureofJob(),
		        parseBigDecimal(w.getRateUnit()),
		        parseBigDecimal(w.getQuantity()),
		        w.getBaseUnitofMeasure(),

		        w.getWorkOrderReleased(),
		        w.getPMOrderNo(),
		        w.getWBSElement(),
		        parseBigDecimal(w.getQtyCompleted()),

		        parseSqlDate(w.getWorkOrderReleaseDate()),
		        parseSqlDate(w.getServiceEntryCreatedDate()),
		        parseSqlDate(w.getServiceEntryUpdatedDate()),

		        w.getPurchaseOrgLevel(),
		        w.getCompanycode(),

		        new Timestamp(System.currentTimeMillis()),
		        "UPDATED",

		        /* WHERE values (VERY IMPORTANT) */
		        w.getWorkOrderNumber(),
		        w.getItem(),
		        w.getLineNumber(),
		        w.getVendorCode(),
		        w.getPlantcode()
		    );
		}
		public String gatepassNumberExists() {
			return QueryFileWatcher.getQuery("GATEPASSID_EXISTS");
		}
		public String getContractorDetailsForCancel() {
			return QueryFileWatcher.getQuery("CONTRACTOR_DETAILS_FOR_BULK_CANCEL");
		}
		@Override
		public boolean gatepassNumberExists(String gatepassNumber) {
			//String sql="select count(*) from GATEPASSMAIN where GatePassId=?";
			String sql=gatepassNumberExists();
			  Integer count = jdbcTemplate.queryForObject(sql, Integer.class, gatepassNumber);
			    return count != null && count > 0;
			}
		
		@Override
		public List<String> getContractorDetailsForCancel(String gatepassNumber) {
			String sql =getContractorDetailsForCancel();
			//String sql = "SELECT UNITID, CONTRACTORID, DEPARTMENTID FROM GATEPASSMAIN WHERE GATEPASSID = ?";

		    try {
		        return jdbcTemplate.queryForObject(sql, new Object[]{gatepassNumber}, (rs, rowNum) -> {
		            List<String> list = new ArrayList<>();
		            list.add(rs.getString("UNITID"));
		            list.add(rs.getString("CONTRACTORID"));
		            list.add(rs.getString("DEPARTMENTID"));
		            return list;
		        });
		    } catch (EmptyResultDataAccessException e) {
		        return null;
		    }
		}
		public String updateGatepass() {
			return QueryFileWatcher.getQuery("UPDATE_GATEPASS_BULK_CANCEL");
		}
		public String insertBulkCancelGatepassTransactionMapping() {
			return QueryFileWatcher.getQuery("INSERT_GATEPASS_TRANSACTIONMAPPING_BULK_CANCEL");
		}
		@Override
		public void updateGatepass(BulkCancel bc,String createdBy) {
			String sql =updateGatepass();
		    //String sql = "UPDATE GATEPASSMAIN SET Reasoning = ?, GatePassTypeId = ?, GatePassStatus = ?,UpdatedBy=?,UpdatedDate=GETDATE() WHERE GatePassId = ?";

		    jdbcTemplate.update(sql,bc.getCancelReason(),GatePassType.BULKCANCEL.getStatus() ,GatePassStatus.APPROVED.getStatus(),createdBy,bc.getGatepassNumber());
		    insertBulkCancelGatepassTransactionMapping(bc,createdBy);
		}

		public void insertBulkCancelGatepassTransactionMapping(BulkCancel bc,String createdBy) {
			String transactionId= workmenDao.getNextTransactionId();
			String sql =insertBulkCancelGatepassTransactionMapping();
			//String sql = "INSERT INTO GatePassTransactionMapping (TRANSACTIONID, GATEPASSID, GATEPASSTYPEID, CREATEDDATE) VALUES (?, ?, ?, GETDATE())";
		    jdbcTemplate.update(sql,transactionId, bc.getGatepassNumber(),GatePassType.BULKCANCEL.getStatus() );
		    gatePassActionPersonInsertBulkCancel(bc, createdBy);
		}
		
		public boolean gatePassActionPersonInsertBulkCancel(BulkCancel bc,String createdBy) {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        String today = LocalDate.now().format(formatter);
	       
		    long personId = workmenDao.getPersonIdFromCmsPerson(bc.getGatepassNumber());
		    if (personId <= 0) return false;

		    // Step 1: Close existing CUSTDATA rows
		    if (!logAndCheck("CUSTDATA_UPDATE",
		            this.updateCmsPersonCustDataEffectiveTill(personId)))
		        return false;

		    // Step 2: Insert new CUSTDATA row
		    boolean custInserted = workmenDao.insertIntoCustData(createdBy,personId,GatePassType.BULKCANCEL.getStatus(),bc.getCancelReason(),today);

		    if (!logAndCheck("CUSTDATA_INSERT", custInserted)) {
		        return false;
		    }

		    // Step 3: Update StatusMM only if active
		    if (workmenDao.isPersonActiveInStatusMM(personId)) {
		    	DateTimeFormatter formatte = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		        String bulkCanceltoday = LocalDate.now().format(formatte);
		        bc.setDot(bulkCanceltoday);
		        
		    	 boolean statusUpdated = this.processDotDate(bc.getDot(),bc.getGatepassNumber());

//		        PersonStatusIds ids = workmenDao.getPersonStatusIds(personId);
//
//		        if (ids.getActiveId() != null && ids.getInactiveId() != null) {
//
//		            boolean statusUpdated =
//		                    workmenDao.updatePersonStatusValidity(ids.getActiveId(), ids.getInactiveId());

		            if (!logAndCheck("STATUSMM_UPDATE", statusUpdated))
		                return false;
		        //}
		    }

		    return true;
		}
		private long getPersonIdFromCmsPerson(String gatePassId) {
			// TODO Auto-generated method stub
			return workmenDao.getPersonIdFromCmsPerson(gatePassId);
		}
		private boolean processDotDate(String dot,String gatepassNumber) {

		    if (dot == null || dot.trim().isEmpty()) {
		        return false;
		    }

		    LocalDate dotDate = LocalDate.parse(dot);
		    LocalDate today = LocalDate.now();

		    long personId = getPersonIdFromCmsPerson(gatepassNumber);

		    PersonStatusIds ids = workmenDao.getPersonStatusIds(personId);

		    if (ids == null || ids.getActiveId() == null || ids.getInactiveId() == null) {
		        return false;
		    }

		    if (dotDate.isAfter(today)) {
		        return workmenDao.updatePersonStatusValidity(ids.getActiveId(),ids.getInactiveId());
		    }else {
		    	// no update when dot <= today
		    	return true;
		    }

		}
		public String getCustomDefID() {
			return QueryFileWatcher.getQuery("GET_CUSTOM_DEFID_CMSPERSONCUSTOMDATADEFINITION");
		}
		public String getMaxRefID() {
			return QueryFileWatcher.getQuery("GET_MAX_REFID_CMSPERSONCUSTOMDATA");
		}
		public String updateEffectiveTillToday() {
			return QueryFileWatcher.getQuery("UPDATE_EFFECTIVETILL_TODAY");
		}
		public boolean updateCmsPersonCustDataEffectiveTill(long personId) {

		    // 1. Get CSTMDEFID for Status
			String defSql = getCustomDefID();

		    Integer defId = jdbcTemplate.queryForObject(defSql, Integer.class);

		    if (defId == null) {
		        return false; // No definition → nothing to update
		    }

		    // 2. Get latest REFID
		    String refSql = getMaxRefID();

		    Long refId = jdbcTemplate.queryForObject(refSql, Long.class, defId, personId);

		    if (refId == null || refId == 0) {
		        return false; // No record → nothing to update
		    }

		    // 3. Update EFFECTIVETILL
		    String updateSql = updateEffectiveTillToday();
//		    String updateSql = "UPDATE CMSPERSONCUSTOMDATA "
//		                     + "SET EFFECTIVETILL = CONVERT(date, GETDATE()) "
//		                     + "WHERE REFID = ?";

		    return jdbcTemplate.update(updateSql, refId) > 0;
		}
		
		private boolean logAndCheck(String label, boolean success) {
		    log.info(label + " : " + (success ? "SUCCESS" : "FAILED"));
		    return success;
		}
		public String WorkorderExists() {
			return QueryFileWatcher.getQuery("WORKORDER_EXISTS_IN_CMSWORKORDER");
		}
		public String WCESICExists() {
			return QueryFileWatcher.getQuery("WCESIC_EXISTS_IN_CMSWORKORDERLLWC");
		}
		public String LLExists() {
			return QueryFileWatcher.getQuery("LL_EXISTS_IN_CMSWORKORDERLLWC");
		}
		public String updateGatepassBulkRenew() {
			return QueryFileWatcher.getQuery("UPDATE_BULK_RENEW_IN_GATEPASS");
		}
		public String insertBulkRenewGatepassTransactionMapping() {
			return QueryFileWatcher.getQuery("INSERT_BULK_RENEW_IN_GATEPASSTRNSACTIONMAPPING");
		}
		public String checkWorkorderExists() {
			return QueryFileWatcher.getQuery("CHECK_WORKORDER_EXISTS");
		}
		//String sql = WorkorderExists();
		@Override
		public Map<String, Object> workorderExists(String workorderNumber, String gpContId) {

		    String sql = checkWorkorderExists();
		    		//"SELECT WORKORDERID, VALIDDT FROM CMSWORKORDER WHERE SAP_WORKORDER_NUM=? AND CONTRACTORID=?";

		    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, workorderNumber, gpContId);

		    return list.isEmpty() ? null : list.get(0);
		}
		
		 @Override
		 public Integer WCESICExists(String workorderNumber, String wcNumber){
			 String sql = WCESICExists();
			    //String sequel = "select WOLLID from CMSWORKORDER_LLWC where WONUMBER=? and LICENSE_NUMBER=? and LICENSE_TYPE in ('ESIC','WC')";
			    List<Integer> result = jdbcTemplate.query(sql, new Object[]{workorderNumber,wcNumber},
			        (rs, rowNum) -> rs.getInt("WOLLID"));
			    return result.isEmpty() ? null : result.get(0);
			}
		 @Override
		 public Integer LLExists(String workorderNumber, String llNumber){
			 String sql = LLExists();
			   // String sql = "select WOLLID from CMSWORKORDER_LLWC where WONUMBER=? and LICENSE_NUMBER=? and LICENSE_TYPE ='LL'";
			    List<Integer> result = jdbcTemplate.query(sql, new Object[]{workorderNumber,llNumber},
			        (rs, rowNum) -> rs.getInt("WOLLID"));
			    return result.isEmpty() ? null : result.get(0);
			}
		 
		 @Override
		 @Transactional(rollbackFor = Exception.class)
		 public void updateGatepassBulkRenew(GatePassMain gm, String createdBy, String dot) {
		     try {
		         String sql = updateGatepassBulkRenew();
		         jdbcTemplate.update(sql,gm.getWorkorder(),gm.getWcEsicNo(),gm.getLlNo(),GatePassType.BULKRENEW.getStatus() ,GatePassStatus.APPROVED.getStatus(),dot,createdBy,gm.getGatePassId());
				    boolean result =insertBulkRenewGatepassTransactionMapping(gm,createdBy,dot);
				    if (!result) {
				    	log.error("GatePassTransaction mapping failed for GatePassId: {}", gm.getGatePassId());
			            throw new RuntimeException("GatePassTransaction mapping failed");
			        }
				    try {
			        	String wfdIntegration = this.getWFDIntegration();
			        	if("yes".equalsIgnoreCase(wfdIntegration)) {
			        		api.updateOnBoardingDetails(gm.getTransactionId());
			        	}
			        	}catch(Exception e) {
			        		log.info(e.getMessage());
							 throw new RuntimeException("renew Api Integration Failed");
			        	}
		     } catch (Exception e) {
		         log.error("Error in Bulk Renew Gatepass for GatePassId: {}", gm.getGatePassId(), e);
		         throw new RuntimeException("Bulk Renew Gatepass failed - rollback triggered", e);
		     }
		 }
		 
		 public boolean insertBulkRenewGatepassTransactionMapping(GatePassMain gm,String createdBy,String dot) {
			    String transactionId = workmenDao.getNextTransactionId();
			    String sql = insertBulkRenewGatepassTransactionMapping();
			    gm.setTransactionId(transactionId);
			    int inserted = jdbcTemplate.update(sql,transactionId,gm.getGatePassId(),GatePassType.BULKRENEW.getStatus());
			    if (inserted == 0) {
			        throw new RuntimeException("GatePassTransactionMapping insert failed");
			    }
			    return gatePassActionPersonInsertRenew(gm,GatePassType.BULKRENEW.getStatus(),createdBy,dot);
			}
		 public boolean gatePassActionPersonInsertRenew(GatePassMain gpm,String gatePassType,String createdBy,String dot) {

                  long personId = workmenDao.getPersonIdFromCmsPerson(gpm.getGatePassId());
                 if (personId <= 0) {
                	 log.error("Invalid personId for GatePassId: "+ gpm.getGatePassId());
                       throw new RuntimeException("Invalid personId for GatePassId: " + gpm.getGatePassId());
                   }

                       // STEP 1: JOB HISTORY
                      boolean jobHist = this.updateCmsPersonJobHistBulkRenew(gpm, personId);
                   if (!jobHist) {
                	   log.error("Job history update/insert failed for personId: " + personId);
                          throw new RuntimeException("Job history update/insert failed for personId: " + personId);
                     }

                      // STEP 2: CUST DATA UPDATE
                      boolean custUpdate = workmenDao.updateCmsPersonCustDataRenewEffectiveTill(personId, dot);
                         if (!custUpdate) {
                        	 log.error("CustData effectivetill update failed for personId: " + personId);
                              throw new RuntimeException("CustData effectivetill update failed for personId: " + personId);
                        }

                    // STEP 3: CUST DATA INSERT
                     boolean custInsert = workmenDao.insertIntoCustDataRenew(createdBy, personId, gatePassType);
                       if (!custInsert) {
                    	   log.error("CustData gatepasstype insert failed for personId: " + personId);
                         throw new RuntimeException("CustData gatepasstype insert failed for personId: " + personId);
                         }

                     // STEP 4: STATUS MM UPDATE
                       if (workmenDao.isPersonActiveInStatusMM(personId)) {
                    	   boolean statusUpdated = processRenewStatusMM(dot,gpm);
//                            PersonStatusIds ids = workmenDao.getPersonStatusIds(personId);
//                                 if (ids.getActiveId() != null && ids.getInactiveId() != null) {
//                                        boolean statusUpdated = workmenDao.updatePersonStatusValidityRenew(ids.getActiveId(),ids.getInactiveId(),dot);
                                           if (!statusUpdated) {
                                        	   log.error("StatusMM update failed for personId: " + personId);
                                                throw new RuntimeException("StatusMM update failed for personId: " + personId);
                                           }
                                //  }
                     }
                return true;
      }
		 
  private boolean processRenewStatusMM(String dot, GatePassMain gpm) {
		try {
			 if (dot == null || dot.trim().isEmpty()) {
		            throw new IllegalArgumentException("DOT cannot be null or empty");
		        }

			    LocalDate dotDate = LocalDate.parse(dot);
			    LocalDate today = LocalDate.now();

			    long personId = getPersonIdFromCmsPerson(gpm.getGatePassId());

			    PersonStatusIds ids = workmenDao.getPersonStatusIds(personId);
			    if (ids == null || ids.getActiveId() == null || ids.getInactiveId() == null) {
		            throw new RuntimeException("StatusMM records not found for personId: " + personId);
		        }
			    //GET THE OLD DOT FROM STATUSMM ACTIVE RECORD
			    String oldDot = workmenDao.getoldDotFromActiveRecord(ids.getActiveId());
			    if (oldDot == null || oldDot.trim().isEmpty()) {
		            throw new RuntimeException("Old DOT not found for personId: " + personId);
		        }
			    LocalDate oldDotDate = LocalDate.parse(oldDot);

			    if (oldDotDate.isAfter(today)) {

			        // 1. update existing inactive record (VALIDTO = today - 1)
			        boolean updated = workmenDao.updateInactiveValidTo(ids.getInactiveId(),today.minusDays(1));

			        if (!updated) {
			        	log.error("StatusMM update failed for inactiverecord validto personId: " + personId);
			        	throw new RuntimeException("StatusMM update failed for inactiverecord validto personId: " + personId);
			        }

			        // 2. insert ACTIVE record (today -> dot)
			        boolean activeInserted = workmenDao.insertActiveStatusRecord(personId,today,dotDate);

			        if (!activeInserted) {
			        	log.error("StatusMM insert failed for activerecord  personId: " + personId);
			        	throw new RuntimeException("StatusMM insert failed for activerecord  personId: " + personId);
			        }

			        // 3. insert INACTIVE record (dot+1 -> 3000-01-01)
			        boolean inactiveInserted = workmenDao.insertInactiveStatusRecord(personId,dotDate.plusDays(1),LocalDate.of(3000, 1, 1));
                   
			        if(!inactiveInserted) {
			        	log.error("StatusMM insert failed for inactiverecord  personId: " + personId);
	                  throw new RuntimeException("StatusMM insert failed for inactiverecord  personId: " + personId);
                     }
			        return true;

			    }else {

		            boolean updated =workmenDao.updatePersonStatusValidityRenew(ids.getActiveId(), ids.getInactiveId(),dot);

		            if (!updated) {
		                throw new RuntimeException("Failed to update status validity for personId: " + personId);
		            }

		            return true;
		        }

		    } catch (Exception e) {

		        log.error("Error while processing StatusMM renewal", e);

		        throw e; // Important for transaction rollback
		    }
		}
  
		 public String saveMinimumWageToStaging() {
				return QueryFileWatcher.getQuery("SAVE_KTC_MINIMUMWAGE");
			}
			public String minimumWageFromExistsInStagging() {
				return QueryFileWatcher.getQuery("CHECK_MINIMUMWAGE_FROM_EXISTS_IN_STAGGING");
			}
		@Override
		public void saveMinimumWageToStaging(MinimumWageDTO stag) {
			String sql =saveMinimumWageToStaging();
//			String sql ="INSERT INTO KTC_STATE_MINIMUMWAGE(UNITCODE,STATENM,ZONENM,SKILLNM,BASIC,DA,OTHERALLOW,FROMDATE,TODATE,RECORD_PROCESSED,RECORD_STATUS,RECORD_UPDATEDON) \r\n"
//					+ "VALUES (?,?,?,?,?,?,?,?,?,'N','NEW',GETDATE())";

		    java.util.Date toDate = stag.getToDate();

		    // if TODATE empty, use default future date
		    if (toDate == null) {
		        toDate = java.sql.Date.valueOf("3000-01-01");
		    }
			    jdbcTemplate.update(sql,
			        /* 01 */ stag.getUnitCode(),
			        /* 02 */ stag.getStateName(),
			        /* 03 */ stag.getZoneName(),           
			        /* 04 */ stag.getSkillName(),               
			        /* 05 */ stag.getBasic(),
			        /* 06 */ stag.getDa(),
			        /* 07 */ stag.getOtherAllowance(),
			        /* 08 */ stag.getFromDate(),
			        /* 09 */ toDate
			    );
			
		}
		@Override
		public boolean minimumWageFromExistsInStagging(String unitCode, String stateName, String zoneName, String skillName,
				java.util.Date fromDate) {
			String sql =minimumWageFromExistsInStagging();
			//String sql = "select count(*) from KTC_STATE_MINIMUMWAGE where UNITCODE=? and STATENM=? and ZONENM=? and SKILLNM=? and FROMDATE=?";
		    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, unitCode, stateName,zoneName,skillName,fromDate);
		    return count != null && count > 0;
		}
		public String updateMinimumWageToStaging() {
			return QueryFileWatcher.getQuery("UPDATE_MINIMUMWAGE_IN_STAGGING");
		}
		@Override
		public void updateMinimumWageToStaging(MinimumWageDTO staging){
			String sql =updateMinimumWageToStaging();
//       String sql ="update KTC_STATE_MINIMUMWAGE set BASIC=?,DA=?,OTHERALLOW=?,RECORD_STATUS=? ,RECORD_UPDATEDON=?\r\n"
//		         + "where UNITCODE=? and STATENM=? and ZONENM=? and SKILLNM=?";
       
       java.util.Date toDate = staging.getToDate();

	    // if TODATE empty, use default future date
	    if (toDate == null) {
	        toDate = java.sql.Date.valueOf("3000-01-01");
	    }
		    jdbcTemplate.update(sql,
		        /* SET values */
		    		staging.getBasic() ,
		    		staging.getDa(),
		    		staging.getOtherAllowance(),
		    		//staging.getFromDate(),
		    		//toDate,
		    		"UPDATED",
		           new Timestamp(System.currentTimeMillis()),

		        /* WHERE values (VERY IMPORTANT) */
		           staging.getUnitCode(),
		           staging.getStateName(),
		           staging.getZoneName(),
		           staging.getSkillName()
		    );
		}
		 @Override
		 public void callMinimumWageProcessingSP() {
		     jdbcTemplate.execute("EXEC CMS_StateMinimumWage_Upload");
		 }
		 public String minimumWagesExistsInStagging() {
				return QueryFileWatcher.getQuery("MINIMUMWAGE_EXISTS_IN_STAGGING");
			}
		 public String isUserExists() {
				return QueryFileWatcher.getQuery("CHECK_USER_EXISTS_IN_MASTERUSER");
			}
		 public String saveuserImport() {
				return QueryFileWatcher.getQuery("SAVE_USERIMPORT_IN_MASTERUSER");
			}
		 public String saveUserRoleMapping() {
				return QueryFileWatcher.getQuery("SAVE_USER_ROLE_IN_USERROLEMAPPING");
			}
		 
		 @Override
			public boolean minimumWagesExistsInStagging(String unitCode, String stateName, String zoneName, String skillName) {
				String sql =minimumWagesExistsInStagging();
				//String sql = "select count(*) from KTC_STATE_MINIMUMWAGE where UNITCODE=? and STATENM=? and ZONENM=? and SKILLNM=?";
			    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, unitCode, stateName,zoneName,skillName);
			    return count != null && count > 0;
			}
		 public String isActiveUserExists() {
				return QueryFileWatcher.getQuery("CHECK_ACTIVE_USER_EXISTS");
			}
		 @Override
			public Integer isUserExists(String userName){
				String sql=isActiveUserExists();
			    //String sql = "select UserId from MASTERUSER where userAccount=? and status='A'";
			  List<Integer> result = jdbcTemplate.query(sql, new Object[]{userName},
				        (rs, rowNum) -> rs.getInt("UserId"));
				    return result.isEmpty() ? null : result.get(0);
			}
		 public String saveusers() {
			    return QueryFileWatcher.getQuery("SAVE_USER");
		    }
		 public String insertrolemapping() {
			    return QueryFileWatcher.getQuery("INSERT_ROLE_MAPPING");
		    }
		  @Override
		    public void saveuserImport(UserImport user, List<Long> roleIds){
		  
			  String query=saveusers();
		        String query1=insertrolemapping();
		        KeyHolder keyHolder = new GeneratedKeyHolder();

		        // Encrypt password
		        user.setPassword(passwordEncoder.encode(user.getPassword()));
		        // Insert the user and retrieve the generated key
		        jdbcTemplate.update(connection -> {
		            PreparedStatement ps = connection.prepareStatement(query, new String[] {"UserId"});
		            ps.setString(1, user.getFirstName());
		            ps.setString(2, user.getLastName());
		            ps.setString(3, user.getEmail());
		            ps.setString(4, user.getMobileNumber());
		            ps.setString(5, user.getPassword());
		            ps.setString(6, user.getUserAccount());
		            return ps;
		        }, keyHolder);
		        // Set the generated UserId in the user object
		        int userId = keyHolder.getKey().intValue();
		        user.setUserId(userId);
		        // Save role mappings if provided
		        if (roleIds != null) {
		            for (Long roleId : roleIds) {
		               // String insertRoleMappingQuery = "INSERT INTO UserRoleMapping (UserId, RoleId) VALUES (?, ?)";
		                jdbcTemplate.update(query1, user.getUserId(), roleId);
		            }
		        }
			}
		  @Override
		    public void saveUserRoleMapping(Long userId, Integer roleId){
			 String sql=saveUserRoleMapping();
		       // String sql = "INSERT INTO UserRoleMapping (UserId, RoleId) VALUES (?, ?)";
		        jdbcTemplate.update(sql, userId, roleId);
		    }
		  public String getContractorIdByCodeInCMSVendor() {
				return QueryFileWatcher.getQuery("GET_CONTRACTORID_FROM_CMSVENDOR");
			}
		  public String insertContractorInCMSVendor() {
				return QueryFileWatcher.getQuery("INSERT_CONTRACTOR_IN_CMSVENDOR");
			}
		  public String updateContractorInCMSVendor() {
				return QueryFileWatcher.getQuery("UPDATE_CONTRACTOR_IN_CMSVENDOR");
			}
		  public String getZoneIdFromMinimumWage() {
				return QueryFileWatcher.getQuery("GET_ZONEID_FROM_MINIMUMWAGE");
			}
		  @Override
			public Long getContractorIdByCodeInCMSVendor(String subContractorCode) {
				String sql=getContractorIdByCodeInCMSVendor();
			    //String sql = "select VENDORID from CMSVENDOR where VENDORCODE=? and IS_BLOCKED='N'";
			    		
			    try {
			        return jdbcTemplate.queryForObject(sql, new Object[]{subContractorCode}, Long.class);
			    } catch (EmptyResultDataAccessException e) {
			        return null;
			    }
			}
		@Override
		public void insertContractorInCMSVendor(CMSVendor cmsvendor) {
		     String sql=insertContractorInCMSVendor();
		    //String sql = "insert into CMSVENDOR  (VENDORID,VENDORCODE,VENDORNAME,IS_BLOCKED) values (?,?,?,'N')";

		    jdbcTemplate.update(sql, cmsvendor.getVendorId(),cmsvendor.getVendorCode(),cmsvendor.getVendorName());
		}
		@Override
		public void updateContractorInCMSVendor(CMSVendor cmsvendor) {
			
				String sql=updateContractorInCMSVendor();
			   //String sql="update CMSVENDOR set VENDORNAME=? where VENDORID=?";
			    jdbcTemplate.update(sql,
			    		//cmsvendor.getVendorCode(),
			    		cmsvendor.getVendorName(),
			    		cmsvendor.getVendorId()
			    );
			}
		@Override
		public Integer getZoneIdFromMinimumWage(String zoneValue, Integer unitId) {
			String sql=getZoneIdFromMinimumWage();
			//String sql="select  cgm.GMID as ZoneId  from CMSSTATEMINIMUMWAGE cmssm inner join CMSGENERALMASTER cgm on  cgm.GMNAME = cmssm.ZONENM where cmssm.ZONENM=? and cmssm.UNITID=?";
			 List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class,zoneValue.trim(), unitId);
			    return result.isEmpty() ? null : result.get(0);
		}
		 public String updateuserRole() {
			    return QueryFileWatcher.getQuery("UPDATE_USER_ROLE_TEMPLATE");
		    }
		    public String fetchRoleSql() {
			    return QueryFileWatcher.getQuery("FETCH_ROLES_EXISTS");
		    }
		    public String insertuserRole() {
			    return QueryFileWatcher.getQuery("INSERT_USER_ROLE");
		    }
		    public String saveorgacctset() {
			    return QueryFileWatcher.getQuery("SAVE_ORG_ACCT_SET");
		    }
		@Override
		public void updateuserImport(UserImport user, Integer userId, List<Long> roleIds) {

		    String insertRoleSql = insertuserRole();

		    // ✅ IMPORTANT FIX
		    user.setUserId(userId);
		    user.setPassword(passwordEncoder.encode(user.getPassword()));
		    //String sql = "UPDATE MASTERUSER SET EmailId = ?, FirstName = ?, LastName = ?, ContactNumber = ?, Password = ? WHERE UserId = ?";
		    String sql =updateuserRole();
		    jdbcTemplate.update(sql,
		            user.getEmail(),
		            user.getFirstName(),
		            user.getLastName(),
		            user.getMobileNumber(),
		            user.getPassword(),
		            user.getUserId()
		    );

		    // =====================================================
		    // ✅ FETCH EXISTING ROLES
		    // =====================================================
		    //String fetchRolesSql = "SELECT RoleId FROM UserRoleMapping WHERE UserId = ?";
		    
		    String fetchRolesSql =fetchRoleSql();
		    
		    List<Long> existingRoles = jdbcTemplate.query(
		            fetchRolesSql,
		            new Object[]{user.getUserId()},
		            (rs, rowNum) -> rs.getLong("RoleId")
		    );

		    Set<Long> existingRoleSet = new HashSet<>(existingRoles);

		    // =====================================================
		    // ✅ INSERT ONLY NEW ROLES
		    // =====================================================
		    for (Long roleId : roleIds) {

		        if (!existingRoleSet.contains(roleId)) {

		            jdbcTemplate.update(insertRoleSql, user.getUserId(), roleId);
		        }
		    }
		}
		public String getOrgLevelEntryId() {
		    return QueryFileWatcher.getQuery("FETCH_ORGLEVELENTRYID");
	    }
		@Override
		public Long getOrgLevelEntryId(String name, Long orgLevelDefId) {

			//String sql = "SELECT ORGLEVELENTRYID FROM ORGLEVELENTRY WHERE NAME = ? AND ORGLEVELDEFID = ?";
			String  sql = getOrgLevelEntryId();
			 try {
			        return jdbcTemplate.queryForObject(sql,new Object[]{name.trim(), orgLevelDefId},Long.class);
			    } catch (Exception e) {
			        return null; // not found
			    }
		}
	
		@Override
		public Long insertUserOrgAccountSet(String userAccount) {
			String query=saveorgacctset();
			Long generatedId = jdbcTemplate.queryForObject(query,
                    new Object[] { userAccount, userAccount },
                    Long.class);
            if (generatedId == null) {
                System.out.println("Failed to retrieve the generated ID after insert.");
                throw new RuntimeException("Failed to retrieve the generated ID after insert.");
            }
            System.out.println("Generated ID: " + generatedId);
            return generatedId;
        }
		public String insertUserOrgMapping() {
		    return QueryFileWatcher.getQuery("SAVE_USER_ORG_MAPPING");
	    }
		@Override
		public void insertUserOrgMapping(List<Long> orgEntryIds, Long orgSetId) {
			 String sql =insertUserOrgMapping();
		    //String sql = "INSERT INTO OLACCTSETMM (ORGLEVELENTRYID, ORGACCTSETID, UPDATEDTM) VALUES (?, ?, getdate())";

		    for (Long entryId : orgEntryIds) {
		        jdbcTemplate.update(sql, entryId,orgSetId);
		    }
		}
		public String getOrgAccountSetIdFromSet() {
		    return QueryFileWatcher.getQuery("GET_ORG_ACCOUNT_SET_ID");
	    }
		@Override
		public Long getOrgAccountSetIdFromSet(String userAccount) {

		    //String sql = "SELECT ORGACCTSETID FROM ORGACCTSET WHERE SHORTNM = ?";
			String sql =getOrgAccountSetIdFromSet();
		    try {
		        return jdbcTemplate.queryForObject(sql,new Object[]{userAccount},Long.class);
		    } catch (EmptyResultDataAccessException e) {
		        // ✅ No record found
		        return null;
		    }
		}
		public String getExistingOrgMappings() {
		    return QueryFileWatcher.getQuery("GET_EXISTING_ORG_MAPPING");
	    }
		@Override
		public List<Long> getExistingOrgMappings(Long orgSetId) {
              String sql = getExistingOrgMappings();
		   // String sql = "SELECT ORGLEVELENTRYID FROM OLACCTSETMM WHERE ORGACCTSETID = ?";

		    return jdbcTemplate.query(
		            sql,
		            new Object[]{orgSetId},
		            (rs, rowNum) -> rs.getLong("ORGLEVELENTRYID")
		    );
		}
		public String getOrgLevelDefIds() {
		    return QueryFileWatcher.getQuery("GET_ALL_ORG_LEVEL_DEFID");
	    }
		
		@Override
		public Map<String, Long> getAllOrgLevelDefIds() {

		    //String sql = "SELECT ORGLEVELDEFID, NAME FROM ORGLEVELDEF";

		    String sql =getOrgLevelDefIds();
		    return jdbcTemplate.query(sql, rs -> {
		        Map<String, Long> map = new HashMap<>();

		        while (rs.next()) {
		            String name = rs.getString("NAME");

		            if (name != null) {
		                map.put(name.toLowerCase().trim(), rs.getLong("ORGLEVELDEFID"));
		            }
		        }
		        return map;
		    });
		}
		public String getGmTypeIdBasedOnGmType() {
		    return QueryFileWatcher.getQuery("GET_GMTYPEID");
	    }
		public String isGmNameGmTypeExists() {
		    return QueryFileWatcher.getQuery("CHECK_GMNAME_EXISTS");
	    }
		 @Override
		 public Integer getGmTypeId(String gmType) {
			    if (gmType == null || gmType.trim().isEmpty()) return null;
			    String sql=getGmTypeIdBasedOnGmType();
			    //String sql = "select GMTYPEID from CMSGMTYPE where GMTYPE = ?";
			    List<Integer> result = jdbcTemplate.query(sql, new Object[]{gmType.trim()},
			        (rs, rowNum) -> rs.getInt("GMTYPEID"));
			    return result.isEmpty() ? null : result.get(0);
			}
		 @Override
			public boolean isGmNameGmTypeExists(String gmName, Integer gmTypeId) {
		    	String sql=isGmNameGmTypeExists();
				// String sql = "select count (*) from CMSGENERALMASTER where GMNAME=? and GMTYPEID =?";
				    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, gmName,gmTypeId);
				    return count != null && count > 0;
			}
		 public String activeGatepassExists() {
			    return QueryFileWatcher.getQuery("IS_ACTIVE_GATEPASS_EXISTS");
		    }
		 @Override
			public boolean activeGatepassExists(String gatepassNumber) {
				//String sql="select count(*) from GATEPASSMAIN where GatePassId=? and GatePassTypeId in (1,2,12,15) and GatePassStatus=4";
				String sql=activeGatepassExists();
				  Integer count = jdbcTemplate.queryForObject(sql, Integer.class, gatepassNumber);
				    return count != null && count > 0;
				}
		 public String workorderExistsForPlantAndContractor() {
			    return QueryFileWatcher.getQuery("IS_WORKORDER_EXISTS_WITH_PLANT_CONTRACTOR");
		    }
		 @Override
			public Map<String, Object>  workorderExistsForPlantAndContractor(String workorderNumber,Integer unitId){

			    String sql =workorderExistsForPlantAndContractor();
			    		//"SELECT WORKORDERID, VALIDDT FROM CMSWORKORDER WHERE SAP_WORKORDER_NUM=? and UNITID=?";

			    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, workorderNumber,unitId);

			    return list.isEmpty() ? null : list.get(0);
			}
		 public String licenseExistsWithWorkorder() {
			    return QueryFileWatcher.getQuery("IS_LICENSE_EXISTS_WITH_WORKORDER");
		    }
		 @Override
			public Map<String, Object> licenseExistsWithWorkorder(String workorderNumber, String wcesicNumber){
			 String sql =licenseExistsWithWorkorder();
//			    String sql = "select llwc.WOLLID AS WCID,wc.WC_FROM_DTM as VALIDFROM, wc.WC_TO_DTM as VALIDTO,wc.LICENCE_TYPE as LICENSETYPE from CMSWORKORDER wo \r\n"
//			    		+ "join CMSWORKORDER_LLWC llwc on llwc.WONUMBER=wo.SAP_WORKORDER_NUM\r\n"
//			    		+ "join CMSCONTRACTOR_WC wc on  wc.WC_CODE=llwc.LICENSE_NUMBER where wo.SAP_WORKORDER_NUM=? and wc.WC_CODE=?";

			    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, workorderNumber, wcesicNumber);

			    return list.isEmpty() ? null : list.get(0);
			}
		 public String getWorkorderIdBasedonPE() {
			    return QueryFileWatcher.getQuery("GET_WORKORDER_BASED_ON_PE");
		    }
		public String IsWorkorderExistsForOtherContractor() {
			    return QueryFileWatcher.getQuery("IS_WORKORDER_EXISTS_FOR_OTHER_CONTRACTOR");
		    }
			
		 @Override
			public Integer getWorkorderIdBasedonPE(String workorderNumber,Integer unitId) {
			    if (workorderNumber == null || workorderNumber.trim().isEmpty()) return null;
			    String sql=getWorkorderIdBasedonPE();
			    //String sql = "SELECT WORKORDERID FROM CMSWORKORDER WHERE SAP_WORKORDER_NUM = ? and UNITID=?";
			    List<Integer> result = jdbcTemplate.queryForList(sql, Integer.class, workorderNumber.trim(),unitId);
			    return result.isEmpty() ? null : result.get(0);
			}
		@Override
		public Integer IsWorkorderExistsForOtherContractor(String workOrder, String contractorCode) {
			String sql=IsWorkorderExistsForOtherContractor();
			//String sql = "select WORKORDERID from CMSWORKORDER cwo join CMSCONTRACTOR cmsc on cmsc.CONTRACTORID=cwo.CONTRACTORID where cwo.SAP_WORKORDER_NUM=? and cmsc.CODE <> ?";
			 List<Integer> result = jdbcTemplate.query(sql, new Object[]{workOrder,contractorCode},
				        (rs, rowNum) -> rs.getInt("WORKORDERID"));
				    return result.isEmpty() ? null : result.get(0);
		}
		
		public String getPrincipalEmployerExists() {
		    return QueryFileWatcher.getQuery("GET_PRINCIPAL_EMPLOYER_EXISTS");
	    }
		public String updatePrincipalEmployer() {
		    return QueryFileWatcher.getQuery("UPDATE_PRINCIPAL_EMPLOYER");
	    }
		
		@Override
		public Long getPrincipalEmployerExists(String code, String organization, String businessType){
			String sql=getPrincipalEmployerExists();
		    //String sql = "select unitid from CMSPRINCIPALEMPLOYER where code = ? and ORGANIZATION =? and BUSINESSTYPE=? and ISACTIVE=1";
		    try {
		        return jdbcTemplate.queryForObject(sql, new Object[]{code, organization,businessType}, Long.class);
		    } catch (EmptyResultDataAccessException e) {
		        return null;
		    }
		}
		
		@Override
		public void updatePrincipalEmployer(PrincipalEmployer p, String createdBy, Long unitId) {

			String sql=updatePrincipalEmployer();
			
//		    String sql = "UPDATE CMSPRINCIPALEMPLOYER SET " +
//		            "NAME = ?, " +
//		            "ADDRESS = ?, " +
//		            "MANAGERNAME = ?, " +
//		            "MANAGERADDRS = ?, " +
//		            "MAXWORKMEN = ?, " +
//		            "MAXCNTRWORKMEN = ?, " +
//		            "BOCWAPPLICABILITY = ?, " +
//		            "ISMWAPPLICABILITY = ?, " +
//		            "LICENSENUMBER = ?, " +
//		            "PFCODE = ?, " +
//		            "WCNUMBER = ?, " +
//		            "FACTORYLICENCENUMBER = ?, " +
//		            "STATEID = ?, " +                 // 🔥 IMPORTANT (trigger uses this)
//		            "UPDATEDBY = ?, " +
//		            "UPDATEDTM = GETDATE() " +       // 🔥 MUST for audit
//		            "WHERE UNITID = ?";

		    jdbcTemplate.update(sql,
		            p.getName(),
		            p.getAddress(),
		            p.getManagerName(),
		            p.getManagerAddrs(),
		            p.getMaxWorkmen(),
		            p.getMaxCntrWorkmen(),
		            p.getBocwApplicability(),
		            p.getIsMwApplicability(),
		            p.getLicenseNumber(),
		            p.getPfCode(),
		            p.getWcNumber(),
		            p.getFactoryLicenseNumber(),
		            Long.valueOf(p.getStateNM()),   // stateId
		            createdBy,
		            unitId
		    );
		}
		
		public String getPEStateExists() {
		    return QueryFileWatcher.getQuery("GET_PE_STATE_EXISTS");
	    }
		
		@Override
		public boolean getPEStateExists(Long unitId, Long stateId) {
		     String sql=getPEStateExists();
	       // String sql = "select count(*) from CMSPESTATE where UNITID=? and STATEID=?";
	        Long count = jdbcTemplate.queryForObject(sql, Long.class, unitId, stateId);
	        return count != null && count > 0;
		}
		@Override
		public Set<String> getExistingPECodes(List<PrincipalEmployer> list, long orgLevelDefId) {

		    if (list == null || list.isEmpty()) {
		        return Collections.emptySet();
		    }

		    List<String> peCodes = list.stream()
		            .map(PrincipalEmployer::getCode)
		            .filter(Objects::nonNull)
		            .map(code -> code.trim().toUpperCase())   // ✅ normalize
		            .distinct()
		            .toList();

		    if (peCodes.isEmpty()) {
		        return Collections.emptySet();
		    }

		    String placeholders = peCodes.stream()
		            .map(c -> "?")
		            .collect(Collectors.joining(","));

		    String sql =
		        "SELECT UPPER(LTRIM(RTRIM(NAME))) FROM ORGLEVELENTRY " +
		        "WHERE ORGLEVELDEFID = ? " +
		        "AND UPPER(LTRIM(RTRIM(NAME))) IN (" + placeholders + ")";

		    List<Object> params = new ArrayList<>();
		    params.add(orgLevelDefId);
		    params.addAll(peCodes);

		    List<String> existing = jdbcTemplate.queryForList(
		            sql,
		            params.toArray(),
		            String.class
		    );

		    return new HashSet<>(existing);
		}
		
		@Transactional(rollbackFor = Exception.class)
		   @Override
		    public void insertIntraPlantTransferTemp(GatePassMain gm, String createdBy, String dot){
			try {
		    	String sql=insertIntoTempSamePlantDiffContIntraPlantTransferQuery();
		        //String sql = "INSERT INTO CMSRequestItemIntraPlantTransfer (GatepassId,unitId,contractorId,DepartmentId,AreaId,EICId,workorderId,wcesicId,LLId,Esic,EffectiveDate,Dot,updatedBy) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
		        jdbcTemplate.update(sql,   gm.getGatePassId(),gm.getUnitId(),gm.getContractor(),gm.getDepartment(),gm.getSubdepartment(),gm.getEic(),gm.getWorkorder(),
		                gm.getWcEsicId(),gm.getLlId(),gm.getEsicNumber(),parseSqlDate(gm.getEffectiveFromDate()), parseSqlDate(dot) , createdBy);
		        updateGatepassMainIntraPlantTransfer(gm,createdBy,dot);
			}catch (Exception e) {
		        log.error("Error in insertIntraPlantTransferTemp", e);
		        throw new RuntimeException("Transaction failed, rolling back", e); //  important
		    }
		    }
		public String updateGtepassmainForIntraplanttransfer() {
			return QueryFileWatcher.getQuery("UPDATE_GATEPASSMAIN_FOR_INTRAPLANT_TRNASFER");
		}
		@Transactional(rollbackFor = Exception.class)
		@Override
		public void updateGatepassMainIntraPlantTransfer(GatePassMain gm, String createdBy, String dot) {

		    try {
		        //String sql = "update gatepassmain set UnitId=?,ContractorId=?,DepartmentId=?,AreaId=?,EicId=?,WorkorderId=?,WcEsicNo=?,LLNo=?,EsicNumber=?,DOT=?,UpdatedBy=?,UpdatedDate=GETDATE(),GatePassTypeId=? where GatePassId=?";
		    	String sql =updateGtepassmainForIntraplanttransfer();
		        int rows = jdbcTemplate.update(sql,gm.getUnitId(),gm.getContractor(),gm.getDepartment(),gm.getSubdepartment(),gm.getEic(),gm.getWorkorder(),gm.getWcEsicNo(),gm.getLlNo(),
		                         gm.getEsicNumber(),dot,createdBy,GatePassType.RENEW.getStatus(),gm.getGatePassId());

		        //  Check if update actually happened
		        if (rows == 0) {
		        	log.error("Update failed: No record found for GatePassId in GatepassMain",rows);
		        	throw new RuntimeException("Update failed: No record found for GatePassId = " + gm.getGatePassId());
		        }
		        
		        int renew =insertRenewGatepassTransactionMapping(gm,createdBy);
			    if(renew==0) {
			    	log.error("insertions failed in gatepassTransactionMapping gatepass: "+gm.getGatePassId());
		           throw new RuntimeException("insertions failed in gatepassTransactionMapping gatepass: "+gm.getGatePassId());
			    }
		        //  Call next process
		        boolean result = gatePassActionPersonInsertIntraPlantTransfer(gm, createdBy, dot);

		        if (!result) {
		        	log.error("Update failed: GatePassPerson action processing failed",result);
		            throw new RuntimeException("GatePassPerson action processing failed");
		        }
		        try {
		        	String wfdIntegration = this.getWFDIntegration();
		        	if("yes".equalsIgnoreCase(wfdIntegration)) {
		        		api.updateOnBoardingDetails(gm.getTransactionId());
		        	}
		        	}catch(Exception e) {
		        		log.info(e.getMessage());
						 throw new RuntimeException("renew Api Integration Failed");
		        	}
		    } catch (Exception e) {
		        log.error("Error in updateIntraPlantTransfer for GatePassId: {}", gm.getGatePassId(), e);
		        throw new RuntimeException("Transaction failed, rolling back", e); //  triggers rollback
		    }
		}
		public int insertRenewGatepassTransactionMapping(GatePassMain gm,String createdBy) {
			String transactionId= workmenDao.getNextTransactionId();
			gm.setTransactionId(transactionId);
			String sql =insertBulkCancelGatepassTransactionMapping();
			//String sql = "INSERT INTO GatePassTransactionMapping (TRANSACTIONID, GATEPASSID, GATEPASSTYPEID, CREATEDDATE) VALUES (?, ?, ?, GETDATE())";
		    int result=jdbcTemplate.update(sql,transactionId, gm.getGatePassId(),GatePassType.RENEW.getStatus() );
		    if(result==0) {
		    	log.error("gatepassTransactionMapping insertion failed ");
	            throw new RuntimeException("gatepassTransactionMapping insertion failed ");
		    }
		    return result;
		}
		@Transactional(rollbackFor = Exception.class)
		public boolean gatePassActionPersonInsertIntraPlantTransfer(GatePassMain gpm, String createdBy, String dot) {

		    try {
		        // 1. Update CMSPERSON
		        long personId = this.updateCmsPerson(gpm,dot);
		        if (personId <= 0) {
		        	log.error("CMSPERSON update failed");
		            throw new RuntimeException("CMSPERSON update failed");
		        }

		        // 2. Update CMSPERSONJOBHIST
		        boolean jobHistUpdated = this.updateCmsPersonJobHist(gpm, personId);
		        if (!jobHistUpdated) {
		        	log.error("CMSPERSONJOBHIST update failed");
		            throw new RuntimeException("CMSPERSONJOBHIST update failed");
		        }

		        // 3. Update CUSTDATA
//		        boolean custDataTypeStatusUpdated =this.updateCmsPersonCustDataIntaPlantTransferEffectiveTillStatus(personId,GatePassType.CANCEL.getStatus(),gpm);
//
//		        if (!custDataTypeStatusUpdated) {
//		        	log.error("CMSPERSONCUSTOMDATA Cancel effectivetill update failed");
//		            throw new RuntimeException("CMSPERSONCUSTOMDATA Canceleffectivetill  update failed");
//		        }

		        // 4. Update StatusMM (only if active)
		        if (this.isPersonActiveInStatusMM(personId)) {

		            PersonStatusIds ids = this.getPersonStatusIds(personId);

		            if (ids.getActiveId() == null && ids.getInactiveId() == null) {

		            	log.error("PersonStatusMM IDs not found for PersonId : {}", personId);
		                throw new RuntimeException("PersonStatusMM IDs not found");
		            }
		                boolean statusUpdated =this.updatePersonStatusValidityForIntraPlantTransfer(ids.getActiveId(),ids.getInactiveId(),dot,gpm.getEffectiveFromDate());

		                if (!statusUpdated) {
		                	log.error("CMSPERSONSTATUSMM update failed");
		                    throw new RuntimeException("CMSPERSONSTATUSMM update failed");
		                }
		            }
		     // 5. Insert new CUSTOMDATA
		        
		        
		       // boolean custDataUpdated =this.updateCmsPersonCustDataDiffPlantSameContIntaPlantTransfer(personId,gpm,dot,createdBy);
		        boolean custDataUpdated =this.insertIntoCustDataRenew( createdBy, personId, GatePassType.RENEW.getStatus());
		        if (!custDataUpdated) {
		        	log.error("CMSPERSONCUSTOMDATA renew update failed");
		            throw new RuntimeException("CMSPERSONCUSTOMDATA renew update failed");
		        }
		        
		        return true;
		    } catch (Exception e) {
		        log.error("Error in gatePassActionPersonInsertIntraPlantTransfer for GatePassId: {}",gpm.getGatePassId(), e);

		        //  THIS TRIGGERS ROLLBACK
		        throw new RuntimeException("Transaction failed, rolling back", e);
		    }
		}
		public String updateCmspersonDot() {
			return QueryFileWatcher.getQuery("UPDATE_CMSPERSON_DOT");
		}
		private long updateCmsPerson(GatePassMain gpm,String dot) {

		    try {
		        //String updateSql = "UPDATE CMSPERSON SET DATEOFTERMINATION = ?, ESICNUMBER = ? WHERE EMPLOYEECODE = ?";
		        String updateSql =updateCmspersonDot();
		        int rows = jdbcTemplate.update(updateSql,dot,gpm.getEsicNumber(),gpm.getGatePassId());

		        //  If no row updated - throw exception
		        if (rows == 0) {
		        	log.error("CMSPERSON update failed: No record found for EMPLOYEECODE");
		            throw new RuntimeException("CMSPERSON update failed: No record found for EMPLOYEECODE = "
		                    + gpm.getGatePassId());
		        }

		        //  Fetch employee ID
		        //String selectSql = "SELECT EMPLOYEEID FROM CMSPERSON WHERE EMPLOYEECODE = ?";
		        String selectSql = getEmployeeIdFromCmspersonQuery();
		        Long personId = jdbcTemplate.queryForObject(selectSql,new Object[]{gpm.getGatePassId()},Long.class);

		        //  Validate result
		        if (personId == null || personId <= 0) {
		            throw new RuntimeException("EMPLOYEEID not found after update for EMPLOYEECODE = "
		                    + gpm.getGatePassId());
		        }

		        return personId;

		    } catch (Exception e) {
		        log.error("Error updating CMSPERSON for EMPLOYEECODE: {}", gpm.getGatePassId(), e);

		        //  IMPORTANT: rethrow exception - triggers rollback
		        throw new RuntimeException("Failed to update CMSPERSON", e);
		    }
		}
		public String getDetailsFromGatepassmain() {
			return QueryFileWatcher.getQuery("GET_DETAILS_FROM_GATEPASSMAIN");
		}
		public String updateExistingRecordToYesterdayInJobhist() {
			return QueryFileWatcher.getQuery("UPDATE_EXISTING_RECORD_TO_YESTERDAY_IN_JOBHIST");
		}
		public String insertNewRecordInJobhist() {
			return QueryFileWatcher.getQuery("INSERT_NEW_RECORD_IN_JOBHIST");
		}
		private boolean updateCmsPersonJobHist(GatePassMain gpm, long personId) {
		 try {

		        // STEP 1: Get data from GATEPASSMAIN
		        //String fetchSql ="select TradeId,SkillId from GATEPASSMAIN where GatePassId=?";
		        String fetchSql  =getDetailsFromGatepassmain();     

		        GatePassMain data = jdbcTemplate.queryForObject(
		                fetchSql,
		                new Object[]{gpm.getGatePassId()},
		                (rs, rowNum) -> {

		                    GatePassMain gp = new GatePassMain();
		                    gp.setTrade(rs.getString("TradeId"));
		                    gp.setSkill(rs.getString("SkillId"));
		                    //gp.setTransactionId(rs.getString("TransactionId"));
		                    return gp;
		                }
		        );

		        // STEP 2: Expire existing record with gpm.effectivedate-1
		        //String updateSql ="UPDATE CMSPERSONJOBHIST SET VALIDTO = DATEADD(DAY, -1, ?) WHERE EMPLOYEEID = ? " ;
		        String updateSql = updateExistingRecordToYesterdayInJobhist();

		        jdbcTemplate.update(updateSql, gpm.getEffectiveFromDate(),personId);

		        // STEP 3: Insert new record
		        String insertSql =insertNewRecordInJobhist();
//		                "INSERT INTO CMSPERSONJOBHIST ( " +
//		                "EMPLOYEEID, TRADEID,SKILLID,UNITID, CONTRACTORID, DEPARTMENTID, " +
//		                "SUBDEPARTMENTID, WORKORDERID, EICID, VALIDFROM, VALIDTO ,UPDATEDTM) " +
//		                "VALUES (?,?,?,?,?,?,?, ?, ?,?,?,getdate())";

		        int inserted = jdbcTemplate.update(
		                insertSql,
		                personId,
		                data.getTrade(),
		                data.getSkill(),
		                gpm.getUnitId(),
		                gpm.getContractor(),
		                gpm.getDepartment(),
		                gpm.getSubdepartment(),
		                gpm.getWorkorder(),
		                gpm.getEic(),
		               // java.sql.Date.valueOf(data.getDoj()),
		                gpm.getEffectiveFromDate(),
		                java.sql.Date.valueOf("3000-01-01")
		        );

		        if (inserted == 0) {
		            log.error("Insert failed in CMSPERSONJOBHIST for personid = {}", personId);
		            throw new RuntimeException("Insert failed for personid = " + personId);
		        }

		        return true;

		    } catch (Exception e) {

		        log.error("Error in bulk renew for personid: {}", personId, e);
		        throw new RuntimeException("Failed CMSPERSONJOBHIST bulk renew", e);
		    }
		}
	public String getCustomDefIDforGPtype() {
		return QueryFileWatcher.getQuery("GET_CUSTOMDEFID_FOR_GP_TYPE");
	}
	public String getCustomDefIDforreasoning() {
		return QueryFileWatcher.getQuery("GET_CUSTOMDEFID_FOR_REASONING");
	}
	public String insertCustomdatadefinitionIntraplantTransfer() {
		return QueryFileWatcher.getQuery("INSERT_CUSTOMDATA_DEFINTION_INTRAPLANT_TRANSFER");
	}
	private boolean updateCmsPersonCustDataIntaPlantTransferEffectiveTillStatus(long personId, String gatepassType, GatePassMain gpm) {

	    try {
	        String defSqlGatePassType = getCustomDefIDforGPtype();
	        String defSqlGatePassStatus = getCustomDefID();
	        String defSqlReasoning  = getCustomDefIDforreasoning();
	        
	        Integer gatePassTypeDefId = jdbcTemplate.queryForObject(defSqlGatePassType, Integer.class);
	        Integer reasoningDefId  = jdbcTemplate.queryForObject(defSqlReasoning, Integer.class);
	       // Integer gatePassStatusDefId = jdbcTemplate.queryForObject(defSqlGatePassStatus, Integer.class);

	        //  Validate definition IDs
	        if (gatePassTypeDefId == null || reasoningDefId == null) {
	        	log.error("Custom definition IDs not found");
	            throw new RuntimeException("Custom definition IDs not found");
	        }

	        // Date handling
	       // LocalDate effectiveDate = LocalDate.parse(gpm.getEffectiveFromDate());
	       // LocalDate effectiveTillDate = effectiveDate.minusDays(1);
	        // Parse any supported format and convert to yyyy-MM-dd
	        Date parsedDate = parseDateQuiet(gpm.getEffectiveFromDate());

	        if (parsedDate == null) {

	            log.error("Invalid EffectiveFromDate : {}",gpm.getEffectiveFromDate());

	            throw new RuntimeException("Invalid EffectiveFromDate format");
	        }

	        // Convert to LocalDate
	        LocalDate effectiveDate =((java.sql.Date) parsedDate).toLocalDate();
	        LocalDate effectiveTillDate =effectiveDate.minusDays(1);

	        String sql =insertCustomdatadefinitionIntraplantTransfer(); 
//	        String sql = "INSERT INTO CMSPERSONCUSTOMDATA "
//	                + "(EMPLOYEEID, CSTMDEFID, CUSTOMDATATEXT, EFFECTIVEFROM, EFFECTIVETILL, CREATEDTM, UPDATEDTM, UPDATEDBY) "
//	                + "VALUES (?, ?, ?, CONVERT(date, GETDATE()), ?, GETDATE(), GETDATE(), ?)";

	        //  Insert GatePass Type and status
	        int count1 = jdbcTemplate.update(sql,personId,gatePassTypeDefId,gatepassType,java.sql.Date.valueOf(effectiveTillDate),gpm.getCreatedBy());
	        int count2 =jdbcTemplate.update(sql,personId,reasoningDefId,gpm.getReasoning(),"3000-01-01",gpm.getCreatedBy());
	        //  Validate inserts
	        if (count1 < 0 && count2 < 0) {
	        	log.error("Insert into CMSPERSONCUSTOMDATA gatepasstype and status,reasoning  failed");
	            throw new RuntimeException("Insert into CMSPERSONCUSTOMDATA failed");
	        }

	        return true;

	    } catch (Exception e) {
	        log.error("Error inserting CMSPERSONCUSTOMDATA for personId: {}", personId, e);

	        //  VERY IMPORTANT - triggers rollback
	        throw new RuntimeException("CMSPERSONCUSTOMDATA operation failed", e);
	    }
	}
	
	public String isPersonActiveInStatusMM() {
		return QueryFileWatcher.getQuery("IS_PERSON_ACTIVE_IN_STATUSMM");
	}

	public boolean isPersonActiveInStatusMM(long personId) {

	    try {
	    	String sql = isPersonActiveInStatusMM();

	        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, personId);

	        if (count == null) {
	            throw new RuntimeException("Failed to fetch is Person Active In StatusMM  for EMPLOYEEID = " + personId);
	        }

	        return count > 0;

	    } catch (Exception e) {
	        log.error("Error checking active status for EMPLOYEEID: {}", personId, e);

	        //  Important: throw exception - triggers rollback in calling method
	        throw new RuntimeException("Failed to check person active status", e);
	    }
	}
	
	public String getActivePersonStatusIds() {
		return QueryFileWatcher.getQuery("GET_ACTIVE_PERSON_STATUS_ID");
	}
	public String getInactivePersonStatusIds() {
		return QueryFileWatcher.getQuery("GET_INACTIVE_PERSON_STATUS_ID");
	}
	
	public PersonStatusIds getPersonStatusIds(long personId) {

	    PersonStatusIds ids = new PersonStatusIds();

	    try {
	        // ================= ACTIVE =================
	        String activeSql = getActivePersonStatusIds();

	        try {
	            Long activeId = jdbcTemplate.queryForObject(activeSql, Long.class, personId);
	            ids.setActiveId(activeId);
	        } catch (EmptyResultDataAccessException ex) {
	            ids.setActiveId(null); //  acceptable (no active record)
	        }

	        // ================= INACTIVE =================
	        String inactiveSql = getInactivePersonStatusIds();

	        try {
	            Long inactiveId = jdbcTemplate.queryForObject(inactiveSql, Long.class, personId);
	            ids.setInactiveId(inactiveId);
	        } catch (EmptyResultDataAccessException ex) {
	            ids.setInactiveId(null); // ✅ acceptable
	        }

	        return ids;

	    } catch (Exception e) {
	        log.error("Error fetching PersonStatusIds for EMPLOYEEID: {}", personId, e);

	        //  IMPORTANT - triggers rollback in transactional flow
	        throw new RuntimeException("Failed to fetch PersonStatusIds", e);
	    }
	}
	
	public String updateValidtodot() {
	    return QueryFileWatcher.getQuery("UPDATE_VALIDITY_ACTIVE_RECORD_BULKRENEW");
	}

	public String updateValidfromdotnextday() {
	    return QueryFileWatcher.getQuery("UPDATE_VALIDITY_FROM_RENEW");
	}


	public boolean updatePersonStatusValidityForIntraPlantTransfer(Long activeId, Long inactiveId, String dot, String effectiveFromDate) {
	    try {
	        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	        LocalDate dotDate = LocalDate.parse(dot, formatter);
	        LocalDate nextDay = dotDate.plusDays(1);
	        LocalDate effectiveFrom = LocalDate.parse(effectiveFromDate, formatter);

	        // ================= UPDATE ACTIVE =================
	        if (activeId != null) {
	        	//String sqlActive = updateValidtodot();
	            //String sql = "UPDATE CMSPERSONSTATUSMM SET VALIDTO = ?, VALIDFROM = ? WHERE PERSONSTATUSMMID = ?";
	            String sql = updateValidtodot();
	            int count1 = jdbcTemplate.update(sql,java.sql.Date.valueOf(dotDate),java.sql.Date.valueOf(effectiveFrom),activeId);
	            // int count1 = jdbcTemplate.update(sqlActive,java.sql.Date.valueOf(dotDate),activeId);
		        //int count3 = jdbcTemplate.update(sqlActiveEffectiveFrom,java.sql.Date.valueOf(effectiveFromDate),activeId);
	            //  Fail fast
	            if (count1 <= 0) {
	                throw new RuntimeException("Failed to update ACTIVE status for PERSONSTATUSMMID = " + activeId);
	            }
	        }

	        // ================= UPDATE INACTIVE =================
	        if (inactiveId != null) {

	            String sqlInactive = updateValidfromdotnextday();

	            int count2 = jdbcTemplate.update(sqlInactive,java.sql.Date.valueOf(nextDay),inactiveId);

	            // ✅ Fail fast
	            if (count2 <= 0) {
	                throw new RuntimeException("Failed to update INACTIVE status for PERSONSTATUSMMID = " + inactiveId);
	            }
	        }

	        return true;

	    } catch (Exception e) {
	        log.error("Error updating CMSPERSONSTATUSMM for activeId: {}, inactiveId: {}",
	                activeId, inactiveId, e);

	        //  VERY IMPORTANT - triggers rollback
	        throw new RuntimeException("CMSPERSONSTATUSMM update failed", e);
	    }
	}
	public String saveCMSPERSONCUSTDATA() {
		return QueryFileWatcher.getQuery("SAVE_CMSPERSON_CUSTOMDATA");
	}
	public String insertIntoCustData() {
		return QueryFileWatcher.getQuery("INSERT_CUSTOM_DATA");
	}
	public boolean insertIntoCustDataRenew(String updatedBy,long personId,String gatePassStatus) {
		String defSqlGatePass  = getCustomDefIDforGPtype();

		Integer gatePassDefId  = jdbcTemplate.queryForObject(defSqlGatePass, Integer.class);
		
		if (gatePassDefId == null ) {
	        log.error("Custom definition IDs not found");
	        return false;
	    }
		
		boolean result = false;
		String sql = insertIntoCustData();

		 int count1 =jdbcTemplate.update(sql,personId,gatePassDefId,gatePassStatus,"3000-01-01",updatedBy);
	   try {
	   if (count1 > 0 ) {
	   	result=true;
	   }else {
	       log.warn("Failed to create GatePass action for GatePassId: " );
	   }
	   }catch (Exception e) {
	       log.error("Error creating GatePass action for GatePassId: " , e);
	       return false;
	   }
	   return result;
	}
//	public boolean updateCmsPersonCustDataDiffPlantSameContIntaPlantTransfer(Long  personId, GatePassMain gpm,String dot,String createdBy){
//      try {
//    	  
//    	// Set required values into gpm
//          gpm.setGatePassAction(GatePassType.CREATE.getStatus());
//          gpm.setGatePassStatus(GatePassStatus.APPROVED.getStatus());
//         // gpm.setDot(dot);
//          gpm.setCreatedBy(createdBy);
//          
//	    String sql = saveCMSPERSONCUSTDATA(); 
//
//	    // Fetch all active custom definitions
//	    String defSql = "SELECT CSTMDEFID, CSTMDEFNAME FROM CMSPERSONCUSTOMDATADEFINITION WHERE ISACTIVE = 1";
//	    List<Map<String, Object>> defList = jdbcTemplate.queryForList(defSql);
//
//
//	    List<Object[]> batchArgs = new ArrayList<>();
//
//	    for (Map<String, Object> def : defList) {
//
//	        int defId = (Integer) def.get("CSTMDEFID");
//	        String fieldName = (String) def.get("CSTMDEFNAME");
//
//	        String value = mapGatePassValue(fieldName, gpm);
//
//	        // Skip null/empty values
//	        if (value == null || value.trim().isEmpty()) {
//	            continue;
//	        }
//          
//	        // ✅ Set EFFECTIVETILL conditionally
//	        Object effectiveTill = "GatePassType".equalsIgnoreCase(fieldName)
//	                ? dot              // only GatePassType gets DOT
//	                : "3000-01-01";           // others get default
//
//	        batchArgs.add(new Object[]{
//	        		personId,        // ?
//	                defId,             // ?
//	                value,             // ?
//	                effectiveTill,     // ? (EFFECTIVETILL)
//	                gpm.getCreatedBy()  // ?
//	        });
//	    }
//
//	    if (batchArgs.isEmpty()) {
//	          log.error("No custom data found to insert for PersonId : {}", personId);
//	    	return false; // nothing to insert
//	    }
//
//	    int[] result = jdbcTemplate.batchUpdate(sql, batchArgs);
//
//	    for (int count : result) {
//
//            if (count <= 0) {
//
//                log.error("Batch insert failed for PersonId : {}", personId);
//
//                throw new RuntimeException("Failed to insert CMSPERSONCUSTOMDATA");
//            }
//        }
//
//        log.info("CMSPERSONCUSTOMDATA inserted successfully for PersonId : {}", personId);
//
//      return true; // records inserted
//      } catch (Exception e) {
//
//          log.error("Error while inserting CMSPERSONCUSTOMDATA for PersonId : {}",personId,e);
//
//          // Rethrow so parent transaction rolls back
//          throw new RuntimeException("CMSPERSONCUSTOMDATA insert failed",e);
//      }
//	}
//	
	private String mapGatePassValue(String field, GatePassMain gp) {

	    switch (field) {

	        case "IdProof":
	            return gp.getAadhaarNumber();

	        case "PoliceVerificationDate":
	            return gp.getPoliceVerificationDate();

	        case "HealthCheckDate":
	            return gp.getHealthCheckDate();

	        case "UnitId":
	            return gp.getUnitId();

	        case "ContractorId":
	            return gp.getContractor();

	        case "DepartmentId":
	            return gp.getDepartment();

	        case "SkillId":
	            return gp.getSkill();

	        case "TradeId":
	            return gp.getTrade();

	        case "WorkorderId":
	            return gp.getWorkorder();

	        case "SectionId":
	            return gp.getSubdepartment();

	        case "MobileNumber":
	            return gp.getMobileNumber();

	        case "MaritalStatus":
	            return gp.getMaritalStatus();

	        case "NatureOfJob":
	            return gp.getNatureOfJob();

	        case "WcEsicNo":
	            return gp.getWcEsicId();

	        case "Technical":
	            return gp.getTechnical();

	        case "WorkFlowType":
	            return String.valueOf(gp.getWorkFlowType());

	        case "Comments":
	            return gp.getComments();

	        case "Address":
	            return gp.getAddress();

	        case "OnboardingType":
	            return gp.getOnboardingType();

	        case "PfNumber":
	            return gp.getPfNumber();

	        case "LLNo":
	            return gp.getLlId();

	        case "GatePassType":
	            return gp.getGatePassType();

	        case "Status":
	            return gp.getGatePassStatus();  // Block / Approve etc.

	        case "WorkmenType":
	            return gp.getWorkmenType();
	            
	        case "PhysicallyChallenged":
	            return gp.getDisability();
	            
	        case "Proficiency":
	            return gp.getProficiency();
	            
	        default:
	            return null;
	    }
	}
	private java.sql.Date parseDateQuiet(String dateStr) {

	    if (dateStr == null || dateStr.trim().isEmpty()) {
	        return null;
	    }

	    String input = dateStr.trim();

	    // Supported formats
	    String[] patterns = {
	            "dd/MM/yyyy",
	            "dd-MM-yyyy",
	            "yyyy/MM/dd",
	            "yyyy-MM-dd"
	    };

	    for (String pattern : patterns) {

	        try {

	            SimpleDateFormat sdf = new SimpleDateFormat(pattern);

	            // Strict parsing
	            sdf.setLenient(false);

	            // Parse using java.util.Date
	            java.util.Date parsedDate = sdf.parse(input);

	            // Convert to yyyy-MM-dd
	            SimpleDateFormat outputFormat =
	                    new SimpleDateFormat("yyyy-MM-dd");

	            String formattedDate =
	                    outputFormat.format(parsedDate);

	            // Return as java.sql.Date
	            return java.sql.Date.valueOf(formattedDate);

	        } catch (Exception e) {
	            // try next pattern
	        }
	    }

	    return null;
	}
	public String getContractorDetailsForIntraPlantTransferQuery() {
		return QueryFileWatcher.getQuery("GET_CONTRACTOR_DETAILS_FOR_INTRAPLANT_TRANSFER");
	}
	@Override
	public List<String> getContractorDetailsForIntraPlantTransfer(String gatepassNumber) {
		//String sql =getContractorDetailsForCancel();
		//String sql = "SELECT UNITID, CONTRACTORID, DEPARTMENTID,DOJ FROM GATEPASSMAIN WHERE GATEPASSID = ?";
		String sql = getContractorDetailsForIntraPlantTransferQuery();
	    try {
	        return jdbcTemplate.queryForObject(sql, new Object[]{gatepassNumber}, (rs, rowNum) -> {
	            List<String> list = new ArrayList<>();
	            list.add(rs.getString("UNITID"));
	            list.add(rs.getString("CONTRACTORID"));
	            list.add(rs.getString("DEPARTMENTID"));
	            list.add(rs.getString("DOJ"));
	            return list;
	        });
	    } catch (EmptyResultDataAccessException e) {
	        return null;
	    }
	}
	public String getEmployeeIdFromCmspersonQuery() {
		return QueryFileWatcher.getQuery("GET_EMPLOYEEID_FROM_CMSPERSON");
	}
	public String getMaxValidFromCmspersonJobHistQuery() {
		return QueryFileWatcher.getQuery("GET_MAX_VALIDFROM_FROM_JOBHIST");
	}
	@Override
	public String getLastEffectiveFromDateFromJobHist(String gatepassNumber,java.util.Date effectiveFrom) {

	    try {
	        // STEP 1 : GET EMPLOYEE ID
	        //String empSql ="SELECT EMPLOYEEID FROM CMSPERSON WHERE EMPLOYEECODE = ?";
	        String empSql =getEmployeeIdFromCmspersonQuery();
	        Integer employeeId =jdbcTemplate.queryForObject(empSql,Integer.class,gatepassNumber);

	        if (employeeId == null) {
	            return null;
	        }
	        // STEP 2 : GET MAX VALIDFROM
	       // String validSql ="SELECT MAX(VALIDFROM) FROM CMSPERSONJOBHIST WHERE EMPLOYEEID = ? AND VALIDFROM > ?";
	        String validSql =getMaxValidFromCmspersonJobHistQuery();
	        java.sql.Date maxValidFrom =jdbcTemplate.queryForObject(validSql,java.sql.Date.class,employeeId, new java.sql.Date(effectiveFrom.getTime()));

	        return maxValidFrom != null? maxValidFrom.toString(): null;
	    } catch (Exception e) {

	        log.error("Error fetching max VALIDFROM for GatePass: {}",gatepassNumber,e);

	        return null;
	    }
	}
	public String getDataFromGatepassmainForBulkRenew() {
		return QueryFileWatcher.getQuery("GET_DATA_FROM_GATEPASSMAIN_FOR_BULKRENEW");
	}
	public String updateExistingRecordInJobhistForBulkRenew() {
		return QueryFileWatcher.getQuery("UPDATE_EXISTING_RECORD_IN_JOBHIST_FOR_BULKRENEW");
	}
	public String insertNewRecordInJobhistForBulkRenew() {
		return QueryFileWatcher.getQuery("INSERT_NEW_RECORD_IN_JOBHIST_FOR_BULKRENEW");
	}
	private boolean updateCmsPersonJobHistBulkRenew(GatePassMain gpm, long personId) {

	    try {

	        // STEP 1: Get data from GATEPASSMAIN
	        //String fetchSql ="select TradeId,SkillId,DepartmentId,AreaId,UnitId,ContractorId,DOJ,EicId from GATEPASSMAIN where GatePassId=?";
	        String fetchSql =  getDataFromGatepassmainForBulkRenew();     

	        GatePassMain data = jdbcTemplate.queryForObject(
	                fetchSql,
	                new Object[]{gpm.getGatePassId()},
	                (rs, rowNum) -> {

	                    GatePassMain gp = new GatePassMain();
	                    gp.setTrade(rs.getString("TradeId"));
	                    gp.setSkill(rs.getString("SkillId"));
	                    gp.setDepartment(rs.getString("DepartmentId"));
	                    gp.setSubdepartment(rs.getString("AreaId"));
	                    gp.setUnitId(rs.getString("UnitId"));
	                    gp.setContractor(rs.getString("ContractorId"));
	                    gp.setDoj(rs.getString("DOJ"));
	                    gp.setEic(rs.getString("EicId"));
	                    return gp;
	                }
	        );

	        // STEP 2: Expire existing record
	        //String updateSql ="UPDATE CMSPERSONJOBHIST SET VALIDTO = DATEADD(DAY, -1, GETDATE()) WHERE EMPLOYEEID = ? " ;
	        String updateSql =updateExistingRecordInJobhistForBulkRenew();
	               

	        jdbcTemplate.update(updateSql, personId);

	        // STEP 3: Safe DOJ conversion
	        java.sql.Date validFromDate;
	        try {
	            validFromDate = java.sql.Date.valueOf(data.getDoj());
	        } catch (Exception ex) {
	        	log.error("Invalid DOJ format for personId: " + personId);
	            throw new RuntimeException("Invalid DOJ format for personId: " + personId, ex);
	        }
	        
	        // STEP 3: Insert new record
	        String insertSql =insertNewRecordInJobhistForBulkRenew();
//	                "INSERT INTO CMSPERSONJOBHIST ( " +
//	                "EMPLOYEEID, TRADEID,SKILLID,UNITID, CONTRACTORID, DEPARTMENTID, " +
//	                "SUBDEPARTMENTID, WORKORDERID, EICID, VALIDFROM, VALIDTO ,UPDATEDTM) " +
//	                "VALUES (?,?,?,?,?,?,?, ?, ?,?,?,getdate())";

	        int inserted = jdbcTemplate.update(
	                insertSql,
	                personId,
	                data.getTrade(),
	                data.getSkill(),
	                data.getUnitId(),
	                data.getContractor(),
	                data.getDepartment(),
	                data.getSubdepartment(),
	                gpm.getWorkorder(),
	                data.getEic(),
	               // java.sql.Date.valueOf(data.getDoj()),
	                validFromDate,
	                java.sql.Date.valueOf("3000-01-01")
	        );

	        if (inserted == 0) {
	            log.error("Insert failed in CMSPERSONJOBHIST for personid = {}", personId);
	            throw new RuntimeException("Insert failed for personid = " + personId);
	        }

	        return true;

	    } catch (Exception e) {

	        log.error("Error in bulk renew for personid: {}", personId, e);
	        throw new RuntimeException("Failed CMSPERSONJOBHIST bulk renew", e);
	    }
	}
	//same plant doff contractor for intra plant transfer
	public String getAllDeatilsOfWorkmenBasedOnGatePass() {
		return QueryFileWatcher.getQuery("GET_ALL_DETAILS_OF_WORKMEN_BASED_ON_GATEPASS");
	}
	@Override
	public GatePassMain getAllDeatilsOfWorkmenBasedOnGatePass(String gatepassId) {

	    try {
	    	 String sql = getAllDeatilsOfWorkmenBasedOnGatePass();
	       // String sql = "SELECT * FROM GATEPASSMAIN WHERE GATEPASSID = ?";

	        return jdbcTemplate.queryForObject(
	                sql,
	                new Object[]{gatepassId},
	                (rs, rowNum) -> {

	                    GatePassMain gm = new GatePassMain();

	                    gm.setTransactionId(rs.getString("TransactionId"));
	                  //  gm.setGatePassId(rs.getString("GatePassId"));
	                    gm.setGatePassType(rs.getString("GatePassTypeId"));
	                  //  gm.setGatePassStatus(rs.getString("GatePassStatus"));
	                    //gm.setAadhaarNumber(rs.getString("AadharNumber"));
	                    gm.setFirstName(rs.getString("FirstName"));
	                    gm.setLastName(rs.getString("LastName"));
	                    gm.setDateOfBirth(rs.getString("DOB"));
	                    gm.setGender(rs.getString("Gender"));
	                    gm.setRelationName(rs.getString("RelativeName"));
	                    gm.setIdMark(rs.getString("IdMark"));
	                    gm.setMobileNumber(rs.getString("MobileNumber"));
	                    gm.setMaritalStatus(rs.getString("MaritalStatus"));
	                    gm.setTrade(rs.getString("TradeId"));
	                    gm.setSkill(rs.getString("SkillId"));
	                    gm.setNatureOfJob(rs.getString("NatureOfJob"));
	                    //gm.setWcEsicNo(rs.getString("WcEsicNo"));
	                    gm.setHazardousArea(rs.getString("HazardousArea"));
	                    gm.setUanNumber(rs.getString("UanNumber"));
	                    gm.setHealthCheckDate(rs.getString("HealthCheckDate"));
	                    gm.setBloodGroup(rs.getString("BloodGroupId"));
	                    gm.setAccommodation(rs.getString("Accommodation"));
	                    gm.setAcademic(rs.getString("AcademicId"));
	                    gm.setTechnical(rs.getString("Technical"));
	                    gm.setIfscCode(rs.getString("IfscCode"));
	                    gm.setAccountNumber(rs.getString("AccountNumber"));
	                    gm.setEmergencyNumber(rs.getString("EmergencyContactNumber"));
	                    gm.setEmergencyName(rs.getString("EmergencyContactName"));
	                    gm.setPfNumber(rs.getString("PfNumber"));
	                    //gm.setEsicNumber(rs.getString("EsicNumber"));
	                    gm.setDoj(rs.getString("DOJ"));
	                    //gm.setDot(rs.getString("DOT"));
	                    gm.setOnboardingType(rs.getString("OnboardingType"));
                        gm.setPoliceVerificationDate(rs.getString("policeverificationDate"));
                        gm.setAddress(rs.getString("Address"));
                        gm.setAadhaarNumber(rs.getString("AadharNumber"));
                        gm.setAccessArea(rs.getString("AccessAreaId"));
                        gm.setComments(rs.getString("Comments"));
                        gm.setWorkmenType(rs.getString("WorkmenType"));
                        gm.setDisability(rs.getString("disability"));
                        gm.setWageCategory(rs.getString("WorkmenWageCategoryId"));
                        gm.setBonusPayout(rs.getString("BonusPayoutId"));
                        gm.setZone(rs.getString("ZoneId"));
                        gm.setBasic(rs.getBigDecimal("Basic"));
                        gm.setDa(rs.getBigDecimal("DA"));
                        gm.setHra(rs.getBigDecimal("HRA"));
                        gm.setWashingAllowance(rs.getBigDecimal("WashingAllowance"));
                        gm.setOtherAllowance(rs.getBigDecimal("OtherAllowance"));
                        gm.setUniformAllowance(rs.getBigDecimal("UniformAllowance"));
                        gm.setPfCap(rs.getString("PfCap"));
                        gm.setPfApplicable(rs.getString("pfapplicable"));
	                    return gm;
	                });

	    } catch (Exception e) {
	        log.error("Error fetching GatePassMain for GatePassId: {}",gatepassId, e);
	        return null;
	    }
	}
	public String insertIntoTempSamePlantDiffContIntraPlantTransferQuery() {
		return QueryFileWatcher.getQuery("INSERT_INTO_TEMP_SAMEPLANT_DIFFCONT_INTRAPLANT_TRANSFER");
	}
	@Transactional(rollbackFor = Exception.class)
	   @Override
	    public void insertSamePlantDiffContIntraPlantTransfer(GatePassMain gm, String createdBy, String dot){
		try {
			String sql =insertIntoTempSamePlantDiffContIntraPlantTransferQuery(); 
	        //String sql = "INSERT INTO CMSRequestItemIntraPlantTransfer (GatepassId,unitId,contractorId,DepartmentId,AreaId,EICId,workorderId,wcesicId,LLId,Esic,EffectiveDate,Dot,updatedBy) values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
	        jdbcTemplate.update(sql,   gm.getGatePassId(),gm.getUnitId(),gm.getContractor(),gm.getDepartment(),gm.getSubdepartment(),gm.getEic(),gm.getWorkorder(),
	                gm.getWcEsicId(),gm.getLlId(),gm.getEsicNumber(),parseSqlDate(gm.getEffectiveFromDate()), parseSqlDate(dot) , createdBy);
	        updateGatepassMainStatusCancelForIntrPlant(gm, dot,createdBy );
		}catch (Exception e) {
	        log.error("Error in insertIntraPlantTransferTemp", e);
	        throw new RuntimeException("Transaction failed, rolling back", e); //  important
	    }
	    }
	public String updateGatepassMainStatusCancelForIntrPlantQuery() {
		return QueryFileWatcher.getQuery("UPDATE_GATEPASSMAIN_FOR_CANCEL_IN_INTRAPLANT_TRANSFER");
	}
	@Transactional(rollbackFor = Exception.class)
	private void updateGatepassMainStatusCancelForIntrPlant(GatePassMain gm,String dot,String createdBy) {
		// String sql = "update GATEPASSMAIN set GatePassTypeId =?,Reasoning=? ,DOT=DATEADD(DAY, -1, ?)  where GatePassId=?";
		 String sql =updateGatepassMainStatusCancelForIntrPlantQuery();
	    jdbcTemplate.update(sql,GatePassType.CANCEL.getStatus() ,gm.getReasoning(),parseSqlDate(gm.getEffectiveFromDate()),gm.getGatePassId());
	  
	    int cancelled =insertCancelGatepassTransactionMapping(gm,createdBy);
	    if(cancelled==0) {
	    	log.error("insertions failed in gatepassTransactionMapping gatepass: "+gm.getGatePassId());
           throw new RuntimeException("insertions failed in gatepassTransactionMapping gatepass: "+gm.getGatePassId());
	    }
	    boolean result = gatePassActionPersonCancelInsertForIntraPlant(gm,GatePassType.CANCEL.getStatus());
	   if(!result) {
	    	log.error("CMSPERSON TABLE insertions failed while canceling gatepass: "+gm.getGatePassId());
           throw new RuntimeException("CMSPERSON TABLE insertions failed while canceling gatepass: "+gm.getGatePassId());
	    }
	    int row =gatepassmainInsertionForSamePlantDiffCont( gm, dot);
	    if(row==0) {
	    	log.error("gatepassmain insertion failed while creating new gatepass");
            throw new RuntimeException("gatepassmain insertion failed while creating new gatepass");
	    }
	}
	public int insertCancelGatepassTransactionMapping(GatePassMain gm,String createdBy) {
		String transactionId= workmenDao.getNextTransactionId();
		String sql =insertBulkCancelGatepassTransactionMapping();
		//String sql = "INSERT INTO GatePassTransactionMapping (TRANSACTIONID, GATEPASSID, GATEPASSTYPEID, CREATEDDATE) VALUES (?, ?, ?, GETDATE())";
	    int result=jdbcTemplate.update(sql,transactionId, gm.getGatePassId(),GatePassType.CANCEL.getStatus() );
	    if(result==0) {
	    	log.error("gatepassTransactionMapping cancel insertion failed ");
            throw new RuntimeException("gatepassTransactionMapping cancel insertion failed ");
	    }
	    return result;
	}
	
	@Transactional(rollbackFor = Exception.class)
	public boolean gatePassActionPersonCancelInsertForIntraPlant(GatePassMain gpm, String gatePassType) {

		 long personId = workmenDao.getPersonIdFromCmsPerson(gpm.getGatePassId());
	        if (personId <= 0) {
	        	log.error("CMSPERSON employeid not found for gatepass:" + gpm.getGatePassId());
	            throw new RuntimeException("CMSPERSON employeid not found for gatepass:" +gpm.getGatePassId() );
	        }

	    // Step 1: Close existing CUSTDATA rows cancel
	   
        boolean custDataTypeStatusUpdated =this.updateCmsPersonCustDataIntaPlantTransferEffectiveTillStatus(personId,GatePassType.CANCEL.getStatus(),gpm);

        if (!custDataTypeStatusUpdated) {
        	log.error("CMSPERSONCUSTOMDATA Cancel effectivetill update failed");
            throw new RuntimeException("CMSPERSONCUSTOMDATA Cancel effectivetill  update failed");
        }

	    // Step 3: Update StatusMM only if active
	    if (this.isPersonActiveInStatusMM(personId)) {

	        PersonStatusIds ids = this.getPersonStatusIds(personId);

	        if (ids.getActiveId() == null && ids.getInactiveId() == null) {
	        	
	        	log.error("PersonStatusMM IDs not found for PersonId : {}", personId);
                throw new RuntimeException("PersonStatusMM IDs not found");
	        }
	            boolean statusUpdated =this.updatePersonStatusValidity(ids.getActiveId(), ids.getInactiveId(),gpm.getEffectiveFromDate());
	            if (!statusUpdated) {
                	log.error("CMSPERSONSTATUSMM update failed for canceling record :"+ gpm.getGatePassId() );
                    throw new RuntimeException("CMSPERSONSTATUSMM update failed for canceling record :"+ gpm.getGatePassId() );
                }
            }
           try {
        	   //gpm.setGatePassType(GatePassType.CANCEL.getStatus());
              String wfdIntegration = this.getWFDIntegration();
			 if("yes".equalsIgnoreCase(wfdIntegration)) {
	             //if(gpm.getGatePassType().equals(GatePassType.CANCEL.getStatus())){
			        api.updateEmpStatusTerOrAct(gpm.getGatePassId(),EmployeeStatusType.CANCEL);
	             }
			   //}
			 }catch(Exception e) {
				 log.info(e.getMessage());
				 throw new RuntimeException("Cancel Api Integration Failed");
			 }
	    
	    return true;
	 }
	public String updateValidtoYesterday() {
		return QueryFileWatcher.getQuery("UPDATE_ACTIVE_VALID_TO_YESTERDAY");
	}
	public String updateValidfromToday() {
		return QueryFileWatcher.getQuery("UPDATE_INACTIVE_VALID_FROM_TODAY");
	}
//	public boolean updatePersonStatusValidity(Long activeId, Long inactiveId) {
//
//	    boolean updated = false;
//	    // Update active record → VALIDTO = yesterday
//	    if (activeId != null) {
//	    	 String sqlActive = updateValidtoYesterday() ;
//
//	        int count1 = jdbcTemplate.update(sqlActive, activeId);
//	        updated = updated || count1 > 0;
//	    }
//
//	    // Update inactive record → VALIDFROM = today
//	    if (inactiveId != null) {
//	    	 String sqlInactive = updateValidfromToday() ;
//
//	        int count2 = jdbcTemplate.update(sqlInactive, inactiveId);
//	        updated = updated || count2 > 0;
//	    }
//
//	    return updated;
//	}
	public String updatePersonstatusmmForActive() {
		return QueryFileWatcher.getQuery("UPDATE_CMSPERSONSTATUSMM_ACTIVE_RECORD");
	}
	public String updatePersonstatusmmForInactive() {
		return QueryFileWatcher.getQuery("UPDATE_CMSPERSONSTATUSMM_INACTIVE_RECORD");
	}
	public boolean updatePersonStatusValidity(Long activeId,Long inactiveId,String effectiveFromDate) {

	    boolean updated = false;

	    try {

	        // ACTIVE record:
	        // VALIDTO = effectiveFromDate - 1 day
	        if (activeId != null) {
	        	String sqlActive = updatePersonstatusmmForActive();
	            //String sqlActive ="UPDATE CMSPERSONSTATUSMM SET VALIDTO = DATEADD(DAY, -1, ?) WHERE PERSONSTATUSMMID = ?";

	            int count1 = jdbcTemplate.update(sqlActive,parseSqlDate(effectiveFromDate),activeId);

	            updated =  count1 > 0;
	        }

	        // INACTIVE record:
	        // VALIDFROM = effectiveFromDate
	        if (inactiveId != null) {
	        	 String sqlInactive =updatePersonstatusmmForInactive();
	            //String sqlInactive ="UPDATE CMSPERSONSTATUSMM SET VALIDFROM = ? WHERE PERSONSTATUSMMID = ?";

	            int count2 = jdbcTemplate.update(sqlInactive,parseSqlDate(effectiveFromDate),inactiveId);

	            updated = count2 > 0;
	        }

	        return updated;

	    } catch (Exception e) {

	        log.error("Error updating CMSPERSONSTATUSMM validity dates while canceling the record", e);

	        throw new RuntimeException("Failed to update CMSPERSONSTATUSMM validity dates while canceling the record",e);
	    }
	}
	public String gatepassmainInsertionForSamePlantDiffContQuery() {
		return QueryFileWatcher.getQuery("GATEPASSMAIN_INSERTION_FOR_PLANT_DIFF_CONT");
	}
	@Transactional(rollbackFor = Exception.class)
	public int gatepassmainInsertionForSamePlantDiffCont(GatePassMain gm, String dot) {
		 int result = 0;

		    try {
		String gatePassId = this.generateGatePassId();
		String transactionId=getNextTransactionId();
		//String gatepassTypeId=GatePassType.CREATE.getStatus();
		String gatepassStatus=GatePassStatus.APPROVED.getStatus();
		gm.setGatePassId(gatePassId);
		gm.setTransactionId(transactionId);
		//gm.setGatePassType(gatepassTypeId);

	    String query =gatepassmainInsertionForSamePlantDiffContQuery();
//	            "INSERT INTO GATEPASSMAIN (TransactionId,GatePassId, GatePassTypeId, GatePassStatus, AadharNumber, FirstName, LastName, "
//	            + "DOB, Gender, RelativeName, IdMark, MobileNumber , MaritalStatus , UnitId, ContractorId, WorkorderId, TradeId, SkillId, "
//	            + "DepartmentId, AreaId, EicId, NatureOfJob, WcEsicNo, HazardousArea, AccessAreaId, UanNumber, HealthCheckDate,pfnumber,esicNumber,"
//	            + " BloodGroupId, Accommodation, AcademicId, Technical, IfscCode, AccountNumber,EmergencyContactName,EmergencyContactNumber, "
//	            + "WorkmenWageCategoryId, BonusPayoutId, PfCap,ZoneId, Basic, DA, HRA, WashingAllowance, OtherAllowance, UniformAllowance,AadharDocName, "
//	            + "PhotoName, BankDocName, PoliceVerificationDocName, IdProof2DocName, MedicalDocName, EducationDocName, Form11DocName, TrainingDocName, "
//	            + "OtherDocName,WorkFlowType,Comments,Address,DOJ,pfapplicable,policeverificationDate, DOT,UpdatedBy, UpdatedDate,OnboardingType,LLNo,"
//	            + "AppointmentDocName,disability,WorkmenType,Proficiency) VALUES  (?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  "
//	            + "?, ?, ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,  ?, ?, ?, ?, ?,?,?,?,?,?,?,?, GETDATE(),?,?,?,?,?,?)";
	    result = jdbcTemplate.update(query,transactionId,gatePassId,gm.getGatePassType(),gatepassStatus,gm.getAadhaarNumber(),gm.getFirstName(),gm.getLastName(),
	            gm.getDateOfBirth(),gm.getGender(),gm.getRelationName(),gm.getIdMark(),gm.getMobileNumber(),gm.getMaritalStatus(),gm.getUnitId(),gm.getContractor(),gm.getWorkorder(),gm.getTrade(),gm.getSkill(),
	            gm.getDepartment(),gm.getSubdepartment(),gm.getEic(),gm.getNatureOfJob(),gm.getWcEsicNo(),gm.getHazardousArea(),gm.getAccessArea(),gm.getUanNumber(),gm.getHealthCheckDate(),gm.getPfNumber(),gm.getEsicNumber(),
	            gm.getBloodGroup(),gm.getAccommodation(),gm.getAcademic(),gm.getTechnical(),gm.getIfscCode(),gm.getAccountNumber(),gm.getEmergencyName(),gm.getEmergencyNumber(),
	            gm.getWageCategory(),gm.getBonusPayout(),gm.getPfCap(),gm.getZone(),gm.getBasic(),gm.getDa(),gm.getHra(),gm.getWashingAllowance(),gm.getOtherAllowance(),gm.getUniformAllowance(),gm.getAadharDocName() !=null?gm.getAadharDocName():"",
	            gm.getPhotoName() !=null?gm.getPhotoName():"",gm.getBankDocName()!=null?gm.getBankDocName():"",gm.getPoliceVerificationDocName()!=null?gm.getBankDocName():"",gm.getIdProof2DocName()!=null?gm.getBankDocName():"",
	            gm.getMedicalDocName()!=null?gm.getBankDocName():"",gm.getEducationDocName()!=null?gm.getBankDocName():"",gm.getForm11DocName()!=null?gm.getBankDocName():"",gm.getTrainingDocName()!=null?gm.getBankDocName():"",
	            gm.getOtherDocName()!=null?gm.getBankDocName():"",gm.getWorkFlowType(),gm.getComments(),gm.getAddress(),gm.getEffectiveFromDate(),gm.getPfApplicable(),gm.getPoliceVerificationDate(),
	            dot,gm.getCreatedBy(),gm.getOnboardingType(),gm.getLlNo(),gm.getAppointmentDocName()!=null?gm.getAppointmentDocName():"",gm.getDisability(),gm.getWorkmenType(),gm.getProficiency()!=null?gm.getProficiency():"");

    // log.info("GatePassMain inserted successfully.");
   //  log.info("Rows affected : {}", result);
     
     boolean row = gatePassActionPersonCreateInsertForIntraPlant(gm,dot);
     if(!row) {
	    	log.error("PERSON TABLES insertions failed while creating gatepass: "+gm.getGatePassId());
      throw new RuntimeException("PERSON TABLES insertions failed while canceling gatepass: "+gm.getGatePassId());
	    }
     
     try {
     	String wfdIntegration = this.getWFDIntegration();
     	if("yes".equalsIgnoreCase(wfdIntegration)) {
     		api.addOnBoardingDetailsActual(gm.getTransactionId());
     	}
     	}catch(Exception e) {
     		log.info(e.getMessage());
			 throw new RuntimeException("create Api Integration Failed");
     	}
     
   } catch (Exception e) {

     log.error("Error occurred while inserting GatePassMain" + gm.getGatePassId(), e);
     throw new RuntimeException("Error occurred while inserting GatePassMain" + gm.getGatePassId(), e);

  }

    return result;
 }
	
	@Transactional(rollbackFor = Exception.class)
	public boolean gatePassActionPersonCreateInsertForIntraPlant(GatePassMain gpm,String dot) {
    
	    long personId = saveIntoCMSPerson(gpm,dot);
	    if (personId <= 0) {
	        throw new RuntimeException("CMSPERSON insert failed");
	    }

	    boolean jobHist = saveIntoCMSPERSONJOBHIST(gpm, personId);

	    if (!jobHist) {
	        throw new RuntimeException("CMSPERSONJOBHIST insert failed");
	    }

	    boolean status =saveCMSPERSONSTATUSMM(gpm, personId,dot);

	    if (!status) {
	        throw new RuntimeException("CMSPERSONSTATUSMM insert failed");
	    }

	    gpm.setGatePassStatus(GatePassStatus.APPROVED.getStatus());

	    boolean custom =this.updateCmsPersonCustDataIntaPlantTransfer(personId,gpm,dot);

	    if (!custom) {
	        throw new RuntimeException("CMSPERSONCUSTOMDATA insert failed");
	    }

	    return true;
 }
	@Transactional(rollbackFor = Exception.class)
	public long saveIntoCMSPerson(GatePassMain gpm,String dot) {
		
		CMSPerson person = new CMSPerson();
		person.setEmployeeCode(gpm.getGatePassId());
		person.setFirstName(gpm.getFirstName());
		person.setLastName(gpm.getLastName());
		person.setRelationName(gpm.getRelationName());
		person.setDateOfBirth(gpm.getDateOfBirth());
		person.setDateOfJoining(gpm.getEffectiveFromDate());
		person.setDateOfTermination(dot!=null?dot:"");
		//person.setBloodGroup(Integer.parseInt(gpm.getBloodGroup()));
		person.setBloodGroup(gpm.getBloodGroup() != null && !gpm.getBloodGroup().trim().isEmpty()? Integer.parseInt(gpm.getBloodGroup()): 0);

		person.setHazardousArea(gpm.getHazardousArea());
		person.setGender(Integer.parseInt(gpm.getGender()));
		person.setAcademics(gpm.getAcademic() != null && !gpm.getAcademic().trim().isEmpty()? Integer.parseInt(gpm.getAcademic()): 0);
		person.setAccomodation(gpm.getAccommodation() != null && gpm.getAccommodation().trim().equalsIgnoreCase("Yes") ? 1 : 0);
		person.setBankBranch(gpm.getIfscCode());
		person.setAccountNo(gpm.getAccountNumber() != null && !gpm.getAccountNumber().trim().isEmpty()? gpm.getAccountNumber(): " ");
		person.setEmergencyName(gpm.getEmergencyName());
		person.setEmergencyNumber(gpm.getEmergencyNumber());
		person.setMobileNumber(gpm.getMobileNumber());
		//person.setAccessLevel(Integer.parseInt(gpm.getAccessArea()));
		person.setAccessLevel(gpm.getAccessArea() != null && !gpm.getAccessArea().trim().isEmpty()? Integer.parseInt(gpm.getAccessArea()): 0);
		person.setEsicNumber(gpm.getEsicNumber());
		person.setUanNumber(gpm.getUanNumber()!=null?gpm.getUanNumber():" ");
		person.setIsPfEligible(gpm.getPfApplicable().equals("Yes")?1:0);
		person.setIdMark(gpm.getIdMark()!=null?gpm.getIdMark():" ");
		person.setPanNumber(gpm.getPfNumber()!=null?gpm.getPfNumber():" ");
		person.setAadharNumber(gpm.getAadhaarNumber());
		person.setUpdatedBy(gpm.getCreatedBy());
		
		return this.saveIntoCMSPerson(person);
	}
	
	public String saveIntoCMSPerson() {
		return QueryFileWatcher.getQuery("SAVE_CMSPERSON");
	}
	
	@Transactional(rollbackFor = Exception.class)
	public long saveIntoCMSPerson(CMSPerson person) {
		String sql= saveIntoCMSPerson();
	    KeyHolder keyHolder = new GeneratedKeyHolder();
	    Object terminationValue = 
	            (person.getDateOfTermination() == null || person.getDateOfTermination().toString().trim().isEmpty())
	                    ? " " : person.getDateOfTermination();
	    
	    Object[] parameters = new Object[]{
	        person.getEmployeeCode(),
	        person.getFirstName(),
	        person.getRelationName(),
	        person.getLastName(),
	        person.getDateOfBirth(),
	        parseSqlDate(person.getDateOfJoining()),
	        //parseSqlDate(person.getDateOfTermination()),
	        terminationValue,
	        person.getBloodGroup(),
	        person.getHazardousArea(),
	        person.getGender(),
	        person.getAcademics(),
	        person.getAccomodation(),
	        person.getBankBranch(),
	        person.getAccountNo(),
	        person.getEmergencyName(),
	        person.getEmergencyNumber(),
	        person.getMobileNumber(),
	        person.getAccessLevel(),
	        person.getEsicNumber(),
	        person.getUanNumber(),
	        person.getIsPfEligible(),
	        person.getIdMark(),
	        person.getPanNumber(),
	        person.getUpdatedBy(),
	        person.getAadharNumber(),
	        
	    };

	    try {
	        jdbcTemplate.update(connection -> {
	            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
	            for (int i = 0; i < parameters.length; i++) {
	                ps.setObject(i + 1, parameters[i]);
	            }
	            return ps;
	        }, keyHolder);

	        Number generatedId = keyHolder.getKey();
	        if (generatedId != null) {
	            return generatedId.longValue();  // return EMPLOYEEID
	        } else {
	            log.warn("Insert succeeded but no EMPLOYEEID returned for EmployeeCode: " + person.getEmployeeCode());
	            throw new RuntimeException("Insert succeeded but no EMPLOYEEID returned for EmployeeCode: " + person.getEmployeeCode());
	        }
	    } catch (Exception e) {
	        log.error("Error inserting into CMSPerson for EmployeeCode: " + person.getEmployeeCode(), e);
	        throw new RuntimeException("Error inserting into CMSPerson for EmployeeCode: " + person.getEmployeeCode(), e);
	    }
	}
	public String saveIntoCMSPERSONJOBHIST() {
		return QueryFileWatcher.getQuery("SAVE_CMSPERSONJOBHIST");
	}
	@Transactional(rollbackFor = Exception.class)
	public boolean saveIntoCMSPERSONJOBHIST(GatePassMain gpm, long employeeId) {
		boolean result = false;
		String sql = saveIntoCMSPERSONJOBHIST();
		// String sql = "INSERT INTO CMSPERSONJOBHIST ( EMPLOYEEID , TRADEID , SKILLID , UNITID , CONTRACTORID , DEPARTMENTID , "
		// 		+ " SUBDEPARTMENTID , WORKORDERID , EICID , VALIDFROM , VALIDTO  ) "
		// 		+ "     VALUES (?,?,?,?,?,?,?,?,?,?,?)";
	     Object[] parameters = new Object[] {employeeId,gpm.getTrade(),gpm.getSkill(),gpm.getUnitId(),gpm.getContractor(),gpm.getDepartment(),
	    		 gpm.getSubdepartment(),gpm.getWorkorder(),gpm.getEic(),parseSqlDate(gpm.getEffectiveFromDate()), "1/1/3000"};
	     try {
	     int status = jdbcTemplate.update(sql, parameters);
	     if (status > 0) {
	     	result=true;
	     }else {
	         log.warn("Failed to insert CMSPERSONJOBHIST action for GatePassId: " + gpm.getGatePassId());
	         throw new RuntimeException("Failed to insert CMSPERSONJOBHIST action for GatePassId: " + gpm.getGatePassId());
	     }
	     }catch (Exception e) {
	         log.error("Error insert CMSPERSONJOBHIST action for GatePassId: " + gpm.getGatePassId(), e);
	         throw new RuntimeException("Error insert CMSPERSONJOBHIST action for GatePassId: " + gpm.getGatePassId(), e);
	     }
	     return result;
	}
	
	public String saveCMSPERSONSTATUSMM() {
		return QueryFileWatcher.getQuery("SAVE_CMSPERSONSTATUSMM");
	}
	
	@Transactional(rollbackFor = Exception.class)
	public boolean saveCMSPERSONSTATUSMM(GatePassMain gpm, long employeeId, String dot) {
		
		boolean result = false;
		String sql = saveCMSPERSONSTATUSMM();
		if(dot==null) {
			throw new RuntimeException("DOT IS NULL for GatePassId: " + gpm.getGatePassId()); 
		}
		// String sql = "INSERT INTO CMSPERSONSTATUSMM ( EMPLOYEEID , ISACTIVE , VALIDFROM , VALIDTO)  VALUES (?,?,?,? )";
	    Object[] parameters = new Object[] {employeeId,1,parseSqlDate(gpm.getEffectiveFromDate()),dot};
	    try {
	    int status = jdbcTemplate.update(sql, parameters);
	    if (status > 0) {
	    	boolean r = saveCMSPERSONSTATUSMMTerminated(gpm,employeeId,dot);
	    	result=r;
	    }else {
	        log.warn("Failed to insert active record statusmm for GatePassId: " + gpm.getGatePassId());
	        throw new RuntimeException("Failed to insert active record statusmm  for GatePassId: " + gpm.getGatePassId());
	    }
	    }catch (Exception e) {
	        log.error("Failed to insert active record statusmm  for GatePassId: " + gpm.getGatePassId(), e);
	        throw new RuntimeException("Failed to insert active record statusmm  for GatePassId: " + gpm.getGatePassId(), e);
	    }
	    return result;
	}
	
	public String saveCMSPERSONSTATUSMMTerminated() {
		return QueryFileWatcher.getQuery("SAVE_CMSPERSONSTATUSMM_INACTIVE");
	}
	
	public boolean saveCMSPERSONSTATUSMMTerminated(GatePassMain gpm, long employeeId,String Newdot) {
		
		String dot = Newdot;  // e.g. "2025-12-31"
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		LocalDate date = LocalDate.parse(dot, formatter);
		LocalDate nextDate = date.plusDays(1);
		String newDot = nextDate.format(formatter);
		gpm.setNewDot(newDot);
		
		boolean result = false;
		String sql = saveCMSPERSONSTATUSMMTerminated();
		// String sql = "INSERT INTO CMSPERSONSTATUSMM ( EMPLOYEEID , ISACTIVE , VALIDFROM , VALIDTO)  VALUES (?,?,?,? )";
	    Object[] parameters = new Object[] {employeeId,0,gpm.getNewDot(),"1/1/3000"};
	    try {
	    int status = jdbcTemplate.update(sql, parameters);
	    if (status > 0) {
	    	result=true;
	    }else {
	        log.warn("Failed to insert inactive record statusmm for GatePassId: " + gpm.getGatePassId());
	        throw new RuntimeException("Failed to insert inactive record statusmm for GatePassId: " + gpm.getGatePassId());
	    }
	    }catch (Exception e) {
	        log.error("Failed to insert inactive record statusmm for GatePassId: " + gpm.getGatePassId(), e);
	        throw new RuntimeException("Failed to insert inactive record statusmm for GatePassId: " + gpm.getGatePassId(), e);
	    }
	    return result;
	}
	public String getActiveCustomDefintion() {
		return QueryFileWatcher.getQuery("GET_ACTIVE_CUSTOM_DEFINITIONS");
	}
	@Transactional(rollbackFor = Exception.class)
	public boolean updateCmsPersonCustDataIntaPlantTransfer(Long  personId, GatePassMain gpm,String dot){
	      try {
	    	  
	          
		    String sql = saveCMSPERSONCUSTDATA(); 

		    // Fetch all active custom definitions
		    //String defSql = "SELECT CSTMDEFID, CSTMDEFNAME FROM CMSPERSONCUSTOMDATADEFINITION WHERE ISACTIVE = 1";
		    String defSql =getActiveCustomDefintion();
		    List<Map<String, Object>> defList = jdbcTemplate.queryForList(defSql);


		    List<Object[]> batchArgs = new ArrayList<>();

		    for (Map<String, Object> def : defList) {

		        int defId = (Integer) def.get("CSTMDEFID");
		        String fieldName = (String) def.get("CSTMDEFNAME");

		        String value = mapGatePassValue(fieldName, gpm);

		        // Skip null/empty values
		        if (value == null || value.trim().isEmpty()) {
		            continue;
		        }
	          
		        // ✅ Set EFFECTIVETILL conditionally
		        Object effectiveTill = "GatePassType".equalsIgnoreCase(fieldName)
		                ? dot              // only GatePassType gets DOT
		                : "3000-01-01";           // others get default

		        batchArgs.add(new Object[]{
		        		personId,        // ?
		                defId,             // ?
		                value,             // ?
		                effectiveTill,     // ? (EFFECTIVETILL)
		                gpm.getCreatedBy()  // ?
		        });
		    }

		    if (batchArgs.isEmpty()) {
		          log.error("No custom data found to insert for PersonId : {}", personId);
		    	return false; // nothing to insert
		    }

		    int[] result = jdbcTemplate.batchUpdate(sql, batchArgs);

		    for (int count : result) {

	            if (count <= 0) {

	                log.error("Batch insert failed for PersonId : {}", personId);

	                throw new RuntimeException("Failed to insert CMSPERSONCUSTOMDATA");
	            }
	        }

	        log.info("CMSPERSONCUSTOMDATA inserted successfully for PersonId : {}", personId);

	      return true; // records inserted
	      } catch (Exception e) {

	          log.error("Error while inserting CMSPERSONCUSTOMDATA for PersonId : {}",personId,e);

	          // Rethrow so parent transaction rolls back
	          throw new RuntimeException("CMSPERSONCUSTOMDATA insert failed",e);
	      }
		}
	@Override
	public boolean checkTrainingDetailsExists(Integer unitId, String department,String trainingType,String trainingName) {
		//String sql=trioexistsMapping();
		String sql = "select count(*) from CMSTRAININGDETAILS where UNIT_ID=? and DEP_NAME=? and TRAINING_TYPE=? and TRAINING_NAME=?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, unitId, department,trainingType,trainingName);
        return count != null && count > 0;
	}
	@Override 
	public void saveTrainingDetails( Integer unitId, String department, String trainingType, String trainingName) { 
		String sql = "INSERT INTO CMSTRAININGDETAILS (UNIT_ID, DEP_NAME, TRAINING_TYPE, TRAINING_NAME,MANDATORY) VALUES (?, ?, ?, ?,'YES')";
		jdbcTemplate.update( sql, unitId, department, trainingType,trainingName );
		}
}