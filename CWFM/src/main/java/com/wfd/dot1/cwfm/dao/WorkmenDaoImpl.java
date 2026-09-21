package com.wfd.dot1.cwfm.dao;

import java.io.File;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.wfd.dot1.cwfm.dto.AadharCheckDto;
import com.wfd.dot1.cwfm.dto.ApproveRejectGatePassDto;
import com.wfd.dot1.cwfm.dto.ApproverStatusDTO;
import com.wfd.dot1.cwfm.dto.CMSPerson;
import com.wfd.dot1.cwfm.dto.ContractWorkmenReportDTO;
import com.wfd.dot1.cwfm.dto.GatePassActionDto;
import com.wfd.dot1.cwfm.dto.GatePassListingDto;
import com.wfd.dot1.cwfm.dto.GatePassStatusLogDto;
import com.wfd.dot1.cwfm.dto.PersonStatusIds;
import com.wfd.dot1.cwfm.enums.GatePassStatus;
import com.wfd.dot1.cwfm.enums.GatePassType;
import com.wfd.dot1.cwfm.enums.WorkFlowType;
import com.wfd.dot1.cwfm.pojo.ApprovalStatus;
import com.wfd.dot1.cwfm.pojo.ApproverInfo;
import com.wfd.dot1.cwfm.pojo.CmsContractorWC;
import com.wfd.dot1.cwfm.pojo.CmsGeneralMaster;
import com.wfd.dot1.cwfm.pojo.ContractWorkmenExportDto;
import com.wfd.dot1.cwfm.pojo.Contractor;
import com.wfd.dot1.cwfm.pojo.DeptMapping;
import com.wfd.dot1.cwfm.pojo.GatePassMain;
import com.wfd.dot1.cwfm.pojo.MasterUser;
import com.wfd.dot1.cwfm.pojo.PersonOrgLevel;
import com.wfd.dot1.cwfm.pojo.PrincipalEmployer;
import com.wfd.dot1.cwfm.pojo.Skill;
import com.wfd.dot1.cwfm.pojo.Trade;
import com.wfd.dot1.cwfm.pojo.WageDetailsDto;
import com.wfd.dot1.cwfm.pojo.Workorder;
import com.wfd.dot1.cwfm.pojo.Zone;
import com.wfd.dot1.cwfm.queries.WorkmenQueryBank;
import com.wfd.dot1.cwfm.util.QueryFileWatcher;

@Repository
public class WorkmenDaoImpl implements WorkmenDao{

	private static final Logger log = LoggerFactory.getLogger(WorkmenDaoImpl.class.getName());
	
	 @Autowired
	 private JdbcTemplate jdbcTemplate;
	 
	
	 
	 public String getAllPes() {
		    return QueryFileWatcher.getQuery("GET_ALL_PES");
		}
	 
	 public String getAllContractorBasedOnPE() {
		    return QueryFileWatcher.getQuery("GET_ALL_CONTRACTOR_BY_PE");
		}
	 
	 
	 public String getAllSkillQuery() {
		    return QueryFileWatcher.getQuery("GET_ALL_SKILL");
		}
	 
	
	 public String getAllGeneralMaster() {
		    return QueryFileWatcher.getQuery("GET_ALL_CMSGENERALMASTER");
		}
	 
	 public String getAllTradesByPe() {
		    return QueryFileWatcher.getQuery("GET_ALL_TRADES_BY_PE");
		}
	 
	 public String getAllWoByPeAndCont() {
		    return QueryFileWatcher.getQuery("GET_ALL_WORKORDER_BY_PE_AND_CONT");
		}
	 
	 public String getAllWc() {
		    return QueryFileWatcher.getQuery("GET_ALL_WC");
		}
	 
	 public String getWorkflowType() {
		    return QueryFileWatcher.getQuery("GET_WORKFLOW_TYPE_BY_PE");
		}
	 
	 public String getWorkflowTypeNew() {
		    return QueryFileWatcher.getQuery("GET_WORKFLOW_TYPE_BY_PE_ACTIONID");
		}
	 
	 public String getDotType() {
		    return QueryFileWatcher.getQuery("GET_DOT_TYPE_BY_PE");
		}
	 
	 public String getAllGatePassForContractor() {
		    return QueryFileWatcher.getQuery("GET_ALL_GATE_PASS_FOR_CREATOR");
		}
	 
	 public String getAllGatePassForSquential() {
		    return QueryFileWatcher.getQuery("GET_ALL_GATE_PASS_FOR_SEQUENTIAL_APPROVER");
		}
	 
	 public String getAllGatePassForParallel() {
		    return QueryFileWatcher.getQuery("GET_ALL_GATE_PASS_FOR_PARALLEL_APPROVER");
		}
	 public String getApproverHierarchy() {
		    return QueryFileWatcher.getQuery("GET_APPROVER_INFO_BY_GPTID");
		}
	 public  String getApprovalStatusOfGatePass() {
		 return QueryFileWatcher.getQuery("GET_APPROVAL_STATUS_BY_TRNSID");
		}
	 public String getGatePassActionListingDetailsQuery() {
		 return QueryFileWatcher.getQuery("GET_ALL_GATE_PASS_ACTION_FOR_CREATOR");
	 }
	 public String getCancelActionListingDetailsQuery() {
		 return QueryFileWatcher.getQuery("GET_ALL_CANCEL_ACTION_FOR_CREATOR");
	 }
	 public String getAllEicManagerQuery() {
		 return QueryFileWatcher.getQuery("GET_ALL_EIC");
	 }
	 
	 public String getLastApproverQuery() {
		 return QueryFileWatcher.getQuery("LAST_APPROVER");
	 }
	 
	 
	 public String getContrForAdmin() {
		 return QueryFileWatcher.getQuery("GET_ALL_CONTR_FOR_ADMIN");
	 }
	 
	 public String getTransactionIdExistsQuery() {
		 return  QueryFileWatcher.getQuery("TRANSACTION_ID_EXISTS");
	 }
	 
	 public String getMaxGatePassIdQuery() {
		 return QueryFileWatcher.getQuery("GET_NEXT_GATEPASSID_SEQ");
	 }
	 
	 public String getAllRenewForContractor() {
		 return QueryFileWatcher.getQuery("GET_ALL_RENEW_FOR_CREATOR");
	 }
	 
	 public String getAllGatePassActionForSquential() {
		    return QueryFileWatcher.getQuery("GET_ALL_GATE_PASS_ACTION_FOR_SEQUENTIAL_APPROVER");
		}
	 
	 public String getAllGatePassActionForParallel() {
		    return QueryFileWatcher.getQuery("GET_ALL_GATE_PASS_ACTION_FOR_PARALLEL_APPROVER");
		}
	 
	 public String getWageMasterExportData() {
		    return QueryFileWatcher.getQuery("GET_WAGE_DETAILS_EXPORT_DATA");
		}
	 public String getAllSkillsByPeandTrade() {
		    return QueryFileWatcher.getQuery("GET_ALL_SKILLS_BY_PEID_AND_TRADEID");
		}
	 public String getAllDepartmentsOnPE() {
		    return QueryFileWatcher.getQuery("GET_ALL_DEPARTMENT_BY_PEID");
		}
	 public String getAllSubDepartments() {
		    return QueryFileWatcher.getQuery("GET_ALL_SUBDEPARTMENT_BY_PEID_DEPID");
		}
	 public String getGatePassUnblockDeblackListingDetailsQuery() {
		 return QueryFileWatcher.getQuery("GET_ALL_GATE_PASS_UNBLOCK_DEBLACK_FOR_CREATOR");
	 }
	 public String getaadharUniqueQuery() {
		 return QueryFileWatcher.getQuery("GET_ALL_AADHAR_UNIQUE");
	 }
	@Override
	public List<PrincipalEmployer> getAllPrincipalEmployer(String userAccount) {
		log.info("Entering into getAllPrincipalEmployer dao method "+userAccount);
		List<PrincipalEmployer> peList= new ArrayList<PrincipalEmployer>();
		String query=getAllPes();
		log.info("Query to getAllPrincipalEmployer "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query, userAccount);
		while(rs.next()) {
			PrincipalEmployer pe = new PrincipalEmployer();
			pe.setUnitId(rs.getInt("UNITID"));
			pe.setName(rs.getString("NAME"));
			peList.add(pe);
		}
		log.info("Exiting from getAllPrincipalEmployer dao method "+peList.size());
		return peList;
	}

	@Override
	public List<Contractor> getAllContractorBasedOnPE(String unitId,String userAccount) {
		log.info("Entering into getAllContractorBasedOnPE dao method "+unitId+" "+userAccount);
		List<Contractor> contList= new ArrayList<Contractor>();
		String query=getAllContractorBasedOnPE();
		log.info("Query to getAllContractorBasedOnPE "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,userAccount,unitId);
		while(rs.next()) {
			Contractor cont = new Contractor();
			cont.setContractorId(rs.getString("contractorid"));
			cont.setContractorName(rs.getString("contractorname"));
			cont.setUnitId(unitId);
			cont.setContractorCode(rs.getString("contractorcode"));
			cont.setContractorAddress(rs.getString("contractoraddress"));
			contList.add(cont);
		}
		log.info("Exiting from getAllContractorBasedOnPE dao method "+contList.size());
		return contList;
	}

	@Override
	public List<Workorder> getAllWorkordersBasedOnPEAndContractor(String unitId, String contractorId) {
		log.info("Entering into getAllWorkordersBasedOnPEAndContractor dao method "+unitId+" "+contractorId);
		List<Workorder> woList= new ArrayList<Workorder>();
		String query=getAllWoByPeAndCont();
		log.info("Query to getAllWorkordersBasedOnPEAndContractor "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId, contractorId);
		while(rs.next()) {
			Workorder wo = new Workorder();
			wo.setWorkorderId(rs.getString("WORKORDERID"));
			wo.setName(rs.getString("NAME"));
			wo.setValidFrom(rs.getString("VALIDFROM"));
			wo.setValidTo(rs.getString("VALIDDT"));
			woList.add(wo);
		}
		log.info("Exiting from getAllWorkordersBasedOnPEAndContractor dao method "+woList.size());
		return woList;
	}

	@Override
	public List<Trade> getAllTradesBasedOnPE(String unitId) {
		log.info("Entering into getAllTradesBasedOnPE dao method "+unitId);
		List<Trade> tradeList= new ArrayList<Trade>();
		String query = getAllTradesByPe();
		log.info("Query to getAllTradesBasedOnPE "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId);
		while(rs.next()) {
			Trade trade = new Trade();
			trade.setTradeId(rs.getString("TRADEID"));
			trade.setTradeName(rs.getString("NAME"));
			tradeList.add(trade);
		}
		log.info("Exiting from getAllTradesBasedOnPE dao method "+tradeList.size());
		return tradeList;
	}

	@Override
	public List<Skill> getAllSkill() {
		log.info("Entering into getAllSkill dao method ");
		List<Skill> skillList= new ArrayList<Skill>();
		String query = getAllSkillQuery();
		log.info("Query to getAllSkill "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query);
		while(rs.next()) {
			Skill skill = new Skill();
			skill.setSkillId(rs.getString("SKILLID"));
			skill.setSkillName(rs.getString("SKILLNM"));
			skill.setStatus(rs.getInt("ISACTIVE"));
			skillList.add(skill);
		}
		log.info("Exiting from getAllSkill dao method "+skillList.size());
		return skillList;
	}

	@Override
	public List<CmsGeneralMaster> getAllDepartmentAndSubDepartment(String userId) {
		log.info("Entering into getAllDepartmentAndSubDepartment dao method ");
		List<CmsGeneralMaster> gmList= new ArrayList<CmsGeneralMaster>();
		log.info("Query to getAllDepartmentAndSubDepartment "+WorkmenQueryBank.GET_ALL_DEPT_AND_SUBDEPT);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(WorkmenQueryBank.GET_ALL_DEPT_AND_SUBDEPT,userId);
		while(rs.next()) {
			CmsGeneralMaster gm = new CmsGeneralMaster();
			gm.setGmId(rs.getString("GMID"));
			gm.setGmName(rs.getString("GMNAME"));
			gm.setAuthorizationOn(rs.getString("AuthorizationOn"));
			gmList.add(gm);
		}
		log.info("Exiting from getAllDepartmentAndSubDepartment dao method "+gmList.size());
		return gmList;
	}

	@Override
	public List<MasterUser> getAllEicManager(String unitId,String deptId) {
		log.info("Entering into getAllEicManager dao method ");
		List<MasterUser> eicList= new ArrayList<MasterUser>();
		//log.info("Query to getAllEicManager "+WorkmenQueryBank.GET_ALL_EIC);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(this.getAllEicManagerQuery(),deptId,unitId);
		while(rs.next()) {
			MasterUser mu = new MasterUser();
			mu.setUserId(rs.getInt("UserId"));
			mu.setFullName(rs.getString("FullName"));
			eicList.add(mu);
		}
		log.info("Exiting from getAllEicManager dao method "+eicList.size());
		return eicList;
	}

	@Override
	public List<CmsContractorWC> getAllWCBasedOnPEAndCont(String unitId, String contractorId,String workorderId) {
		log.info("Entering into getAllWCBasedOnPEAndCont dao method ");
		List<CmsContractorWC> wcList= new ArrayList<CmsContractorWC>();
		String query = getAllWc();
		log.info("Query to getAllWCBasedOnPEAndCont "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId,contractorId,workorderId);
		while(rs.next()) {
			CmsContractorWC wc = new CmsContractorWC();
			wc.setWcId(rs.getString("WCID"));
			wc.setWcCode(rs.getString("WC_CODE"));
			wc.setLicenceType(rs.getString("LICENCE_TYPE"));;
			wcList.add(wc);
		}
		log.info("Exiting from getAllWCBasedOnPEAndCont dao method "+wcList.size());
		return wcList;	
	}

	@Override
	public List<CmsGeneralMaster> getAllGenders() {
		log.info("Entering into getAllGenders dao method ");
		List<CmsGeneralMaster> gmList= new ArrayList<CmsGeneralMaster>();
		log.info("Query to getAllGenders "+WorkmenQueryBank.GET_ALL_GENDER);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(WorkmenQueryBank.GET_ALL_GENDER);
		while(rs.next()) {
			CmsGeneralMaster gm = new CmsGeneralMaster();
			gm.setGmId(rs.getString("GMID"));
			gm.setGmName(rs.getString("GMNAME"));
			gmList.add(gm);
		}
		log.info("Exiting from getAllGenders dao method "+gmList.size());
		return gmList;
	}
	
	

	@Override
	public List<CmsGeneralMaster> getAllGeneralMasters() {
		log.info("Entering into getAllGeneralMasters dao method ");
		List<CmsGeneralMaster> gmList= new ArrayList<CmsGeneralMaster>();
		String query = getAllGeneralMaster();
		log.info("Query to getAllGeneralMasters "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query);
		while(rs.next()) {
			CmsGeneralMaster gm = new CmsGeneralMaster();
			gm.setGmId(rs.getString("GMID"));
			gm.setGmName(rs.getString("GMNAME"));
			gm.setGmType(rs.getString("GMTYPE"));
			gmList.add(gm);
		}
		log.info("Exiting from getAllGeneralMasters dao method "+gmList.size());
		return gmList;
	}

	
	public static Timestamp getTimeInTimeStamp() {
		Timestamp timestamp = null;
		timestamp = new Timestamp(System.currentTimeMillis());
		return timestamp;
	}
	public String getUpdateContractWorkmen() {
		 return QueryFileWatcher.getQuery("UPDATE_CONTRACT_WORKMEN"); 
	}
	
	public String getSaveContractWorkmen() {
		 return QueryFileWatcher.getQuery("SAVE_CONTRACT_WORKMEN"); 
	}
	@Transactional(rollbackFor = Exception.class)
	@Override
	public String saveGatePass(GatePassMain gatePassMain) {
	    log.info("Entering into saveGatePass dao method");
	    String transId=gatePassMain.getTransactionId();
	    boolean status=false;
	  //check record already exists with same transactionid
        SqlRowSet rs = jdbcTemplate.queryForRowSet(this.getTransactionIdExistsQuery(),transId);
		if(rs.next()){
			
				status = true;
		}
		
	    if(status) {//already record drafted
	    	
	    	Object[] parameters = prepareGatePassParameters1(gatePassMain.getTransactionId(), gatePassMain); 
	    	 try {
	    		 String query = getUpdateContractWorkmen();
		            int result = jdbcTemplate.update(query, parameters);
		            if (result > 0) {
		            	transId=gatePassMain.getTransactionId();
		                log.info("GatePass saved successfully for transId: " + transId);
		            } else {
		                log.warn("Failed to save GatePass for transId: " + transId);
		            }
		        } catch (Exception e) {
		            log.error("Error saving GatePass for transId: " + transId, e);
		            return null;
		        }
	    }else {//saving directly without draft

	    
	        log.info("Gatepass generated for Aadhaar: " + gatePassMain.getAadhaarNumber() + " is: " + transId);

	        Object[] parameters = prepareGatePassParameters(transId, gatePassMain); 

	        try {
	        	String query = getSaveContractWorkmen();
	            int result = jdbcTemplate.update(query, parameters);
	            if (result > 0) {
	                log.info("GatePass saved successfully for transId: " + transId);
	            } else {
	                log.warn("Failed to save GatePass for transId: " + transId);
	                throw new RuntimeException("Failed to save GatePass transId: " + transId);
	            }
	        } catch (Exception e) {
	            log.error("Error saving GatePass for transId: " + transId, e);
	            //return null;
	            throw new RuntimeException(e);
	        }
	    
	    }
	    return transId;
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
				log.info("maxTestReqId"+maxTestReqId);
				
			}
	        if(maxTestReqId==null  || maxTestReqId.equals("0")){
				
	        	gatePassId ="GP700001";
			}else{
							
				long incrMaxId = Long.parseLong(maxTestReqId)+1;
				gatePassId = "GP" + decimalFormat.format(incrMaxId);
			}
	    } catch (Exception e) {
	        log.error("Error generating GatePassId", e);
	    }
	    return gatePassId;
	}

	private Object[] prepareGatePassParameters(String transId, GatePassMain gatePassMain) {
		String gatePassType = gatePassMain.getOnboardingType().equals("project")?GatePassType.PROJECT.getStatus():GatePassType.CREATE.getStatus();
	    return new Object[]{
	    		transId," ",
	    		gatePassType,
	        gatePassMain.getGatePassStatus(),
	        gatePassMain.getAadhaarNumber(),
	        gatePassMain.getFirstName(),
	        gatePassMain.getLastName(),
	        gatePassMain.getDateOfBirth(),
	        gatePassMain.getGender(),
	        gatePassMain.getRelationName(),
	        gatePassMain.getIdMark(),
	        gatePassMain.getMobileNumber(),
	        gatePassMain.getMaritalStatus(),
	        gatePassMain.getPrincipalEmployer(),
	        gatePassMain.getContractor(),
	        gatePassMain.getWorkorder(),
	        gatePassMain.getTrade(),
	        gatePassMain.getSkill(),
	        gatePassMain.getDepartment(),
	        gatePassMain.getSubdepartment(),
	        gatePassMain.getEic(),
	        gatePassMain.getNatureOfJob(),
	        gatePassMain.getWcEsicNo(),
	        gatePassMain.getHazardousArea(),
	        gatePassMain.getAccessArea(),
	        gatePassMain.getUanNumber(),
	        gatePassMain.getHealthCheckDate(),
	        gatePassMain.getPfNumber(),
	        gatePassMain.getEsicNumber(),
	        gatePassMain.getBloodGroup(),
	        gatePassMain.getAccommodation(),
	        gatePassMain.getAcademic(),
	        gatePassMain.getTechnical(),
	        gatePassMain.getIfscCode(),
	        gatePassMain.getAccountNumber(),
	        gatePassMain.getEmergencyName(),
	        gatePassMain.getEmergencyNumber(),
	        gatePassMain.getWageCategory(),
	        gatePassMain.getBonusPayout(),
	        gatePassMain.getPfCap(),
	        gatePassMain.getZone(),
	        gatePassMain.getBasic(),
	        gatePassMain.getDa(),
	        gatePassMain.getHra(),
	        gatePassMain.getWashingAllowance(),
	        gatePassMain.getOtherAllowance(),
	        gatePassMain.getUniformAllowance(),
	        gatePassMain.getAadharDocName(),gatePassMain.getPhotoName(),gatePassMain.getBankDocName(),
	        gatePassMain.getPoliceVerificationDocName(),gatePassMain.getIdProof2DocName(),gatePassMain.getMedicalDocName(),
	        gatePassMain.getEducationDocName(),gatePassMain.getForm11DocName(),gatePassMain.getTrainingDocName(),gatePassMain.getOtherDocName(),
	        gatePassMain.getWorkFlowType(),
	        gatePassMain.getComments()!=null?gatePassMain.getComments():"",
	        		gatePassMain.getAddress()!=null?gatePassMain.getAddress():"",
	        				gatePassMain.getDoj(),gatePassMain.getPfApplicable(),gatePassMain.getPoliceVerificationDate(),gatePassMain.getDot(),
	        gatePassMain.getUserId(),
	        gatePassMain.getOnboardingType(),gatePassMain.getLlNo(),gatePassMain.getAppointmentDocName(),gatePassMain.getDisability(),gatePassMain.getWorkmenType(),gatePassMain.getProficiency(),gatePassMain.getIsmw()
	        };

	}

	@Override
	public List<GatePassListingDto> getGatePassListingDetails(
	        String unitId,
	        String deptId,
	        String userId,
	        String gatePassTypeId,
	        String type,
	        List<PersonOrgLevel> contList) {

	    log.info("Entering into getGatePassListingDetails dao method");

	    List<GatePassListingDto> listDto = new ArrayList<>();

	    String query = getAllGatePassForContractor();

	    List<String> contractorIds = new ArrayList<>();

	    if (contList != null && !contList.isEmpty()) {
	        contractorIds = contList.stream()
	                .map(PersonOrgLevel::getId) // change getter if needed
	                .filter(Objects::nonNull)
	                .map(String::valueOf)
	                .distinct()
	                .collect(Collectors.toList());
	    }

	    String contractorFilter = "";

	    if (!contractorIds.isEmpty()) {
	        String inClause = contractorIds.stream()
	                .map(id -> "?")
	                .collect(Collectors.joining(","));

	        contractorFilter = " AND gpm.ContractorId IN (" + inClause + ") ";
	    }

	    query = query.replace("{CONTRACTOR_FILTER}", contractorFilter);

	    log.info("Query to getGatePassListingDetails {}", query);

	    List<Object> params = new ArrayList<>();

	    // first SELECT params
	    params.add(gatePassTypeId);
	    params.add(deptId);
	    params.add(deptId);
	    params.add(unitId);
	    params.add(type);
	    params.addAll(contractorIds);

	    // UNION SELECT params
	    params.add(gatePassTypeId);
	    params.add(type);
	    params.addAll(contractorIds);

	    SqlRowSet rs = jdbcTemplate.queryForRowSet(query, params.toArray());

	    while (rs.next()) {
	        GatePassListingDto dto = new GatePassListingDto();

	        dto.setTransactionId(rs.getString("TransactionId"));
	        dto.setGatePassId(rs.getString("GatePassId"));
	        dto.setFirstName(rs.getString("FirstName"));
	        dto.setLastName(rs.getString("LastName"));
	        dto.setGender(rs.getString("GMNAME"));
	        dto.setDateOfBirth(rs.getString("DOB"));
	        dto.setAadhaarNumber(rs.getString("AadharNumber"));
	        dto.setContractorName(rs.getString("ContractorName"));
	        dto.setVendorCode(rs.getString("VendorCode"));
	        dto.setUnitName(rs.getString("UnitName"));

	        String gatePassType = rs.getString("GatePassTypeId");

	        if (GatePassType.CREATE.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Create");
	        } else if (GatePassType.BLOCK.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Block");
	        } else if (GatePassType.UNBLOCK.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Unblock");
	        } else if (GatePassType.BLACKLIST.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Blacklist");
	        } else if (GatePassType.DEBLACKLIST.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Deblacklist");
	        } else if (GatePassType.CANCEL.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Cancel");
	        } else if (GatePassType.LOSTORDAMAGE.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Lost/Damage");
	        } else if (GatePassType.PROJECT.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Project Gatepass");
	        } else if (GatePassType.BULKCANCEL.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Bulk Cancel");
	        } else if (GatePassType.BULKRENEW.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Bulk Renew");
	        } else if (GatePassType.RENEW.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Renew");
	        }

	        String status = rs.getString("GatePassStatus");

	        if (GatePassStatus.APPROVALPENDING.getStatus().equals(status)) {
	            dto.setStatus("Approval Pending");
	        } else if (GatePassStatus.APPROVED.getStatus().equals(status)) {
	            dto.setStatus("Approved");
	        } else if (GatePassStatus.REJECTED.getStatus().equals(status)) {
	            dto.setStatus("Rejected");
	        } else if (GatePassStatus.DRAFT.getStatus().equals(status)) {
	            dto.setStatus("Draft");
	        }

	        dto.setOnboardingType(type);
	        listDto.add(dto);
	    }

	    log.info("Exiting from getGatePassListingDetails dao method {}", listDto.size());
	    return listDto;
	}
	public String getWorkFlowTypeId() {
		return QueryFileWatcher.getQuery("GET_WORKFLOWTYPEID");
	}
	@Override
public int getWorkFlowTypeId(String unitId, String actionId) {
		String query = getWorkFlowTypeId();
	//String query = "select distinct cwt.WorkflowTypeId from CMSWORKFLOWTYPE cwt \r\n"
	//		+ "join CMSAPPROVERHIERARCHY cah on cah.WorkFlowTypeId=cwt.WorkFlowTypeId where cwt.UnitId=? and cah.Action_id=?";
	SqlRowSet rs =null;
	rs = jdbcTemplate.queryForRowSet(query,unitId,actionId);
	while(rs.next()) {
		return rs.getInt("WorkflowTypeId");
	}
	return 0;
}
	@Override
	public List<GatePassListingDto> getGatePassListingForApprovers(String roleId,int workFlowType,String gatePassTypeId,String deptId,String unitId,String type) {
		log.info("Entering into getGatePassListingForApprovers dao method ");
		List<GatePassListingDto> listDto= new ArrayList<GatePassListingDto>();
		SqlRowSet rs =null;
		String query=null;
		int workflowTypeId = this.getWorkFlowTypeId(unitId, gatePassTypeId);
		
		if(workFlowType == WorkFlowType.SEQUENTIAL.getWorkFlowTypeId()) {
			query=this.getAllGatePassForSquential();
			log.info("Query to getGatePassListingForApprovers "+query);
			
			 rs = jdbcTemplate.queryForRowSet(query,workflowTypeId,workflowTypeId,deptId,deptId,unitId,roleId,gatePassTypeId,type);
		}else {
			query=this.getAllGatePassForParallel();
			log.info("Query to getGatePassListingForApprovers "+query);
			 rs = jdbcTemplate.queryForRowSet(query,workflowTypeId,roleId,gatePassTypeId,roleId,gatePassTypeId,deptId,deptId,unitId,type);
		}
		
		while(rs.next()) {
			GatePassListingDto dto = new GatePassListingDto();
			dto.setTransactionId(rs.getString("TransactionId"));
			dto.setGatePassId((rs.getString("GatePassId")));
			dto.setFirstName(rs.getString("firstName"));
			dto.setLastName(rs.getString("lastName"));
			dto.setGender(rs.getString("GMNAME"));
			dto.setDateOfBirth(rs.getString("DOB"));
			dto.setAadhaarNumber(rs.getString("AadharNumber"));
			dto.setContractorName(rs.getString("ContractorName"));
			dto.setVendorCode(rs.getString("VendorCode"));
			dto.setUnitName(rs.getString("UnitName"));
			String gatePassType = rs.getString("GatePassTypeId");
			if(gatePassType.equals(GatePassType.CREATE.getStatus())) {
				dto.setGatePassType("Create");
			}else if(gatePassType.equals(GatePassType.BLOCK.getStatus())) {
				dto.setGatePassType("Block");
			}
			else if(gatePassType.equals(GatePassType.UNBLOCK.getStatus())) {
				dto.setGatePassType("Unblock");
			}else if(gatePassType.equals(GatePassType.BLACKLIST.getStatus())) {
				dto.setGatePassType("Blacklist");
			}else if(gatePassType.equals(GatePassType.DEBLACKLIST.getStatus())) {
				dto.setGatePassType("Deblacklist");
			}else if(gatePassType.equals(GatePassType.CANCEL.getStatus())) {
				dto.setGatePassType("Cancel");
			}else if(gatePassType.equals(GatePassType.LOSTORDAMAGE.getStatus())) {
				dto.setGatePassType("Lost/Damage");
			}else if(gatePassType.equals(GatePassType.PROJECT.getStatus())) {
				dto.setGatePassType("Project Gatepass");
			}else if(gatePassType.equals(GatePassType.BULKCANCEL.getStatus())) {
				dto.setGatePassType("Bulk Cancel");
			}else if(gatePassType.equals(GatePassType.BULKRENEW.getStatus())) {
				dto.setGatePassType("Bulk Renew");
			}else if(gatePassType.equals(GatePassType.RENEW.getStatus())) {
				dto.setGatePassType("Renew");
			}
			
			String status =rs.getString("GatePassStatus");
			if(status.equals(GatePassStatus.APPROVALPENDING.getStatus())) {
				dto.setStatus("Approval Pending");
			}else if(status.equals(GatePassStatus.APPROVED.getStatus())) {
				dto.setStatus("Approved");
			}else if(status.equals(GatePassStatus.REJECTED.getStatus())) {
				dto.setStatus("Rejected");
			}else if(status.equals(GatePassStatus.DRAFT.getStatus())) {
				dto.setStatus("Draft");
			}
			dto.setOnboardingType(rs.getString("OnboardingType"));
			dto.setGatePassTypeId(rs.getString("GatePassTypeId"));
			listDto.add(dto);
		}
		log.info("Exiting from getGatePassListingForApprovers dao method "+listDto.size());
		return listDto;
	}

	public String getContractWorkmenDetails() {
		 return QueryFileWatcher.getQuery("GET_CONTRACT_WORKMEN_DETAILS");
	}
	@Override
	public GatePassMain getIndividualContractWorkmenDetails(String transactionId) {
		log.info("Entering into getIndividualContractWorkmenDetails dao method ");
		GatePassMain dto = null;
		String query = getContractWorkmenDetails();
		log.info("Query to getIndividualContractWorkmenDetails "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,transactionId);
		if(rs.next()) {
			dto = new GatePassMain();
			dto.setTransactionId(rs.getString("TransactionId"));
			dto.setGatePassId(rs.getString("GatePassId"));
			dto.setUnitId(rs.getString("peId"));
			dto.setGatePassAction(rs.getString("GatePassTypeId"));
			dto.setGatePassStatus(rs.getString("GatePassStatus"));
			dto.setAadhaarNumber(rs.getString("AadharNumber"));
			dto.setFirstName(rs.getString("FirstName"));
			dto.setLastName(rs.getString("LastName"));
			dto.setDateOfBirth(rs.getString("DOB"));
			dto.setGender(rs.getString("Gender"));
			dto.setRelationName(rs.getString("RelativeName"));
			dto.setIdMark(rs.getString("IdMark"));
			dto.setMobileNumber(rs.getString("MobileNumber"));
			dto.setMaritalStatus(rs.getString("MaritalStatus"));
			dto.setPrincipalEmployer(rs.getString("UnitId"));
			dto.setContractor(rs.getString("ContractorId"));
			dto.setWorkorder(rs.getString("WorkorderId"));
			dto.setTrade(rs.getString("TradeId"));
			dto.setSkill(rs.getString("SkillId"));
			dto.setDepartment(rs.getString("DepartmentId"));
			dto.setSubdepartment(rs.getString("AreaId"));
			dto.setEic(rs.getString("EicId"));
			dto.setNatureOfJob(rs.getString("NatureOfJob"));
			dto.setWcEsicNo(rs.getString("WcEsicNo"));
			dto.setHazardousArea(rs.getString("HazardousArea"));
			dto.setAccessArea(rs.getString("AccessAreaId"));
			dto.setUanNumber(rs.getString("UanNumber"));
			dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
			dto.setPfNumber(rs.getString("pfnumber"));
			dto.setEsicNumber(rs.getString("esicNumber"));
			dto.setBloodGroup(rs.getString("BloodGroupId"));
			dto.setAccommodation(rs.getString("Accommodation"));
			dto.setAcademic(rs.getString("AcademicId"));
			dto.setTechnical(rs.getString("Technical"));
			dto.setIfscCode(rs.getString("IfscCode"));
			dto.setAccountNumber(rs.getString("AccountNumber"));
			dto.setEmergencyName(rs.getString("EmergencyContactName"));
			dto.setEmergencyNumber(rs.getString("EmergencyContactNumber"));
			dto.setWageCategory(rs.getString("WorkmenWageCategoryId"));
			dto.setBonusPayout(rs.getString("BonusPayoutId"));
			dto.setZone(rs.getString("ZoneId"));
			dto.setBasic(new BigDecimal(rs.getString("Basic")));
			dto.setDa(new BigDecimal(rs.getString("DA")));
			dto.setHra(new BigDecimal(rs.getString("HRA")));
			dto.setWashingAllowance(new BigDecimal(rs.getString("WashingAllowance")));
			dto.setOtherAllowance(new BigDecimal(rs.getString("OtherAllowance")));
			dto.setUniformAllowance(new BigDecimal(rs.getString("UniformAllowance")));
			dto.setPfCap(rs.getString("PfCap"));
			dto.setCreatedBy(rs.getString("UpdatedBy"));
			dto.setAadharDocName(rs.getString("AadharDocName"));
			dto.setPhotoName(rs.getString("PhotoName"));
			dto.setPoliceVerificationDocName(rs.getString("PoliceVerificationDocName"));
			dto.setBankDocName(rs.getString("BankDocName"));
			dto.setIdProof2DocName(rs.getString("IdProof2DocName"));
			dto.setMedicalDocName(rs.getString("MedicalDocName"));
			dto.setForm11DocName(rs.getString("Form11DocName"));
			dto.setOtherDocName(rs.getString("OtherDocName"));
			dto.setTrainingDocName(rs.getString("TrainingDocName"));
			dto.setEducationDocName(rs.getString("EducationDocName"));
			dto.setComments(rs.getString("Comments"));
			dto.setAddress(rs.getString("Address"));
			dto.setDoj(rs.getString("DOJ"));
			dto.setPfApplicable(rs.getString("pfapplicable"));
			dto.setPoliceVerificationDate(rs.getString("policeverificationDate"));
			dto.setDot(rs.getString("DOT"));
			dto.setOnboardingType(rs.getString("OnboardingType"));
			dto.setLlNo(rs.getString("LLNo"));
			dto.setAppointmentDocName(rs.getString("AppointmentDocName"));
			dto.setDisability(rs.getString("disability"));
			dto.setWorkmenType(rs.getString("WorkmenType"));
			dto.setProficiency(rs.getString("Proficiency"));
			}
		log.info("Exiting from getIndividualContractWorkmenDetails dao method "+transactionId);
		return dto;
	}
	
	public String getAllCmsGeneralMasterForGatePass() {
		return QueryFileWatcher.getQuery("GET_ALL_CMSGENERALMASTER_FOR_GATE_PASS");
	}
	@Override
	public List<CmsGeneralMaster> getAllGeneralMastersForGatePass(GatePassMain gpm) {
		log.info("Entering into getAllGeneralMastersForGatePass dao method ");
		List<CmsGeneralMaster> gmList= new ArrayList<CmsGeneralMaster>();
		String query = getAllCmsGeneralMasterForGatePass();
		Object[] obj = new Object[] {
			    gpm.getGender() != null ? gpm.getGender() : "",
			    gpm.getBloodGroup() != null ? gpm.getBloodGroup() : "",
			    gpm.getAccessArea() != null ? gpm.getAccessArea() : "",
			    gpm.getAcademic() != null ? gpm.getAcademic() : "",
			    gpm.getZone() != null ? gpm.getZone() : "",
			    gpm.getWageCategory() != null ? gpm.getWageCategory() : "",
			    gpm.getBonusPayout() != null ? gpm.getBonusPayout() : "",
			    gpm.getDepartment() != null ? gpm.getDepartment() : "",
			    gpm.getSubdepartment() != null ? gpm.getSubdepartment() : "",
			    gpm.getTrade() != null ? gpm.getTrade() : "",
			    gpm.getSkill() != null ? gpm.getSkill() : "",
			    gpm.getWorkmenType() != null ? gpm.getWorkmenType() : "",
			    gpm.getReasoning() != null ? gpm.getReasoning() : "",
			    gpm.getProficiency() != null ? gpm.getProficiency() : ""
			};

		log.info("Query to getAllGeneralMastersForGatePass "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,obj);
		while(rs.next()) {
			CmsGeneralMaster gm = new CmsGeneralMaster();
			gm.setGmId(rs.getString("GMID"));
			gm.setGmName(rs.getString("GMNAME"));
			gm.setGmType(rs.getString("GMTYPE"));
			gmList.add(gm);
		}
		log.info("Exiting from getAllGeneralMastersForGatePass dao method "+gmList.size());
		return gmList;
	}

	@Override
	public int getWorkFlowTYpe(String principalEmployer) {
		log.info("Entering into getWorkFlowTYpe dao method ");
		int workflowTypeId = 0;
		String query = getWorkflowType();
		log.info("Query to getWorkFlowTYpe "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,principalEmployer);
		if(rs.next()) {
			workflowTypeId = rs.getInt("WorkflowType");
		}
		log.info("Exiting from getWorkFlowTYpe dao method "+principalEmployer);
		return workflowTypeId;
	}

	@Override
	public List<MasterUser> getApproversForGatePass(String createdBy) {//not required anymore
		log.info("Entering into getApproversForGatePass dao method ");
		List<MasterUser> approverList= new ArrayList<MasterUser>();
		log.info("Query to getApproversForGatePass "+WorkmenQueryBank.GET_APPROVERS_FOR_GATE_PASS);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(WorkmenQueryBank.GET_APPROVERS_FOR_GATE_PASS,createdBy);
		while(rs.next()) {
			MasterUser mu = new MasterUser();
			mu.setUserId(rs.getInt("UserId"));
			mu.setFullName(rs.getString("FullName"));
			mu.setRoleName(rs.getString("AuthorizationOn"));
			approverList.add(mu);
		}
		log.info("Exiting from getApproversForGatePass dao method "+approverList.size());
		return approverList;
	}
	
	@Override//not required anymore
	public  void saveGatePassApprover(final String gatePassId, final List<MasterUser> approverList,final String createdBy){
		log.info("Entering into getApproversForGatePass dao method ",gatePassId);
		String SAVE_GP_APPROVER = WorkmenQueryBank.SAVE_GATE_PASS_APPROVER;
		int[] save = jdbcTemplate.batchUpdate(SAVE_GP_APPROVER, new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ps.setString(1, gatePassId);
				ps.setString(2, approverList.get(i).getRoleName());
				ps.setInt(3, approverList.get(i).getUserId());
				ps.setInt(4, approverList.get(i).getIndex());
				ps.setInt(5,Integer.parseInt(approverList.get(i).getStatus()));
				ps.setString(6,createdBy);
				
			}

			public int getBatchSize() {
				return approverList.size();
			}
		});
	}

	public String getSaveGatepassApprovalStatus() {
		return QueryFileWatcher.getQuery("SAVE_GATEPASS_APPROVAL_STATUS");
	}
	@Override
	public String approveRejectGatePass(ApproveRejectGatePassDto dto) {
		 String result = null; 


		        Object[] parameters = new Object[] {dto.getTransactionId(),dto.getGatePassId(),dto.getApproverId(),dto.getApproverRole(),Integer.parseInt(dto.getStatus()),dto.getComments(),Integer.parseInt(dto.getGatePassType()),dto.getRoleId()}; 

		        try {
		        	String query = getSaveGatepassApprovalStatus();
		            int status = jdbcTemplate.update(query, parameters);
		            if (status > 0) {
		                log.info("GatePass approved/rejected successfully for GatePassId: " + dto.getTransactionId());
		                result="GatePass approved/rejected successfully";
		            } else {
		                log.warn("Failed to approve/reject GatePass for transactionId: " +  dto.getTransactionId());
		            }
		        } catch (Exception e) {
		            log.error("Error approving/rejecting GatePass for transactionId: " +  dto.getTransactionId(), e);
		            return null;
		        }
		    

		    return result;
	}

	public String getWorkflowTYpeByBT() {
		return QueryFileWatcher.getQuery("GET_WORKFLOW_TYPE_BY_BT");
	}
	@Override
	public int getWorkFlowTypeForApprovers(String businessType) {
		log.info("Entering into getWorkFlowTYpe dao method ");
		int workflowTypeId = 0;
		String query = getWorkflowTYpeByBT();
		log.info("Query to getWorkFlowTYpe "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,businessType);
		if(rs.next()) {
			workflowTypeId = rs.getInt("WorkflowType");
		}
		log.info("Exiting from getWorkFlowTYpe dao method "+businessType);
		return workflowTypeId;
	}

	public String getUpdateGatepassMainStatus() {
		return QueryFileWatcher.getQuery("UPDATE_GATEPASSMAIN_STATUS");
	}
	@Override
	public synchronized boolean updateGatePassMainStatus(String gatePassId, String status) {
		boolean res=false;
		Object[] object=new Object[]{status,gatePassId};
		String query= getUpdateGatepassMainStatus();
		int i = jdbcTemplate.update(query,object);
		if(i>0){
			res=true;
		}
		return res;
	}
	
	@Override
	public synchronized boolean updateGatePassMainDOJ(String gatePassId, String doj) {
		boolean res=false;
		Object[] object=new Object[]{doj,gatePassId};
		String query= "update gatepassmain set DOJ=? where GatePassId=?";
		int i = jdbcTemplate.update(query,object);
		if(i>0){
			res=true;
		}
		return res;
	}
	
	
	public String getUpdateGatepassMainStatusByTransactionId() {
		return QueryFileWatcher.getQuery("UPDATE_GATEPASSMAIN_STATUS_BY_TRANSACTION_ID");
	}
	@Override
	public synchronized boolean updateGatePassMainStatusByTransactionId(String transactionId, String status) {
		boolean res=false;
		Object[] object=new Object[]{status,transactionId};
		String query= getUpdateGatepassMainStatusByTransactionId();
		int i = jdbcTemplate.update(query,object);
		if(i>0){
			res=true;
		}
		return res;
	}
	
	public String getUpdateGatepassMainStatusType() {
		return QueryFileWatcher.getQuery("UPDATE_GATEPASSMAIN_STATUS_TYPE");
	}
	@Override
	public synchronized boolean updateGatePassMainStatusAndType(String gatePassId, String status,String gatePassType) {
		boolean res=false;
		Object[] object=new Object[]{status,gatePassType,gatePassId};
		String query = getUpdateGatepassMainStatusType();
		int i = jdbcTemplate.update(query,object);
		if(i>0){
			res=true;
		}
		return res;
	}

	@Override
	public boolean isLastApprover(String roleName, String  gatePassTypeId,int workflowTypeId) {
		boolean status=false;
		
		SqlRowSet rs = jdbcTemplate.queryForRowSet(this.getLastApproverQuery(),gatePassTypeId,workflowTypeId,gatePassTypeId,workflowTypeId);
		if(rs.next()){
			if(roleName.equals(rs.getString("Role_Name")))
				status = true;
		}
		log.info("exit from isLastApprover method = "+status);
		return status; 
	}
	
	public String getUpdateGatepassAction() {
		return QueryFileWatcher.getQuery("UPDATE_GATE_PASS_ACTION");
	}

	@Override
	public String gatePassAction(GatePassActionDto dto) {
		 String result = null; 


	        Object[] parameters = new Object[] {dto.getGatePassType(),dto.getGatePassStatus(),dto.getCreatedBy(),dto.getComments(),dto.getGatePassId()}; 
 
	        try {
	        	String query = getUpdateGatepassAction();
	            int status = jdbcTemplate.update(query, parameters);
	            if (status > 0) {
	                log.info("GatePass action created successfully for GatePassId: " + dto.getGatePassId());
	               // result="GatePass action created successfully";
	                result=gatePassTransactionMapping(dto);
	            } else {
	                log.warn("Failed to create GatePass action for GatePassId: " + dto.getGatePassId());
	            }
	        } catch (Exception e) {
	            log.error("Error creating GatePass action for GatePassId: " + dto.getGatePassId(), e);
	            return null;
	        }
	    

	    return result;
	}
	public String gatePassTransactionMapping() {
		return QueryFileWatcher.getQuery("INSERT_GATEPASS_TRANSACTION_MAPPING");
	}
	public String gatePassTransactionMapping(GatePassActionDto dto) {
		 //String sql = "insert into GatePassTransactionMapping (TransactionId,GatePassId,GatePassTypeId) VALUES (?,?,?)";
		String sql=gatePassTransactionMapping();
        String result=null;
        Object[] parameters = new Object[] {dto.getTransactionId(),dto.getGatePassId(),dto.getGatePassType()};
        try {
        int status = jdbcTemplate.update(sql, parameters);
        if (status > 0) {
        	result="GatePass action created successfully";
        }else {
            log.warn("Failed to create GatePass action for GatePassId: " + dto.getGatePassId());
        }
        }catch (Exception e) {
            log.error("Error creating GatePass action for GatePassId: " + dto.getGatePassId(), e);
            return null;
        }
        return result;
     
	}
	public String getSaveGatePassStatusLog() {
		return QueryFileWatcher.getQuery("SAVE_GATEPASS_STATUSLOG");
	}
	@Override
	public void saveGatePassStatusLog(GatePassStatusLogDto dto) {
		log.info("Entered into saveGatePassStatusLog for gatePassId: "+dto.getGatePassId() );

        Object[] parameters = new Object[] {dto.getTransactionId(),dto.getGatePassId(),dto.getGatePassType(),dto.getStatus(),dto.getComments(),dto.getUpdatedBy()};

        try {
        	String query = getSaveGatePassStatusLog();
            int result = jdbcTemplate.update(query,parameters );
//            if (result > 0) {
//                log.info("GatePass status log saved successfully for GatePassId: " + dto.getGatePassId());
//            } else {
//                log.warn("Failed to save GatePass status log for GatePassId: " + dto.getGatePassId());
//            }
            if (result <= 0) {
            	log.warn("Failed to save GatePass status log for GatePassId: " + dto.getGatePassId());
                throw new RuntimeException("Failed to save GatePass status log: " + dto.getGatePassId());
            }else {
            	log.info("GatePass status log saved successfully for GatePassId: " + dto.getGatePassId());
            }

        } catch (Exception e) {
            log.error("Error saving GatePass status log for GatePassId: " + dto.getGatePassId(), e);
            throw new RuntimeException(e);
        }
		log.info("Exiting from saveGatePassStatusLog for gatePassId: "+dto.getGatePassId() );
	}

	@Override
	public List<GatePassListingDto> getGatePassActionListingDetails(
	        String unitId,
	        String deptId,
	        String userId,
	        String gatePassTypeId,
	        String previousGatePassAction,
	        String renewGatePassAction,
	        String bulkRenewAction,
	        List<PersonOrgLevel> contList) {

	    log.info("Entering into getGatePassActionListingDetails dao method");

	    List<GatePassListingDto> listDto = new ArrayList<>();

	    List<String> contractorIds = new ArrayList<>();

	    if (contList != null && !contList.isEmpty()) {
	        contractorIds = contList.stream()
	                .map(PersonOrgLevel::getId) // change getter if required
	                .filter(Objects::nonNull)
	                .map(String::valueOf)
	                .distinct()
	                .collect(Collectors.toList());
	    }

	    String contractorFilter = "";

	    if (!contractorIds.isEmpty()) {
	        String inClause = contractorIds.stream()
	                .map(id -> "?")
	                .collect(Collectors.joining(","));

	        contractorFilter = " AND gpm.ContractorId IN (" + inClause + ") ";
	    }

	    String query;
	    List<Object> params = new ArrayList<>();

	    if (GatePassType.CANCEL.getStatus().equals(gatePassTypeId)) {

	        query = getCancelActionListingDetailsQuery();
	        query = query.replace("{CONTRACTOR_FILTER}", contractorFilter);

	        String bulkCancel = GatePassType.BULKCANCEL.getStatus();

	        params.add(gatePassTypeId);
	        params.add(bulkCancel);

	        params.add(deptId);
	        params.add(deptId);
	        params.add(unitId);

	        params.add(previousGatePassAction);
	        params.add(renewGatePassAction);
	        params.add(bulkRenewAction);
	        params.add(GatePassStatus.APPROVED.getStatus());

	        params.add(gatePassTypeId);
	        params.add(bulkCancel);

	        params.addAll(contractorIds);

	    } else {

	        query = getGatePassActionListingDetailsQuery();
	        query = query.replace("{CONTRACTOR_FILTER}", contractorFilter);

	        params.add(gatePassTypeId);

	        params.add(deptId);
	        params.add(deptId);
	        params.add(unitId);

	        params.add(previousGatePassAction);
	        params.add(renewGatePassAction);
	        params.add(bulkRenewAction);
	        params.add(GatePassStatus.APPROVED.getStatus());

	        params.add(gatePassTypeId);

	        params.addAll(contractorIds);
	    }

	    log.info("Query to getGatePassActionListingDetails {}", query);

	    SqlRowSet rs = jdbcTemplate.queryForRowSet(query, params.toArray());

	    while (rs.next()) {

	        GatePassListingDto dto = new GatePassListingDto();

	        dto.setTransactionId(rs.getString("TransactionId"));
	        dto.setGatePassId(rs.getString("GatePassId"));
	        dto.setFirstName(rs.getString("FirstName"));
	        dto.setLastName(rs.getString("LastName"));
	        dto.setGender(rs.getString("GMNAME"));
	        dto.setDateOfBirth(rs.getString("DOB"));
	        dto.setAadhaarNumber(rs.getString("AadharNumber"));
	        dto.setContractorName(rs.getString("ContractorName"));
	        dto.setVendorCode(rs.getString("VendorCode"));
	        dto.setUnitName(rs.getString("UnitName"));

	        String gatePassType = rs.getString("GatePassTypeId");

	        if (GatePassType.CREATE.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Create");
	        } else if (GatePassType.BLOCK.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Block");
	        } else if (GatePassType.UNBLOCK.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Unblock");
	        } else if (GatePassType.BLACKLIST.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Blacklist");
	        } else if (GatePassType.DEBLACKLIST.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Deblacklist");
	        } else if (GatePassType.CANCEL.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Cancel");
	        } else if (GatePassType.LOSTORDAMAGE.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Lost/Damage");
	        } else if (GatePassType.RENEW.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Renew");
	        } else if (GatePassType.BULKCANCEL.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Bulk Cancel");
	        } else if (GatePassType.BULKRENEW.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Bulk Renew");
	        } else if (GatePassType.PROJECT.getStatus().equals(gatePassType)) {
	            dto.setGatePassType("Project");
	        }

	        String status = rs.getString("GatePassStatus");

	        if (GatePassStatus.APPROVALPENDING.getStatus().equals(status)) {
	            dto.setStatus("Approval Pending");
	        } else if (GatePassStatus.APPROVED.getStatus().equals(status)) {
	            dto.setStatus("Approved");
	        } else if (GatePassStatus.REJECTED.getStatus().equals(status)) {
	            dto.setStatus("Rejected");
	        } else if (GatePassStatus.DRAFT.getStatus().equals(status)) {
	            dto.setStatus("Draft");
	        }

	        dto.setOnboardingType(rs.getString("OnboardingType"));

	        listDto.add(dto);
	    }

	    log.info("Exiting from getGatePassActionListingDetails dao method {}", listDto.size());

	    return listDto;
	}
	
	@Override//not required anymore
	public List<MasterUser> getApproversForGatePassAction(String createdBy,String gatepassAction) {
		log.info("Entering into getApproversForGatePass dao method ");
		List<MasterUser> approverList= new ArrayList<MasterUser>();
		log.info("Query to getApproversForGatePass "+WorkmenQueryBank.GET_APPROVERS_FOR_GATE_PASS_ACTION);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(WorkmenQueryBank.GET_APPROVERS_FOR_GATE_PASS_ACTION,createdBy,gatepassAction);
		while(rs.next()) {
			MasterUser mu = new MasterUser();
			mu.setUserId(rs.getInt("UserId"));
			mu.setFullName(rs.getString("FullName"));
			mu.setRoleName(rs.getString("RoleName"));
			approverList.add(mu);
		}
		log.info("Exiting from getApproversForGatePass dao method "+approverList.size());
		return approverList;
	}
	
	public String getGatepassById() {
		return QueryFileWatcher.getQuery("GET_GATE_PASS_BY_ID");
	}
	@Override
	public List<GatePassListingDto> getWorkmenDetailBasedOnId(String gatePassId) {
		log.info("Entering into getWorkmenDetailBasedOnId dao method ");
		List<GatePassListingDto> listDto= new ArrayList<GatePassListingDto>();
		String query = getGatepassById();
		log.info("Query to getWorkmenDetailBasedOnId "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,gatePassId);
		while(rs.next()) {
			GatePassListingDto dto = new GatePassListingDto();
			dto.setTransactionId(rs.getString("TransactionId"));
			dto.setGatePassId((rs.getString("GatePassId")));
			dto.setFirstName(rs.getString("firstName"));
			dto.setLastName(rs.getString("lastName"));
			dto.setGender(rs.getString("GMNAME"));
			dto.setDateOfBirth(rs.getString("DOB"));
			dto.setAadhaarNumber(rs.getString("AadharNumber"));
			dto.setContractorName(rs.getString("ContractorName"));
			dto.setVendorCode(rs.getString("VendorCode"));
			dto.setUnitName(rs.getString("UnitName"));
			String gatePassType = rs.getString("GatePassTypeId");
			if(gatePassType.equals(GatePassType.CREATE.getStatus())) {
				dto.setGatePassType("Create");
			}else if(gatePassType.equals(GatePassType.BLOCK.getStatus())) {
				dto.setGatePassType("Block");
			}
			else if(gatePassType.equals(GatePassType.UNBLOCK.getStatus())) {
				dto.setGatePassType("Unblock");
			}else if(gatePassType.equals(GatePassType.BLACKLIST.getStatus())) {
				dto.setGatePassType("Blacklist");
			}else if(gatePassType.equals(GatePassType.DEBLACKLIST.getStatus())) {
				dto.setGatePassType("Deblacklist");
			}else if(gatePassType.equals(GatePassType.CANCEL.getStatus())) {
				dto.setGatePassType("Cancel");
			}else if(gatePassType.equals(GatePassType.LOSTORDAMAGE.getStatus())) {
				dto.setGatePassType("Lost/Damage");
			}else if(gatePassType.equals(GatePassType.RENEW.getStatus())) {
				dto.setGatePassType("Renew");
			}else if(gatePassType.equals(GatePassType.BULKCANCEL.getStatus())) {
				dto.setGatePassType("Bulk Cancel");
			}else if(gatePassType.equals(GatePassType.BULKRENEW.getStatus())) {
				dto.setGatePassType("Bulk Renew");
			}
			String status =rs.getString("GatePassStatus");
			if(status.equals(GatePassStatus.APPROVALPENDING.getStatus())) {
				dto.setStatus("Approval Pending");
			}else if(status.equals(GatePassStatus.APPROVED.getStatus())) {
				dto.setStatus("Approved");
			}else if(status.equals(GatePassStatus.REJECTED.getStatus())) {
				dto.setStatus("Rejected");
			}else if(status.equals(GatePassStatus.DRAFT.getStatus())) {
				dto.setStatus("Draft");
			}
			listDto.add(dto);
		}
		log.info("Exiting from getWorkmenDetailBasedOnId dao method "+listDto.size());
		return listDto;
	}
	

	@Override
	public int getDOTTYpe(String principalEmployer) {
		log.info("Entering into getDOTTYpe dao method ");
		int workflowTypeId = 0;
		String query = getDotType();
		log.info("Query to getDOTTYpe "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,principalEmployer);
		if(rs.next()) {
			workflowTypeId = rs.getInt("WorkflowType");
		}
		log.info("Exiting from getDOTTYpe dao method "+principalEmployer);
		return workflowTypeId;
	}
	public String getValidityOfWoWc() {
		return QueryFileWatcher.getQuery("GET_VALIDITY_OF_WO_WC");
	}
	@Override
	 public Map<String, LocalDate> getValidityDates(String workOrderId, String wcId,String llNo) {
		 Map<String, LocalDate> validityDates = new HashMap<>();
		 String query = getValidityOfWoWc();
		 SqlRowSet rs = jdbcTemplate.queryForRowSet(query,workOrderId,wcId,llNo);
		 while(rs.next()) {
			 LocalDate validTill = rs.getDate("validTill").toLocalDate();
             String source = rs.getString("source");
             validityDates.put(source, validTill);
			}
		 return validityDates;
	 }

	@Override
	public List<ApproverStatusDTO> getApprovalDetails(String transactionId,String unitId,String gatePassTypeId,String comments) {
		 // Fetch approvers from GATEPASSAPPROVERINFO
        List<ApproverInfo> approverList = this.getApproversByGatePassId(gatePassTypeId,unitId);

        // Fetch approval statuses from GATEPASSAPPROVALSTATUS
        List<ApprovalStatus> approvalStatuses = this.getApprovalStatusByGatePassId(transactionId,gatePassTypeId);

        // Map to hold approval status by User ID
        Map<String, ApprovalStatus> statusMap = approvalStatuses.stream()
            .collect(Collectors.toMap(ApprovalStatus::getUserRole, status -> status));

        // Prepare the DTO list
        List<ApproverStatusDTO> approverStatusList = new ArrayList<>();
        for (ApproverInfo approver : approverList) {
            ApproverStatusDTO dto = new ApproverStatusDTO();
           
            dto.setUserRole(approver.getUserRole().toUpperCase());
            if(!"Workmen Bulk Upload".equals(comments)) {
            if (statusMap.containsKey(approver.getUserRole())) {
                ApprovalStatus status = statusMap.get(approver.getUserRole());
                dto.setStatus(status.getStatus() == 4 ? "Approved" : "Rejected");
                dto.setComments(status.getComments());
                dto.setTimestamp(status.getLastUpdatedDate());
            } else {
                dto.setStatus("Pending");
                dto.setComments("");
                dto.setTimestamp(null);
            }

            approverStatusList.add(dto);
            }
        }

        return approverStatusList;
	}

	private List<ApproverInfo> getApproversByGatePassId(String gatePassTypeId,String unitId) {
		 SqlRowSet rs = jdbcTemplate.queryForRowSet(getApproverHierarchy(),gatePassTypeId,unitId);
		 List<ApproverInfo> list = new ArrayList<ApproverInfo>();
		 while(rs.next()) {
			 ApproverInfo info=new ApproverInfo();
			 info.setGatePassApproverInfoId(rs.getString("hierarchy_id"));
			// info.setGatePassId(rs.getString("GatePassId"));
			 info.setIndex(rs.getInt("Index"));
			 info.setUserRole(rs.getString("Role_Name"));
			 //info.setStatus(rs.getInt("Status"));
			// info.setCreatedBy(rs.getString("CreatedBy"));
			// info.setCreatedDate(rs.getString("CreatedDate"));
			 list.add(info);
		 }
		 return list;
	}

	private List<ApprovalStatus> getApprovalStatusByGatePassId(String transactionId,String gatePassTypeId) {
		 SqlRowSet rs = jdbcTemplate.queryForRowSet(this.getApprovalStatusOfGatePass(),transactionId,gatePassTypeId);
		 List<ApprovalStatus> list = new ArrayList<ApprovalStatus>();
		 while(rs.next()) {
			 ApprovalStatus info=new ApprovalStatus();
			 info.setGatePassApprovalStatusId(rs.getString("GatePassApprovalStatusId"));
			 info.setTransactionId(rs.getString("TransactionId"));
			 info.setGatePassId(rs.getString("GatePassId"));
			 info.setGatePassTypeId(rs.getInt("GatePassTypeId"));
			 info.setUserRole(rs.getString("UserRole"));
			 info.setUserId(rs.getString("UserId"));
			 info.setStatus(rs.getInt("Status"));
			 info.setComments(rs.getString("Comments"));
			 info.setLastUpdatedDate(rs.getString("LastUpdatedDate"));
			 list.add(info);
		 }
		 return list;
	}

	@Override
	public List<Contractor> getAllContractorForAdmin(String unitId) {
		log.info("Entering into getAllContractorForAdmin dao method "+unitId);
		List<Contractor> contList= new ArrayList<Contractor>();
		String query=this.getContrForAdmin();
		log.info("Query to getAllContractorForAdmin "+query);
		SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId);
		while(rs.next()) {
			Contractor cont = new Contractor();
			cont.setContractorId(rs.getString("contractorid"));
			cont.setContractorName(rs.getString("contractorname"));
			cont.setUnitId(unitId);
			cont.setContractorCode(rs.getString("contractorcode"));
			cont.setContractorAddress(rs.getString("contractoraddress"));
			contList.add(cont);
		}
		log.info("Exiting from getAllContractorForAdmin dao method "+contList.size());
		return contList;
	}

	@Override
	public String draftGatePass(GatePassMain gatePassMain) {
	    log.info("Entering into draftGatePass dao method");

	    String transId = gatePassMain.getTransactionId();
	    		//generateTransationId(); 
	   boolean status=false;
	    if (transId != null) {
	        log.info("transId generated for Aadhaar: " + gatePassMain.getAadhaarNumber() + " is: " + transId);
	        //check record already exists with same transactionid
	        SqlRowSet rs = jdbcTemplate.queryForRowSet(this.getTransactionIdExistsQuery(),transId);
			if(rs.next()){
				
					status = true;
			}
			if(status) {
				Object[] parameters = prepareGatePassParameters1(gatePassMain.getTransactionId(), gatePassMain); 
		    	 try {
		    		 String query = this.getUpdateContractWorkmen();
			            int result = jdbcTemplate.update(query, parameters);
			            if (result > 0) {
			            	transId=gatePassMain.getTransactionId();
			                log.info("GatePass saved successfully for transId: " + transId);
			            } else {
			                log.warn("Failed to save GatePass for transId: " + transId);
			            }
			        } catch (Exception e) {
			            log.error("Error saving GatePass for transId: " + transId, e);
			            return null;
			        }
				
			}else {
				 Object[] parameters = prepareGatePassDraftParameters(transId, gatePassMain); 

			        try {
			        	String query = this.getSaveContractWorkmen();
			            int result = jdbcTemplate.update(query, parameters);
			            if (result > 0) {
			                log.info("GatePass drafted successfully for transId: " + transId);
			            } else {
			                log.warn("Failed to draft GatePass for transId: " + transId);
			            }
			        } catch (Exception e) {
			            log.error("Error saving GatePass for transId: " + transId, e);
			            return null;
			        }
			}

	       
	    } else {
	        log.error("Failed to generate transId for Aadhaar: " + gatePassMain.getAadhaarNumber());
	    }

	    return transId;
	}
	
	public String getMaxTransactionId() {
		return QueryFileWatcher.getQuery("GET_MAX_TRANSACTION_ID");
	}
	@Override
	public String generateTransationId() {
	    String transactionId = null;
	    try {
	    	String query = getMaxTransactionId();
	        SqlRowSet rs = jdbcTemplate.queryForRowSet(query);
	        if (rs.next()) {
	        	transactionId = rs.getString("newTransactionId");
	        }
	    } catch (Exception e) {
	        log.error("Error generating transactionId", e);
	    }
	    return transactionId;
	}
private Object[] prepareGatePassDraftParameters(String transId, GatePassMain gatePassMain) {
		
	    return new Object[]{
	    		transId,
	    		gatePassMain.getGatePassId(),
	        gatePassMain.getGatePassAction(),
	       gatePassMain.getGatePassStatus(),
	        gatePassMain.getAadhaarNumber()!=null? gatePassMain.getAadhaarNumber():" ",
	        gatePassMain.getFirstName()!=null? gatePassMain.getFirstName():" ",
	        gatePassMain.getLastName()!=null?  gatePassMain.getLastName():" ",
	        gatePassMain.getDateOfBirth()!=null?gatePassMain.getDateOfBirth():" ",
	        gatePassMain.getGender()!=null?gatePassMain.getGender():" ",
	        gatePassMain.getRelationName()!=null?gatePassMain.getRelationName():" ",
	        gatePassMain.getIdMark()!=null?gatePassMain.getIdMark(): " ",
	        gatePassMain.getMobileNumber()!=null?gatePassMain.getMobileNumber() : " ",
	        gatePassMain.getMaritalStatus()!=null?gatePassMain.getMaritalStatus():" ",
	        gatePassMain.getPrincipalEmployer()!=null?gatePassMain.getPrincipalEmployer():" ",
	        gatePassMain.getContractor()!=null? gatePassMain.getContractor():" ",
	        gatePassMain.getWorkorder()!=null?gatePassMain.getWorkorder():" ",
	        gatePassMain.getTrade()!=null?gatePassMain.getTrade(): " ",
	        gatePassMain.getSkill()!=null?gatePassMain.getSkill():" ",
	        gatePassMain.getDepartment()!=null?gatePassMain.getDepartment():" ",
	        gatePassMain.getSubdepartment()!=null? gatePassMain.getSubdepartment():" ",
	        gatePassMain.getEic()!=null?gatePassMain.getEic():" ",
	        gatePassMain.getNatureOfJob()!=null?gatePassMain.getNatureOfJob():" ",
	        gatePassMain.getWcEsicNo()!=null?gatePassMain.getWcEsicNo():" ",
	        gatePassMain.getHazardousArea()!=null?gatePassMain.getHazardousArea():" ",
	        gatePassMain.getAccessArea()!=null? gatePassMain.getAccessArea():" ",
	        gatePassMain.getUanNumber()!=null?gatePassMain.getUanNumber():" ",
	        gatePassMain.getHealthCheckDate()!=null?gatePassMain.getHealthCheckDate():" ",
	       gatePassMain.getPfNumber()!=null?gatePassMain.getPfNumber():" ",
	       gatePassMain.getEsicNumber()!=null?gatePassMain.getEsicNumber():" ",
	        gatePassMain.getBloodGroup()!=null?gatePassMain.getBloodGroup():" ",
	        gatePassMain.getAccommodation()!=null? gatePassMain.getAccommodation():" ",
	        gatePassMain.getAcademic()!=null?gatePassMain.getAcademic():" ",
	        gatePassMain.getTechnical()!=null?gatePassMain.getTechnical():" ",
	        gatePassMain.getIfscCode()!=null?gatePassMain.getIfscCode():" ",
	        gatePassMain.getAccountNumber()!=null?  gatePassMain.getAccountNumber():" ",
	        gatePassMain.getEmergencyName()!=null?gatePassMain.getEmergencyName():" ",
	        gatePassMain.getEmergencyNumber()!=null?gatePassMain.getEmergencyNumber():" ",
	        gatePassMain.getWageCategory()!=null?gatePassMain.getWageCategory():" ",
	        gatePassMain.getBonusPayout()!=null?gatePassMain.getBonusPayout():" ",
	        gatePassMain.getPfCap()!=null?gatePassMain.getPfCap():" ",
	        gatePassMain.getZone()!=null?gatePassMain.getZone():" ",
	        gatePassMain.getBasic()!=null?gatePassMain.getBasic():" ",
	        gatePassMain.getDa()!=null?gatePassMain.getDa():" ",
	        gatePassMain.getHra()!=null?gatePassMain.getHra():" ",
	        gatePassMain.getWashingAllowance()!=null?gatePassMain.getWashingAllowance():" ",
	        gatePassMain.getOtherAllowance()!=null?gatePassMain.getOtherAllowance():" ",
	        gatePassMain.getUniformAllowance()!=null?gatePassMain.getUniformAllowance():" ",
	        gatePassMain.getAadharDocName()!=null? gatePassMain.getAadharDocName():" ",
	        gatePassMain.getPhotoName()!=null?gatePassMain.getPhotoName():" ",
	        gatePassMain.getBankDocName()!=null?gatePassMain.getBankDocName():" ",
	        gatePassMain.getPoliceVerificationDocName()!=null?gatePassMain.getPoliceVerificationDocName():" ",
	        gatePassMain.getIdProof2DocName()!=null?gatePassMain.getIdProof2DocName():" ",
	        gatePassMain.getMedicalDocName()!=null? gatePassMain.getMedicalDocName():" ",
	        gatePassMain.getEducationDocName()!=null?gatePassMain.getEducationDocName():" ",
	        gatePassMain.getForm11DocName()!=null?gatePassMain.getForm11DocName():" ",
	        gatePassMain.getTrainingDocName()!=null?gatePassMain.getTrainingDocName():" ",
	        gatePassMain.getOtherDocName()!=null?gatePassMain.getOtherDocName():" ",
	        gatePassMain.getWorkFlowType()==0?gatePassMain.getWorkFlowType():0,
	        gatePassMain.getComments()!=null?gatePassMain.getComments():"",
	        gatePassMain.getAddress()!=null?gatePassMain.getAddress():"",
	        gatePassMain.getDoj()!=null?gatePassMain.getDoj():" ",
	        		gatePassMain.getPfApplicable()!=null?gatePassMain.getPfApplicable():" ",
	        gatePassMain.getPoliceVerificationDate()!=null?gatePassMain.getPoliceVerificationDate():" ",
	        gatePassMain.getDot()!=null?gatePassMain.getDot():" ",
	        gatePassMain.getUserId(),
	       gatePassMain.getOnboardingType(),gatePassMain.getLlNo(),gatePassMain.getAppointmentDocName()!=null?gatePassMain.getAppointmentDocName():" ",
	        	gatePassMain.getDisability()!=null?gatePassMain.getDisability():" ",
	        			gatePassMain.getWorkmenType()!=null?gatePassMain.getWorkmenType():" ",
	        					gatePassMain.getProficiency()!=null?gatePassMain.getProficiency():" ",
	        							gatePassMain.getIsmw()!=null?gatePassMain.getIsmw():" "
	    };
	}
public String getContractWorkmenDraftDetails() {
	return QueryFileWatcher.getQuery("GET_CONTRACT_WORKMEN_DRAFT_DETAILS");
}

@Override
public GatePassMain getIndividualContractWorkmenDraftDetails(String transactionId) {
	log.info("Entering into getIndividualContractWorkmenDraftDetails dao method ");
	GatePassMain dto = null;
	String query = getContractWorkmenDraftDetails();
	log.info("Query to getIndividualContractWorkmenDraftDetails "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,transactionId);
	if(rs.next()) {
		dto = new GatePassMain();
		dto.setTransactionId(rs.getString("TransactionId"));
		dto.setGatePassId(rs.getString("GatePassId"));
		dto.setUnitId(rs.getString("peId"));
		dto.setGatePassAction(rs.getString("GatePassTypeId"));
		dto.setGatePassStatus(rs.getString("GatePassStatus"));
		dto.setAadhaarNumber(rs.getString("AadharNumber"));
		dto.setFirstName(rs.getString("FirstName"));
		dto.setLastName(rs.getString("LastName"));
		dto.setDateOfBirth(rs.getString("DOB"));
		dto.setGender(rs.getString("Gender"));
		dto.setRelationName(rs.getString("RelativeName"));
		dto.setIdMark(rs.getString("IdMark"));
		dto.setMobileNumber(rs.getString("MobileNumber"));
		dto.setMaritalStatus(rs.getString("MaritalStatus"));
		dto.setPrincipalEmployer(rs.getString("UnitId"));
		dto.setContractor(rs.getString("ContractorId"));
		dto.setWorkorder(rs.getString("WorkorderId"));
		dto.setTrade(rs.getString("TradeId"));
		dto.setSkill(rs.getString("SkillId"));
		dto.setDepartment(rs.getString("DepartmentId"));
		dto.setSubdepartment(rs.getString("AreaId"));
		dto.setEic(rs.getString("EicId"));
		dto.setNatureOfJob(rs.getString("NatureOfJob"));
		dto.setWcEsicNo(rs.getString("WcEsicNo"));
		dto.setHazardousArea(rs.getString("HazardousArea"));
		dto.setAccessArea(rs.getString("AccessAreaId"));
		dto.setUanNumber(rs.getString("UanNumber"));
		dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
		dto.setPfNumber(rs.getString("pfnumber"));
		dto.setEsicNumber(rs.getString("esicNumber"));
		dto.setBloodGroup(rs.getString("BloodGroupId"));
		dto.setAccommodation(rs.getString("Accommodation"));
		dto.setAcademic(rs.getString("AcademicId"));
		dto.setTechnical(rs.getString("Technical"));
		dto.setIfscCode(rs.getString("IfscCode"));
		dto.setAccountNumber(rs.getString("AccountNumber"));
		dto.setEmergencyName(rs.getString("EmergencyContactName"));
		dto.setEmergencyNumber(rs.getString("EmergencyContactNumber"));
		dto.setWageCategory(rs.getString("WorkmenWageCategoryId"));
		dto.setBonusPayout(rs.getString("BonusPayoutId"));
		dto.setZone(rs.getString("ZoneId"));
		
		String basicValue = rs.getString("Basic");

		if (basicValue == null || basicValue.trim().isEmpty()) { 
		    dto.setBasic(BigDecimal.ZERO);
		} else {
		    try {
		        dto.setBasic(new BigDecimal(basicValue.trim())); // Trim whitespace before conversion
		    } catch (NumberFormatException e) {
		        log.error("Invalid number format for 'Basic': " + basicValue, e);
		        dto.setBasic(BigDecimal.ZERO); // Default to zero if the value is not a valid number
		    }
		}
		
		String DA = rs.getString("DA");

		if (DA == null || DA.trim().isEmpty()) { 
		    dto.setDa(BigDecimal.ZERO);
		} else {
		    try {
		        dto.setDa(new BigDecimal(DA.trim())); // Trim whitespace before conversion
		    } catch (NumberFormatException e) {
		        log.error("Invalid number format for 'Basic': " + basicValue, e);
		        dto.setDa(BigDecimal.ZERO); // Default to zero if the value is not a valid number
		    }
		}
		
		String hra = rs.getString("HRA");

		if (hra == null || hra.trim().isEmpty()) { 
		    dto.setHra(BigDecimal.ZERO);
		} else {
		    try {
		        dto.setHra(new BigDecimal(hra.trim())); // Trim whitespace before conversion
		    } catch (NumberFormatException e) {
		        log.error("Invalid number format for 'Basic': " + basicValue, e);
		        dto.setHra(BigDecimal.ZERO); // Default to zero if the value is not a valid number
		    }
		}
		
		String WashingAllowance = rs.getString("WashingAllowance");

		if (WashingAllowance == null || WashingAllowance.trim().isEmpty()) { 
		    dto.setWashingAllowance(BigDecimal.ZERO);
		} else {
		    try {
		        dto.setWashingAllowance(new BigDecimal(WashingAllowance.trim())); // Trim whitespace before conversion
		    } catch (NumberFormatException e) {
		        log.error("Invalid number format for 'Basic': " + basicValue, e);
		        dto.setWashingAllowance(BigDecimal.ZERO); // Default to zero if the value is not a valid number
		    }
		}
		
		String OtherAllowance = rs.getString("OtherAllowance");

		if (OtherAllowance == null || OtherAllowance.trim().isEmpty()) { 
		    dto.setOtherAllowance(BigDecimal.ZERO);
		} else {
		    try {
		        dto.setOtherAllowance(new BigDecimal(OtherAllowance.trim())); // Trim whitespace before conversion
		    } catch (NumberFormatException e) {
		        log.error("Invalid number format for 'Basic': " + basicValue, e);
		        dto.setOtherAllowance(BigDecimal.ZERO); // Default to zero if the value is not a valid number
		    }
		}
		
		String UniformAllowance = rs.getString("UniformAllowance");

		if (UniformAllowance == null || UniformAllowance.trim().isEmpty()) { 
		    dto.setUniformAllowance(BigDecimal.ZERO);
		} else {
		    try {
		        dto.setUniformAllowance(new BigDecimal(UniformAllowance.trim())); // Trim whitespace before conversion
		    } catch (NumberFormatException e) {
		        log.error("Invalid number format for 'Basic': " + basicValue, e);
		        dto.setUniformAllowance(BigDecimal.ZERO); // Default to zero if the value is not a valid number
		    }
		}
//		dto.setBasic(rs.getString("Basic")!=null  || "".equals(rs.getString("Basic"))?new BigDecimal(rs.getString("Basic")):new BigDecimal("0"));
//		dto.setDa(rs.getString("DA")!=null || "".equals(rs.getString("DA"))?new BigDecimal(rs.getString("DA")):new BigDecimal("0"));
//		dto.setHra(rs.getString("HRA")!=null || "".equals(rs.getString("HRA"))?new BigDecimal(rs.getString("HRA")):new BigDecimal("0"));
//		dto.setWashingAllowance(rs.getString("WashingAllowance")!=null || "".equals(rs.getString("WashingAllowance"))?new BigDecimal(rs.getString("WashingAllowance")):new BigDecimal("0"));
//		dto.setOtherAllowance(rs.getString("OtherAllowance")!=null || "".equals(rs.getString("OtherAllowance"))?new BigDecimal(rs.getString("OtherAllowance")):new BigDecimal("0"));
//		dto.setUniformAllowance(rs.getString("UniformAllowance")!=null || "".equals(rs.getString("UniformAllowance"))?new BigDecimal(rs.getString("UniformAllowance")):new BigDecimal("0"));
		dto.setPfCap(rs.getString("PfCap"));
		dto.setCreatedBy(rs.getString("UpdatedBy"));
		dto.setAadharDocName(rs.getString("AadharDocName"));
		dto.setPhotoName(rs.getString("PhotoName"));
		dto.setPoliceVerificationDocName(rs.getString("PoliceVerificationDocName"));
		dto.setBankDocName(rs.getString("BankDocName"));
		dto.setIdProof2DocName(rs.getString("IdProof2DocName"));
		dto.setMedicalDocName(rs.getString("MedicalDocName"));
		dto.setForm11DocName(rs.getString("Form11DocName"));
		dto.setOtherDocName(rs.getString("OtherDocName"));
		dto.setTrainingDocName(rs.getString("TrainingDocName"));
		dto.setEducationDocName(rs.getString("EducationDocName"));
		dto.setComments(rs.getString("Comments"));
		dto.setAddress(rs.getString("Address"));
		dto.setDoj(rs.getString("DOJ"));
		dto.setPfApplicable(rs.getString("pfapplicable"));
		dto.setPoliceVerificationDate(rs.getString("policeverificationDate"));
		dto.setDot(rs.getString("DOT"));
		dto.setLlNo(rs.getString("LLNo"));
		dto.setAppointmentDocName(rs.getString("AppointmentDocName"));
		dto.setDisability(rs.getString("disability"));
		dto.setWorkmenType(rs.getString("WorkmenType"));
		dto.setProficiency(rs.getString("Proficiency"));
	}
	log.info("Exiting from getIndividualContractWorkmenDraftDetails dao method "+transactionId);
	return dto;
}

private Object[] prepareGatePassParameters1(String transId, GatePassMain gatePassMain) {
    return new Object[]{
    		
        gatePassMain.getGatePassAction(),
        gatePassMain.getGatePassStatus(),
        gatePassMain.getAadhaarNumber(),
        gatePassMain.getFirstName(),
        gatePassMain.getLastName(),
        gatePassMain.getDateOfBirth(),
        gatePassMain.getGender(),
        gatePassMain.getRelationName(),
        gatePassMain.getIdMark(),
        gatePassMain.getMobileNumber(),
        gatePassMain.getMaritalStatus(),
        gatePassMain.getPrincipalEmployer(),
        gatePassMain.getContractor(),
        gatePassMain.getWorkorder(),
        gatePassMain.getTrade(),
        gatePassMain.getSkill(),
        gatePassMain.getDepartment(),
        gatePassMain.getSubdepartment(),
        gatePassMain.getEic(),
        gatePassMain.getNatureOfJob(),
        gatePassMain.getWcEsicNo(),
        gatePassMain.getHazardousArea(),
        gatePassMain.getAccessArea(),
        gatePassMain.getUanNumber(),
        gatePassMain.getHealthCheckDate(),
        gatePassMain.getPfNumber(),
        gatePassMain.getEsicNumber(),
        gatePassMain.getBloodGroup(),
        gatePassMain.getAccommodation(),
        gatePassMain.getAcademic(),
        gatePassMain.getTechnical(),
        gatePassMain.getIfscCode(),
        gatePassMain.getAccountNumber(),
        gatePassMain.getEmergencyName(),
        gatePassMain.getEmergencyNumber(),
        gatePassMain.getWageCategory(),
        gatePassMain.getBonusPayout(),
        gatePassMain.getPfCap(),
        gatePassMain.getZone(),
        gatePassMain.getBasic(),
        gatePassMain.getDa(),
        gatePassMain.getHra(),
        gatePassMain.getWashingAllowance(),
        gatePassMain.getOtherAllowance(),
        gatePassMain.getUniformAllowance(),
        gatePassMain.getAadharDocName(),gatePassMain.getPhotoName(),gatePassMain.getBankDocName(),
        gatePassMain.getPoliceVerificationDocName(),gatePassMain.getIdProof2DocName(),gatePassMain.getMedicalDocName(),
        gatePassMain.getEducationDocName(),gatePassMain.getForm11DocName(),gatePassMain.getTrainingDocName(),gatePassMain.getOtherDocName(),
       0,
        gatePassMain.getComments()!=null?gatePassMain.getComments():"",
        		gatePassMain.getAddress()!=null?gatePassMain.getAddress():"",
        				gatePassMain.getDoj(),gatePassMain.getPfApplicable(),gatePassMain.getPoliceVerificationDate(),gatePassMain.getDot(),
        gatePassMain.getUserId(),gatePassMain.getLlNo(),gatePassMain.getAppointmentDocName(),gatePassMain.getDisability(),gatePassMain.getWorkmenType(),gatePassMain.getProficiency(),gatePassMain.getIsmw(),transId
    };
}

public String getUpdateGatepassid() {
	return QueryFileWatcher.getQuery("UPDATE_GATEPASSID");
}
@Override
public synchronized String updateGatePassIdByTransactionId(String transactionId) {
	String gatePassId = this.generateGatePassId();
	if(null !=gatePassId) {
		Object[] object=new Object[]{gatePassId,transactionId};
		String query = getUpdateGatepassid();
		int i = jdbcTemplate.update(query,object);
//		if(i>0){
//			return gatePassId;
//		}
//		
//	}
//	return null;
		 if (i <= 0) {
		        throw new RuntimeException(
		                "Failed to update GatePassId");
		    }
	}
		    return gatePassId;
}
public String getContractWorkmenDetailsByTransId() {
	return QueryFileWatcher.getQuery("GET_CONTRACT_WORKMEN_DETAILS_BY_TRANSID");
}
@Override
public GatePassMain getIndividualContractWorkmenDetailsByTransId(String transactionId) {
	log.info("Entering into getIndividualContractWorkmenDetails dao method ");
	GatePassMain dto = null;
	String query = getContractWorkmenDetailsByTransId();
	log.info("Query to getIndividualContractWorkmenDetails "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,transactionId);
	if(rs.next()) {
		dto = new GatePassMain();
		dto.setTransactionId(rs.getString("TransactionId"));
		dto.setGatePassId(rs.getString("GatePassId"));
		dto.setUnitId(rs.getString("peId"));
		dto.setGatePassAction(rs.getString("GatePassTypeId"));
		dto.setGatePassStatus(rs.getString("GatePassStatus"));
		dto.setAadhaarNumber(rs.getString("AadharNumber"));
		dto.setFirstName(rs.getString("FirstName"));
		dto.setLastName(rs.getString("LastName"));
		dto.setDateOfBirth(rs.getString("DOB"));
		dto.setGender(rs.getString("Gender"));
		dto.setRelationName(rs.getString("RelativeName"));
		dto.setIdMark(rs.getString("IdMark"));
		dto.setMobileNumber(rs.getString("MobileNumber"));
		dto.setMaritalStatus(rs.getString("MaritalStatus"));
		dto.setPrincipalEmployer(rs.getString("UnitId"));
		dto.setContractor(rs.getString("ContractorId"));
		dto.setWorkorder(rs.getString("WorkorderId"));
		dto.setTrade(rs.getString("TradeId"));
		dto.setSkill(rs.getString("SkillId"));
		dto.setDepartment(rs.getString("DepartmentId"));
		dto.setSubdepartment(rs.getString("AreaId"));
		dto.setEic(rs.getString("EicId"));
		dto.setNatureOfJob(rs.getString("NatureOfJob"));
		dto.setWcEsicNo(rs.getString("WcEsicNo"));
		dto.setHazardousArea(rs.getString("HazardousArea"));
		dto.setAccessArea(rs.getString("AccessAreaId"));
		dto.setUanNumber(rs.getString("UanNumber"));
		dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
		dto.setPfNumber(rs.getString("pfnumber"));
		dto.setEsicNumber(rs.getString("esicNumber"));
		dto.setBloodGroup(rs.getString("BloodGroupId"));
		dto.setAccommodation(rs.getString("Accommodation"));
		dto.setAcademic(rs.getString("AcademicId"));
		dto.setTechnical(rs.getString("Technical"));
		dto.setIfscCode(rs.getString("IfscCode"));
		dto.setAccountNumber(rs.getString("AccountNumber"));
		dto.setEmergencyName(rs.getString("EmergencyContactName"));
		dto.setEmergencyNumber(rs.getString("EmergencyContactNumber"));
		dto.setWageCategory(rs.getString("WorkmenWageCategoryId"));
		dto.setBonusPayout(rs.getString("BonusPayoutId"));
		dto.setZone(rs.getString("ZoneId"));
		dto.setBasic(new BigDecimal(rs.getString("Basic")));
		dto.setDa(new BigDecimal(rs.getString("DA")));
		dto.setHra(new BigDecimal(rs.getString("HRA")));
		dto.setWashingAllowance(new BigDecimal(rs.getString("WashingAllowance")));
		dto.setOtherAllowance(new BigDecimal(rs.getString("OtherAllowance")));
		dto.setUniformAllowance(new BigDecimal(rs.getString("UniformAllowance")));
		dto.setPfCap(rs.getString("PfCap"));
		dto.setCreatedBy(rs.getString("UpdatedBy"));
		dto.setAadharDocName(rs.getString("AadharDocName"));
		dto.setPhotoName(rs.getString("PhotoName"));
		dto.setPoliceVerificationDocName(rs.getString("PoliceVerificationDocName"));
		dto.setBankDocName(rs.getString("BankDocName"));
		dto.setIdProof2DocName(rs.getString("IdProof2DocName"));
		dto.setMedicalDocName(rs.getString("MedicalDocName"));
		dto.setForm11DocName(rs.getString("Form11DocName"));
		dto.setOtherDocName(rs.getString("OtherDocName"));
		dto.setTrainingDocName(rs.getString("TrainingDocName"));
		dto.setEducationDocName(rs.getString("EducationDocName"));
		dto.setComments(rs.getString("Comments"));
		dto.setAddress(rs.getString("Address"));
		dto.setDoj(rs.getString("DOJ"));
		dto.setPfApplicable(rs.getString("pfapplicable"));
		dto.setPoliceVerificationDate(rs.getString("policeverificationDate"));
		dto.setDot(rs.getString("DOT"));
		dto.setOnboardingType(rs.getString("OnboardingType"));
		dto.setLlNo(rs.getString("LLNo"));
		dto.setAppointmentDocName(rs.getString("AppointmentDocName"));
		dto.setDisability(rs.getString("disability"));
		dto.setWorkmenType(rs.getString("WorkmenType"));
		dto.setReasoning(rs.getString("Reasoning"));
		dto.setExitLetterDocName(rs.getString("ExitLetterDocName"));
		dto.setFNFDocName(rs.getString("FNFDocName"));
		dto.setFeedbackFormDocName(rs.getString("FeedbackFormDocName"));
		dto.setRateManagerDocName(rs.getString("RateManagerDocName"));
		dto.setLOCDocName(rs.getString("LOCDocName"));
		dto.setProficiency(rs.getString("Proficiency"));
		dto.setTrainingId(rs.getString("TRAININGID"));
		dto.setIsmw(rs.getString("ISMW"));

		}
	log.info("Exiting from getIndividualContractWorkmenDetails dao method "+transactionId);
	return dto;
}

@Override
public List<GatePassListingDto> getRenewListingDetails(
        String userId,
        String gatePassTypeId,
        String gatePassStatus,
        String deptId,
        String unitId,
        List<PersonOrgLevel> contList) {

    log.info("Entering into getRenewListingDetails dao method");

    List<GatePassListingDto> listDto = new ArrayList<>();

    String query = getAllRenewForContractor();

    List<String> contractorIds = new ArrayList<>();

    if (contList != null && !contList.isEmpty()) {
        contractorIds = contList.stream()
                .map(PersonOrgLevel::getId) // change if your getter is different
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .distinct()
                .collect(Collectors.toList());
    }

    String contractorFilter = "";

    if (!contractorIds.isEmpty()) {
        String inClause = contractorIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        contractorFilter = " AND gpm.ContractorId IN (" + inClause + ") ";
    }

    query = query.replace("{CONTRACTOR_FILTER}", contractorFilter);

    log.info("Query to getRenewListingDetails {}", query);

    List<Object> params = new ArrayList<>();

    params.add(gatePassTypeId);
    params.add(gatePassStatus);
    params.addAll(contractorIds);
    params.add(deptId);
    params.add(deptId);
    params.add(unitId);
    

    SqlRowSet rs = jdbcTemplate.queryForRowSet(query, params.toArray());

    while (rs.next()) {
        GatePassListingDto dto = new GatePassListingDto();

        dto.setTransactionId(rs.getString("TransactionId"));
        dto.setGatePassId(rs.getString("GatePassId"));
        dto.setFirstName(rs.getString("FirstName"));
        dto.setLastName(rs.getString("LastName"));
        dto.setGender(rs.getString("GMNAME"));
        dto.setDateOfBirth(rs.getString("DOB"));
        dto.setAadhaarNumber(rs.getString("AadharNumber"));
        dto.setContractorName(rs.getString("ContractorName"));
        dto.setVendorCode(rs.getString("VendorCode"));
        dto.setUnitName(rs.getString("UnitName"));

        String gatePassType = rs.getString("GatePassTypeId");

        if (GatePassType.CREATE.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Create");
        } else if (GatePassType.BLOCK.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Block");
        } else if (GatePassType.UNBLOCK.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Unblock");
        } else if (GatePassType.BLACKLIST.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Blacklist");
        } else if (GatePassType.DEBLACKLIST.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Deblacklist");
        } else if (GatePassType.CANCEL.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Cancel");
        } else if (GatePassType.LOSTORDAMAGE.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Lost/Damage");
        } else if (GatePassType.RENEW.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Renew");
        } else if (GatePassType.BULKCANCEL.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Bulk Cancel");
        } else if (GatePassType.BULKRENEW.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Bulk Renew");
        } else if (GatePassType.PROJECT.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Project");
        }

        String status = rs.getString("GatePassStatus");

        if (GatePassStatus.APPROVALPENDING.getStatus().equals(status)) {
            dto.setStatus("Approval Pending");
        } else if (GatePassStatus.APPROVED.getStatus().equals(status)) {
            dto.setStatus("Approved");
        } else if (GatePassStatus.REJECTED.getStatus().equals(status)) {
            dto.setStatus("Rejected");
        } else if (GatePassStatus.DRAFT.getStatus().equals(status)) {
            dto.setStatus("Draft");
        }

        dto.setOnboardingType(rs.getString("OnboardingType"));
        listDto.add(dto);
    }

    log.info("Exiting from getRenewListingDetails dao method {}", listDto.size());
    return listDto;
}
public String getUpdateRenewContractWorkmen() {
	 return QueryFileWatcher.getQuery("UPDATE_RENEW_CONTRACT_WORKMEN"); 
}
@Override
public String renewGatePass(GatePassMain gatePassMain) {
    log.info("Entering into saveGatePass dao method");
    String transId=gatePassMain.getTransactionId();
   
	
    
        String transactionid=this.getTransactionIdByGatePassId(gatePassMain.getGatePassId());
    	Object[] parameters = prepareRenewGatePassParameters1(transactionid, gatePassMain); 
    	
    	 try {
    		 String query = this.getUpdateRenewContractWorkmen();
	            int result = jdbcTemplate.update(query, parameters);
	            if (result > 0) {
	            	transId=gatePassMain.getTransactionId();
	            	GatePassActionDto dto = new GatePassActionDto();
	            	dto.setTransactionId(gatePassMain.getTransactionId());
	            	dto.setGatePassId(gatePassMain.getGatePassId());
	            	dto.setGatePassType(GatePassType.RENEW.getStatus());
	            	this.gatePassTransactionMapping(dto);
	                log.info("GatePass saved successfully for transId: " + transId);
	            } else {
	                log.warn("Failed to save GatePass for transId: " + transId);
	            }
	        } catch (Exception e) {
	            log.error("Error saving GatePass for transId: " + transId, e);
	            return null;
	        }
   
    return transId;
}

public String getContractWorkmenDetailsByGpId() {
	return QueryFileWatcher.getQuery("GET_CONTRACT_WORKMEN_DETAILS_BY_GPID");
}
@Override
public GatePassMain getIndividualContractWorkmenDetailsByGatePassId(String gatePassId) {
	log.info("Entering into getIndividualContractWorkmenDetails dao method ");
	GatePassMain dto = null;
	String query = getContractWorkmenDetailsByGpId();
	log.info("Query to getIndividualContractWorkmenDetails "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,gatePassId);
	if(rs.next()) {
		dto = new GatePassMain();
		dto.setOnboardingType(rs.getString("OnboardingType"));
		dto.setTransactionId(rs.getString("TransactionId"));
		dto.setGatePassId(rs.getString("GatePassId"));
		dto.setUnitId(rs.getString("peId"));
		dto.setGatePassAction(rs.getString("GatePassTypeId"));
		dto.setGatePassStatus(rs.getString("GatePassStatus"));
		dto.setAadhaarNumber(rs.getString("AadharNumber"));
		dto.setFirstName(rs.getString("FirstName"));
		dto.setLastName(rs.getString("LastName"));
		dto.setDateOfBirth(rs.getString("DOB"));
		dto.setGender(rs.getString("Gender"));
		dto.setRelationName(rs.getString("RelativeName"));
		dto.setIdMark(rs.getString("IdMark"));
		dto.setMobileNumber(rs.getString("MobileNumber"));
		dto.setMaritalStatus(rs.getString("MaritalStatus"));
		dto.setPrincipalEmployer(rs.getString("UnitId"));
		dto.setContractor(rs.getString("ContractorId"));
		dto.setWorkorder(rs.getString("WorkorderId"));
		dto.setTrade(rs.getString("TradeId"));
		dto.setSkill(rs.getString("SkillId"));
		dto.setDepartment(rs.getString("DepartmentId"));
		dto.setSubdepartment(rs.getString("AreaId"));
		dto.setEic(rs.getString("EicId"));
		dto.setNatureOfJob(rs.getString("NatureOfJob"));
		dto.setWcEsicNo(rs.getString("WcEsicNo"));
		dto.setHazardousArea(rs.getString("HazardousArea"));
		dto.setAccessArea(rs.getString("AccessAreaId"));
		dto.setUanNumber(rs.getString("UanNumber"));
		dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
		dto.setPfNumber(rs.getString("pfnumber"));
		dto.setEsicNumber(rs.getString("esicNumber"));
		dto.setBloodGroup(rs.getString("BloodGroupId"));
		dto.setAccommodation(rs.getString("Accommodation"));
		dto.setAcademic(rs.getString("AcademicId"));
		dto.setTechnical(rs.getString("Technical"));
		dto.setIfscCode(rs.getString("IfscCode"));
		dto.setAccountNumber(rs.getString("AccountNumber"));
		dto.setEmergencyName(rs.getString("EmergencyContactName"));
		dto.setEmergencyNumber(rs.getString("EmergencyContactNumber"));
		dto.setWageCategory(rs.getString("WorkmenWageCategoryId"));
		dto.setBonusPayout(rs.getString("BonusPayoutId"));
		dto.setZone(rs.getString("ZoneId"));
		dto.setBasic(new BigDecimal(rs.getString("Basic")));
		dto.setDa(new BigDecimal(rs.getString("DA")));
		dto.setHra(new BigDecimal(rs.getString("HRA")));
		dto.setWashingAllowance(new BigDecimal(rs.getString("WashingAllowance")));
		dto.setOtherAllowance(new BigDecimal(rs.getString("OtherAllowance")));
		dto.setUniformAllowance(new BigDecimal(rs.getString("UniformAllowance")));
		dto.setPfCap(rs.getString("PfCap"));
		dto.setCreatedBy(rs.getString("UpdatedBy"));
		dto.setAadharDocName(rs.getString("AadharDocName"));
		dto.setPhotoName(rs.getString("PhotoName"));
		dto.setPoliceVerificationDocName(rs.getString("PoliceVerificationDocName"));
		dto.setBankDocName(rs.getString("BankDocName"));
		dto.setIdProof2DocName(rs.getString("IdProof2DocName"));
		dto.setMedicalDocName(rs.getString("MedicalDocName"));
		dto.setForm11DocName(rs.getString("Form11DocName"));
		dto.setOtherDocName(rs.getString("OtherDocName"));
		dto.setTrainingDocName(rs.getString("TrainingDocName"));
		dto.setEducationDocName(rs.getString("EducationDocName"));
		dto.setComments(rs.getString("Comments"));
		dto.setAddress(rs.getString("Address"));
		dto.setDoj(rs.getString("DOJ"));
		dto.setPfApplicable(rs.getString("pfapplicable"));
		dto.setPoliceVerificationDate(rs.getString("policeverificationDate"));
		dto.setDot(rs.getString("DOT"));
		dto.setLlNo(rs.getString("LLNo"));
		dto.setAppointmentDocName(rs.getString("AppointmentDocName"));
		dto.setDisability(rs.getString("disability"));
		dto.setWorkmenType(rs.getString("WorkmenType"));
		dto.setReasoning(rs.getString("Reasoning"));;
		dto.setExitLetterDocName(rs.getString("ExitLetterDocName"));
		dto.setFNFDocName(rs.getString("FNFDocName"));
		dto.setFeedbackFormDocName(rs.getString("FeedbackFormDocName"));
		dto.setRateManagerDocName(rs.getString("RateManagerDocName"));
		dto.setLOCDocName(rs.getString("LOCDocName"));
		dto.setAttachmentOfReference(rs.getString("AttachmentOfReference"));		
		dto.setWoId(rs.getString("woId"));
		dto.setProficiency(rs.getString("Proficiency"));
		dto.setLlId(rs.getString("llid"));
		dto.setWcEsicId(rs.getString("wcesicid"));
	}
	log.info("Exiting from getIndividualContractWorkmenDetails dao method "+gatePassId);
	return dto;
}

@Override
public List<GatePassListingDto> getGatePassActionListingForApprovers(String roleId,int workFlowType,String gatePassTypeId,String deptId,String unitId) {
	log.info("Entering into getGatePassListingForApprovers dao method ");
	List<GatePassListingDto> listDto= new ArrayList<GatePassListingDto>();
	SqlRowSet rs =null;
	String query=null;
	int workflowTypeId = this.getWorkFlowTypeId(unitId, gatePassTypeId);
	if(workFlowType == WorkFlowType.SEQUENTIAL.getWorkFlowTypeId()) {
		query=this.getAllGatePassActionForSquential();
		log.info("Query to getGatePassListingForApprovers "+query);
		
		 rs = jdbcTemplate.queryForRowSet(query,gatePassTypeId,workflowTypeId,workflowTypeId,gatePassTypeId,gatePassTypeId,gatePassTypeId,roleId,deptId,deptId,unitId);
	}else {
		query=this.getAllGatePassActionForParallel();
		log.info("Query to getGatePassListingForApprovers "+query);
		 rs = jdbcTemplate.queryForRowSet(query,gatePassTypeId,workflowTypeId,roleId,gatePassTypeId,roleId,gatePassTypeId,deptId,deptId,unitId);
	}
	
	while(rs.next()) {
		GatePassListingDto dto = new GatePassListingDto();
		dto.setTransactionId(rs.getString("TransactionId"));
		dto.setGatePassId((rs.getString("GatePassId")));
		dto.setFirstName(rs.getString("firstName"));
		dto.setLastName(rs.getString("lastName"));
		dto.setGender(rs.getString("GMNAME"));
		dto.setDateOfBirth(rs.getString("DOB"));
		dto.setAadhaarNumber(rs.getString("AadharNumber"));
		dto.setContractorName(rs.getString("ContractorName"));
		dto.setVendorCode(rs.getString("VendorCode"));
		dto.setUnitName(rs.getString("UnitName"));
		String gatePassType = rs.getString("GatePassTypeId");
		if(gatePassType.equals(GatePassType.CREATE.getStatus())) {
			dto.setGatePassType("Create");
		}else if(gatePassType.equals(GatePassType.BLOCK.getStatus())) {
			dto.setGatePassType("Block");
		}
		else if(gatePassType.equals(GatePassType.UNBLOCK.getStatus())) {
			dto.setGatePassType("Unblock");
		}else if(gatePassType.equals(GatePassType.BLACKLIST.getStatus())) {
			dto.setGatePassType("Blacklist");
		}else if(gatePassType.equals(GatePassType.DEBLACKLIST.getStatus())) {
			dto.setGatePassType("Deblacklist");
		}else if(gatePassType.equals(GatePassType.CANCEL.getStatus())) {
			dto.setGatePassType("Cancel");
		}else if(gatePassType.equals(GatePassType.LOSTORDAMAGE.getStatus())) {
			dto.setGatePassType("Lost/Damage");
		}else if(gatePassType.equals(GatePassType.RENEW.getStatus())) {
			dto.setGatePassType("Renew");
		}else if(gatePassType.equals(GatePassType.BULKCANCEL.getStatus())) {
			dto.setGatePassType("Bulk Cancel");
		}else if(gatePassType.equals(GatePassType.BULKRENEW.getStatus())) {
			dto.setGatePassType("Bulk Renew");
		}else if(gatePassType.equals(GatePassType.PROJECT.getStatus())) {
			dto.setGatePassType("Project");
		}
		
		String status =rs.getString("GatePassStatus");
		if(status.equals(GatePassStatus.APPROVALPENDING.getStatus())) {
			dto.setStatus("Approval Pending");
		}else if(status.equals(GatePassStatus.APPROVED.getStatus())) {
			dto.setStatus("Approved");
		}else if(status.equals(GatePassStatus.REJECTED.getStatus())) {
			dto.setStatus("Rejected");
		}else if(status.equals(GatePassStatus.DRAFT.getStatus())) {
			dto.setStatus("Draft");
		}
		dto.setOnboardingType(rs.getString("OnboardingType"));
		dto.setGatePassTypeId(rs.getString("GatePassTypeId"));
		listDto.add(dto);
	}
	log.info("Exiting from getGatePassListingForApprovers dao method "+listDto.size());
	return listDto;
}

public String getWorkflowTypeByTransactionIdQuery() {
	return QueryFileWatcher.getQuery("GET_WORKFLOW_TYPE_BY_TRANSACTION_ID");
}
@Override
public int getWorkFlowTYpeByTransactionId(String transactionId,String actionId) {
	log.info("Entering into getWorkFlowTYpe dao method ");
	int workflowTypeId = 0;
	String query =getWorkflowTypeByTransactionIdQuery();
	log.info("Query to getWorkFlowTYpe "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,transactionId,actionId);
	if(rs.next()) {
		workflowTypeId = rs.getInt("WorkflowType");
	}
	log.info("Exiting from getWorkFlowTYpe dao method "+transactionId);
	return workflowTypeId;
}
public String getIsLastApproverForParallel() {
	return QueryFileWatcher.getQuery("IS_LAST_APPROVER_FOR_PARALLEL");
}
@Override
public boolean isLastApproverForParallel(String gatePassTypeId, String transactionId, String roleId,String unitId) {
    boolean status = false;

    String query = getIsLastApproverForParallel();

    try {
        // Ensure proper data type conversion
        int actionId = Integer.parseInt(gatePassTypeId);
        int approverRoleId = Integer.parseInt(roleId);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(query, actionId, unitId, transactionId,actionId, approverRoleId);
        if (rs.next()) {
            String result = rs.getString("IsLastApprover");
            status = "YES".equals(result);
        }
    } catch (NumberFormatException e) {
        log.error("Invalid number format: gatePassTypeId={}, roleId={}", gatePassTypeId, roleId, e);
    } catch (Exception e) {
        log.error("Error executing isLastApproverForParallel query", e);
    }

    log.info("Exit from isLastApproverForParallel method = " + status);
    return status;
}

public String getIsLastApproverForParallelGatePassAction() {
	return QueryFileWatcher.getQuery("IS_LAST_APPROVER_FOR_GATEPASS_ACTION");
}
@Override
public boolean isLastApproverForParallelGatePassAction(String gatePassTypeId, String gatePassId, String roleId,String unitId) {
    boolean status = false;

    String query = getIsLastApproverForParallelGatePassAction();

    try {
        // Ensure proper data type conversion
        int actionId = Integer.parseInt(gatePassTypeId);
        int approverRoleId = Integer.parseInt(roleId);

        SqlRowSet rs = jdbcTemplate.queryForRowSet(query, actionId, unitId,gatePassId,actionId, approverRoleId);
        if (rs.next()) {
            String result = rs.getString("IsLastApprover");
            status = "YES".equals(result);
        }
    } catch (NumberFormatException e) {
        log.error("Invalid number format: gatePassTypeId={}, roleId={}", gatePassTypeId, roleId, e);
    } catch (Exception e) {
        log.error("Error executing isLastApproverForParallel query", e);
    }

    log.info("Exit from isLastApproverForParallel method = " + status);
    return status;
}

@Override
public int getWorkFlowTYpeNew(String principalEmployer,String gatePassAction) {
	log.info("Entering into getWorkFlowTYpeNew dao method ");
	int workflowTypeId = 0;
	String query = getWorkflowTypeNew();
	log.info("Query to getWorkFlowTYpeNew "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,principalEmployer,gatePassAction);
	if(rs.next()) {
		workflowTypeId = rs.getInt("WorkflowType");
	}
	log.info("Exiting from getWorkFlowTYpeNew dao method "+principalEmployer);
	return workflowTypeId;
}



public String getContractWorkmenExportQuery() {
	return QueryFileWatcher.getQuery("WORKMEN_EXPORT");
}
@Override
public List<ContractWorkmenExportDto> getContractWorkmenExportData(String unitId) {
	List<ContractWorkmenExportDto> dto = new ArrayList<ContractWorkmenExportDto>();
	String query = getContractWorkmenExportQuery();
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId,unitId);
	while(rs.next()) {
		ContractWorkmenExportDto obj = new ContractWorkmenExportDto();
		obj.setPersonNumber(rs.getString("GatePassId"));
		obj.setFirstName(rs.getString("FirstName"));
		obj.setLastName(rs.getString("LastName"));
		obj.setMiddleName(rs.getString("middleName"));
		obj.setShortName(rs.getString("shortName"));
		obj.setBadgeNumber(rs.getString("badgeNumber"));
		obj.setHireDate(rs.getString("hireDate"));
		obj.setBirthDate(rs.getString("birthDate"));
		obj.setPhone1(rs.getString("phone1"));
		obj.setPhone2(rs.getString("phone2"));
		obj.setPhone3(rs.getString("phone3"));
		obj.setEmail(rs.getString("email"));
		obj.setAddress(rs.getString("Address"));
		obj.setCity(rs.getString("city"));
		obj.setState(rs.getString("State"));
		obj.setCountry(rs.getString("postalCode"));
		obj.setPostalCode(rs.getString("country"));
		obj.setEmploymentStatus(rs.getString("employmentStatus"));
		
		obj.setEmploymentStatusEffectiveDate(rs.getString("employmentStatusEffectiveDate"));
		obj.setReportsToManager(rs.getString("ReportingManager"));
		obj.setWorkerType(rs.getString("workType"));
		obj.setUserAccountName(rs.getString("userAccountName"));
		obj.setUserAccountStatus(rs.getString("userAccountStatus"));
		obj.setUserPassword(rs.getString("userPassword"));
		obj.setCompany(rs.getString("company"));
		obj.setLocation(rs.getString("location"));
		obj.setPlantLocation(rs.getString("plantLocation"));
		obj.setDepartment(rs.getString("department"));
		//obj.setSection(rs.getString("section"));
		//obj.setSubSection(rs.getString("subSection"));
		obj.setContractorCOde(rs.getString("contractorCode"));
		obj.setHome6(rs.getString("home6"));
		obj.setHome7(rs.getString("home7"));
		obj.setHome8(rs.getString("home8"));
		obj.setHome9(rs.getString("home9"));
		obj.setCategory(rs.getString("category"));
		obj.setWorkorder(rs.getString("workorder"));
		//obj.setSupervisorId(rs.getString("supervisorId"));
		obj.setCostCenter(rs.getString("costCenter"));
		//obj.setWorkorder(rs.getString("workorder"));
		obj.setHome3(rs.getString("home3"));
		obj.setHome4(rs.getString("home4"));
		obj.setHome5(rs.getString("home5"));
		obj.setHome6(rs.getString("catehome6"));
		obj.setCategoryEffectiveDate(rs.getString("categoryEffectiveDate"));
		obj.setGender(rs.getString("gender"));
		obj.setAadhar(rs.getString("AadharNumber"));
		obj.setNameAsAadhar(rs.getString("aadharName"));
		obj.setRelativeName(rs.getString("RelativeName"));
		obj.setPermanentAddress(rs.getString("Address"));
		obj.setPermanentDistrict(rs.getString("permanentDistrict"));
		obj.setPermanentState(rs.getString("permanentState"));
		obj.setPermanentpincode(rs.getString("permanentPincode"));
		obj.setIdMark(rs.getString("IdMark"));
		obj.setUanNumber(rs.getString("UanNumber"));
		obj.setMaritalStatus(rs.getString("MaritalStatus"));
		obj.setTechnical(rs.getString("Technical"));
		obj.setAcademic(rs.getString("academic"));
		obj.setShoeSize(rs.getString("shoesize"));
		obj.setBloodGroup(rs.getString("bloodGroup"));
		obj.setWorkmenType(rs.getString("workmenType"));
		obj.setNatureOfJob(rs.getString("NatureOfJob"));
		obj.setEsicNo(rs.getString("WcEsicNo"));
		obj.setPanNumber(rs.getString("panNumber"));
		obj.setPfNumber(rs.getString("pfNumber"));
		obj.setBankAccountNumber(rs.getString("AccountNumber"));
		obj.setBankName(rs.getString("bankName"));
		obj.setIfscCode(rs.getString("IfscCode"));
		obj.setFutureUse2(rs.getString("future2"));
		obj.setFutureUse3(rs.getString("future3"));
		obj.setFutureUse4(rs.getString("future4"));
		obj.setFutureUse5(rs.getString("future5"));
		obj.setFutureUse6(rs.getString("future6"));
		obj.setFutureUse7(rs.getString("future7"));
		obj.setFutureUse8(rs.getString("future8"));
		obj.setFutureDate1(rs.getString("futureDate1"));
		obj.setFutureDate2(rs.getString("futureDate2"));
		obj.setFutureDate3(rs.getString("futureDate3"));
		obj.setFutureDate4(rs.getString("futureDate4"));
		obj.setFutureDate5(rs.getString("futureDate5"));
		obj.setSkill(rs.getString("skill"));
		obj.setProLevel(rs.getString("proLevel"));
		obj.setSkillDate(rs.getString("skillDate"));
		obj.setCert1(rs.getString("cert1"));
		obj.setCert1StartDate(rs.getString("cert1StartDate"));
		obj.setCert1EndDate(rs.getString("cert1EndDate"));
		
		obj.setCert2(rs.getString("cert2"));
		obj.setCert2StartDate(rs.getString("cert2StartDate"));
		obj.setCert2EndDate(rs.getString("cert2EndDate"));
		
		obj.setCert3(rs.getString("cert3"));
		obj.setCert3StartDate(rs.getString("cert3StartDate"));
		obj.setCert3EndDate(rs.getString("cert3EndDate"));
		
		obj.setCert4(rs.getString("cert4"));
		obj.setCert4StartDate(rs.getString("cert4StartDate"));
		obj.setCert4EndDate(rs.getString("cert4EndDate"));
		dto.add(obj);
	}
	return dto;
}




public String getAadharExistsQuery() {
	return QueryFileWatcher.getQuery("AADHAR_EXISTS");
}
public String getUanExistsQuery() {
	return QueryFileWatcher.getQuery("UAN_EXISTS");
}
public String getpfnumberExistsQuery() {
	return QueryFileWatcher.getQuery("PFNUMBER_EXISTS");
}

@Override
public boolean isAadharExists(String aadharNumber,String transactionId) {
	String sql = getAadharExistsQuery();
    Integer count = jdbcTemplate.queryForObject(sql, new Object[]{aadharNumber,transactionId}, Integer.class);
    return count != null && count > 0;
}



@Override
public List<WageDetailsDto> getWageMasterExportData(String unitId) {
	List<WageDetailsDto> peList= new ArrayList<WageDetailsDto>();
	String query = getWageMasterExportData();
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId);
	while(rs.next()) {
		WageDetailsDto pe = new WageDetailsDto();
		pe.setPeId(rs.getString("PECODE"));
		pe.setContractorCode(rs.getString("CONTRACTORCODE"));
		pe.setWorkmenId(rs.getString("WORKMENID"));
		pe.setAadhar(rs.getString("AADHARNUMBER"));
		pe.setBasic(rs.getString("BASIC"));
		pe.setDa(rs.getString("DA"));
		pe.setHra(rs.getString("HRA"));
		pe.setConveyenceAllow(rs.getString("CONVEYANCEALLOWANCE"));
		pe.setSpecialAllow(rs.getString("SPECIALALLOWANCE"));
		pe.setSkillAllow(rs.getString("SKILLALLOWANCE"));
		pe.setWashingAllow(rs.getString("WASHINGALLOWANCE"));
		pe.setUniformAllow(rs.getString("UNIFORMALLOWANCE"));
		pe.setHardshipAllow(rs.getString("HARDSHIPALLOWANCE"));
		pe.setEffectiveDate(rs.getString("EFFECTIVEDATE"));
		
		peList.add(pe);
	}
	return peList;
}
@Override
public String findAadharByUanIfExistsWithDifferentAadhar(String uan, String aadhar) {
    //String sql = "SELECT  TOP 1 gpm.AadharNumber FROM GATEPASSMAIN gpm WHERE gpm.UanNumber = ? AND gpm.AadharNumber != ?";
    String sql = getUanExistsQuery();
    try {
        return jdbcTemplate.queryForObject(sql, new Object[]{uan, aadhar}, String.class);
    } catch (EmptyResultDataAccessException e) {
        return null; // Not found
    }
}
@Override
public String findAadharBypfNumberIfExistsWithDifferentAadhar(String pfNumber, String aadhar) {
    //String sql = "SELECT  TOP 1 gpm.AadharNumber FROM GATEPASSMAIN gpm WHERE gpm.pfnumber = ? AND gpm.AadharNumber != ?";
    String sql = getpfnumberExistsQuery();
    try {
        return jdbcTemplate.queryForObject(sql, new Object[]{pfNumber, aadhar}, String.class);
    } catch (EmptyResultDataAccessException e) {
        return null; // Not found
    }
}

@Override
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
public String createDraftGatepass() {
	return QueryFileWatcher.getQuery("CREATE_DRAFT_GATEPASS");
}
@Override
public void createDraftGatepass(String transactionId, String userId) {
	String status = GatePassStatus.DRAFT.getStatus();
	String sql = createDraftGatepass();
    //String sql = "INSERT INTO GATEPASSMAIN (TransactionId, GatePassStatus, UpdatedDate, UpdatedBy) VALUES (?, ?, GETDATE(), ?)";
    jdbcTemplate.update(sql, transactionId,status, userId);
}
public String getGatePassActionListingDetailsQueryNav() {
	 return QueryFileWatcher.getQuery("GET_ALL_GATE_PASS_ACTION_NAV_FOR_CREATOR");
}
@Override
public List<GatePassListingDto> getGatePassActionListingDetailsDashboardNav(String unitId,String deptId,String userId, String gatePassTypeId) {
	log.info("Entering into getGatePassListingDetails dao method ");
	List<GatePassListingDto> listDto= new ArrayList<GatePassListingDto>();
	String query = getGatePassActionListingDetailsQueryNav();
	log.info("Query to getGatePassListingDetails "+query);
	//SqlRowSet rs = jdbcTemplate.queryForRowSet(query,userId,deptId,unitId,previousGatePassAction,GatePassStatus.APPROVED.getStatus(),gatePassTypeId);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,deptId,deptId,unitId,gatePassTypeId);
	while(rs.next()) {
		GatePassListingDto dto = new GatePassListingDto();
		dto.setTransactionId(rs.getString("TransactionId"));
		dto.setGatePassId((rs.getString("GatePassId")));
		dto.setFirstName(rs.getString("firstName"));
		dto.setLastName(rs.getString("lastName"));
		dto.setGender(rs.getString("GMNAME"));
		dto.setDateOfBirth(rs.getString("DOB"));
		dto.setAadhaarNumber(rs.getString("AadharNumber"));
		dto.setContractorName(rs.getString("ContractorName"));
		dto.setVendorCode(rs.getString("VendorCode"));
		dto.setUnitName(rs.getString("UnitName"));
		String gatePassType = rs.getString("GatePassTypeId");
		if(gatePassType.equals(GatePassType.CREATE.getStatus())) {
			dto.setGatePassType("Create");
		}else if(gatePassType.equals(GatePassType.BLOCK.getStatus())) {
			dto.setGatePassType("Block");
		}
		else if(gatePassType.equals(GatePassType.UNBLOCK.getStatus())) {
			dto.setGatePassType("Unblock");
		}else if(gatePassType.equals(GatePassType.BLACKLIST.getStatus())) {
			dto.setGatePassType("Blacklist");
		}else if(gatePassType.equals(GatePassType.DEBLACKLIST.getStatus())) {
			dto.setGatePassType("Deblacklist");
		}else if(gatePassType.equals(GatePassType.CANCEL.getStatus())) {
			dto.setGatePassType("Cancel");
		}else if(gatePassType.equals(GatePassType.LOSTORDAMAGE.getStatus())) {
			dto.setGatePassType("Lost/Damage");
		}
		String status =rs.getString("GatePassStatus");
		if(status.equals(GatePassStatus.APPROVALPENDING.getStatus())) {
			dto.setStatus("Approval Pending");
		}else if(status.equals(GatePassStatus.APPROVED.getStatus())) {
			dto.setStatus("Approved");
		}else if(status.equals(GatePassStatus.REJECTED.getStatus())) {
			dto.setStatus("Rejected");
		}else if(status.equals(GatePassStatus.DRAFT.getStatus())) {
			dto.setStatus("Draft");
		}
		dto.setOnboardingType(rs.getString("OnboardingType"));
		listDto.add(dto);
	}
	log.info("Exiting from getGatePassListingDetails dao method "+listDto.size());
	return listDto;
}

@Override
public List<DeptMapping> getAllSkills(String unitId, String tradeId) {
	log.info("Entering into getAllSkillsBasedOnPE dao method "+unitId);
	List<DeptMapping> skillList= new ArrayList<DeptMapping>();
	String query = getAllSkillsByPeandTrade();
	log.info("Query to getAllSkillsBasedOnPE "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId,tradeId);
	while(rs.next()) {
		DeptMapping skill = new DeptMapping();
		skill.setSkillId(Integer.parseInt(rs.getString("SKILLID")));
		skill.setSkill(rs.getString("SKILLNM"));
		skillList.add(skill);
	}
	log.info("Exiting from getAllSkillsBasedOnPE dao method "+skillList.size());
	return skillList;
}

@Override
public List<DeptMapping> getAllDepartmentsOnPE(String unitId) {
	log.info("Entering into getAllDepartmentsOnPE dao method "+unitId);
	List<DeptMapping> departmentList= new ArrayList<DeptMapping>();
	String query = getAllDepartmentsOnPE();
	log.info("Query to getAllDepartmentsOnPE "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId);
	while(rs.next()) {
		DeptMapping dept = new DeptMapping();
		dept.setDepartmentId(Integer.parseInt(rs.getString("DEPARTMENTID")));
		dept.setDepartment(rs.getString("DEPARTMENT"));
		departmentList.add(dept);
	}
	log.info("Exiting from getAllDepartmentsOnPE dao method "+departmentList.size());
	return departmentList;


}

@Override
public List<DeptMapping> getAllSubDepartments(String unitId, String departmentId) {
	log.info("Entering into getAllSubDepartments dao method "+unitId);
	List<DeptMapping> areaList= new ArrayList<DeptMapping>();
	String query = getAllSubDepartments();
	log.info("Query to getAllSubDepartmentsBasedOnPE "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId,departmentId);
	while(rs.next()) {
		DeptMapping area = new DeptMapping();
		area.setSubDepartmentId(Integer.parseInt(rs.getString("SUBDEPARTMENTID")));
		area.setSubDepartment(rs.getString("SUBDEPARTMENT"));
		areaList.add(area);
	}
	log.info("Exiting from getAllSkillsBasedOnPE dao method "+areaList.size());
	return areaList;
}
public String getTransactionIdByGPId() {
	return QueryFileWatcher.getQuery("GET_TRANSACTIONID_BY_GPID");
}
public String saveIntoCMSPerson() {
	return QueryFileWatcher.getQuery("SAVE_CMSPERSON");
}
@Override
public String getTransactionIdByGPId(String gatepassid,String gatepasstypeid) {
	String query= getTransactionIdByGPId();
	//String query="select top 1 TransactionId from GatePassTransactionMapping where GatePassId=? and GatePassTypeId=? order by CreatedDate desc ";
	String transactionId=null;
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,gatepassid,gatepasstypeid);
	if(rs.next()) {
		transactionId = rs.getString("TransactionId");
	}
	return transactionId;
}
@Transactional(rollbackFor = Exception.class)
@Override
public long saveIntoCMSPerson(CMSPerson person) {
	String sql= saveIntoCMSPerson();
   // String sql = "INSERT INTO CMSPERSON (EMPLOYEECODE, FIRSTNAME, RELATIONNAME, LASTNAME, DATEOFBIRTH, DATEOFJOINING, " +
   //         "DATEOFTERMINATION, BLOODGROUP, HAZARDOUSAREA, GENDER, ACADEMICS, ACCOMODATION, BANKBRANCH, ACCOUNTNO, " +
   //         "EMERGENCYNAME, EMERGENCYNUMBER, MOBILENUMBER, ACCESSLEVEL, ESICNUMBER, UANNUMBER, ISPFELIGIBLE, IDMARK, " +
   //         "PANNO, UPDATEDBY, AADHARNUMBER) " +
   //         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
        person.getDateOfJoining(),
       // person.getDateOfTermination(),
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
            return -1;
        }
    } catch (Exception e) {
        log.error("Error inserting into CMSPerson for EmployeeCode: " + person.getEmployeeCode(), e);
        return -1;
    }
}
public String saveIntoCMSPERSONJOBHIST() {
	return QueryFileWatcher.getQuery("SAVE_CMSPERSONJOBHIST");
}
@Transactional(rollbackFor = Exception.class)
@Override
public boolean saveIntoCMSPERSONJOBHIST(GatePassMain gpm, long employeeId) {
	boolean result = false;
	String sql = saveIntoCMSPERSONJOBHIST();
	// String sql = "INSERT INTO CMSPERSONJOBHIST ( EMPLOYEEID , TRADEID , SKILLID , UNITID , CONTRACTORID , DEPARTMENTID , "
	// 		+ " SUBDEPARTMENTID , WORKORDERID , EICID , VALIDFROM , VALIDTO  ) "
	// 		+ "     VALUES (?,?,?,?,?,?,?,?,?,?,?)";
     Object[] parameters = new Object[] {employeeId,gpm.getTrade(),gpm.getSkill(),gpm.getUnitId(),gpm.getContractor(),gpm.getDepartment(),
    		 gpm.getSubdepartment(),gpm.getWorkorder(),gpm.getEic(),gpm.getDoj(), "1/1/3000"};
     try {
     int status = jdbcTemplate.update(sql, parameters);
     if (status > 0) {
     	result=true;
     }else {
         log.warn("Failed to create GatePass action for GatePassId: " + gpm.getGatePassId());
     }
     }catch (Exception e) {
         log.error("Error creating GatePass action for GatePassId: " + gpm.getGatePassId(), e);
         return false;
     }
     return result;
}
public String saveCMSPERSONSTATUSMM() {
	return QueryFileWatcher.getQuery("SAVE_CMSPERSONSTATUSMM");
}
@Transactional(rollbackFor = Exception.class)
@Override
public boolean saveCMSPERSONSTATUSMM(GatePassMain gpm, long employeeId) {
	
	boolean result = false;
	String sql = saveCMSPERSONSTATUSMM();
	// String sql = "INSERT INTO CMSPERSONSTATUSMM ( EMPLOYEEID , ISACTIVE , VALIDFROM , VALIDTO)  VALUES (?,?,?,? )";
    Object[] parameters = new Object[] {employeeId,1,gpm.getDoj(),gpm.getDot()};
    try {
    int status = jdbcTemplate.update(sql, parameters);
    if (status > 0) {
    	boolean r = saveCMSPERSONSTATUSMMTerminated(gpm,employeeId);
    	result=r;
    }else {
        log.warn("Failed to create GatePass action for GatePassId: " + gpm.getGatePassId());
    }
    }catch (Exception e) {
        log.error("Error creating GatePass action for GatePassId: " + gpm.getGatePassId(), e);
        return false;
    }
    return result;
}
public String saveCMSPERSONSTATUSMMTerminated() {
	return QueryFileWatcher.getQuery("SAVE_CMSPERSONSTATUSMM_INACTIVE");
}
@Override
public boolean saveCMSPERSONSTATUSMMTerminated(GatePassMain gpm, long employeeId) {
	
	boolean result = false;
	String sql = saveCMSPERSONSTATUSMMTerminated();
	// String sql = "INSERT INTO CMSPERSONSTATUSMM ( EMPLOYEEID , ISACTIVE , VALIDFROM , VALIDTO)  VALUES (?,?,?,? )";
    Object[] parameters = new Object[] {employeeId,0,gpm.getNewDot(),"1/1/3000"};
    try {
    int status = jdbcTemplate.update(sql, parameters);
    if (status > 0) {
    	result=true;
    }else {
        log.warn("Failed to create GatePass action for GatePassId: " + gpm.getGatePassId());
    }
    }catch (Exception e) {
        log.error("Error creating GatePass action for GatePassId: " + gpm.getGatePassId(), e);
        return false;
    }
    return result;
}

public String saveCMSPERSONCUSTDATA() {
	return QueryFileWatcher.getQuery("SAVE_CMSPERSON_CUSTOMDATA");
}

//@Override
//public boolean saveCMSPERSONCUSTDATA( GatePassMain gp,long employeeId) {
//	String sql = saveCMSPERSONCUSTDATA() ;
//    //String sql = "INSERT INTO CMSPERSONCUSTOMDATA "
//    //        + "(EMPLOYEEID, CSTMDEFID, CUSTOMDATATEXT, EFFECTIVEFROM, EFFECTIVETILL, CREATEDTM, UPDATEDTM, UPDATEDBY) "
//    //        + "VALUES (?, ?, ?, CONVERT(date, GETDATE()), '3000-01-01', GETDATE(), GETDATE(), ?)";
//
//    // Fetch all active custom definitions
//    String defSql = "SELECT CSTMDEFID, CSTMDEFNAME FROM CMSPERSONCUSTOMDATADEFINITION WHERE ISACTIVE = 1";
//    List<Map<String, Object>> defList = jdbcTemplate.queryForList(defSql);
//
//    boolean isQuick = "quick".equalsIgnoreCase(gp.getOnboardingType());
//
//    // Allowed fields only for quick onboarding
//    List<String> quickAllowed = Arrays.asList(
//            "IdProof",
//            "MobileNumber",
//            "ContractorId",
//            "UnitId",
//            "GatePassType",
//            "Status"
//    );
//
//    List<Object[]> batchArgs = new ArrayList<>();
//
//    for (Map<String, Object> def : defList) {
//
//        int defId = (Integer) def.get("CSTMDEFID");
//        String fieldName = (String) def.get("CSTMDEFNAME");
//
//        // If quick onboarding, allow only selected fields
//        if (isQuick && !quickAllowed.contains(fieldName)) {
//            continue;
//        }
//
//        String value = mapGatePassValue(fieldName, gp);
//
//        // Skip null/empty values
//        if (value == null || value.trim().isEmpty()) {
//            continue;
//        }
//
//        batchArgs.add(new Object[]{
//                employeeId,
//                defId,
//                value,
//                gp.getDot(),
//                gp.getCreatedBy()
//        });
//    }
//
//    if (batchArgs.isEmpty()) {
//        return false; // nothing inserted
//    }
//
//    jdbcTemplate.batchUpdate(sql, batchArgs);
//    return true; // records inserted
//}

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
            return gp.getWcEsicNo();

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
            return gp.getLlNo();

        case "GatePassType":
            return gp.getGatePassAction();

        case "Status":
            return gp.getGatePassStatus();  // Block / Approve etc.

        case "WorkmenType":
            return gp.getWorkmenType();
            
        case "PhysicallyChallenged":
            return gp.getDisability();
            
        case "Proficiency":
            return gp.getProficiency();
            
        case "TrainingId":
            return gp.getTrainingId();
            
        case "ISMW":
            return gp.getIsmw();
            
        default:
            return null;
    }
}
public String getContractWorkmenDetailsByGpIdForApprove() {
	return QueryFileWatcher.getQuery("GET_CONTRACT_WORKMEN_DETAILS_BY_GPID_APPROVE");
}

@Override
public GatePassMain getIndividualContractWorkmenDetailsByGatePassIdForApprove(String gatePassId) {
	log.info("Entering into getIndividualContractWorkmenDetails dao method ");
	GatePassMain dto = null;
	String query = getContractWorkmenDetailsByGpIdForApprove();
	log.info("Query to getIndividualContractWorkmenDetails "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,gatePassId);
	if(rs.next()) {
		dto = new GatePassMain();
		dto.setOnboardingType(rs.getString("OnboardingType"));
		dto.setTransactionId(rs.getString("TransactionId"));
		dto.setGatePassId(rs.getString("GatePassId"));
		dto.setUnitId(rs.getString("peId"));
		dto.setGatePassAction(rs.getString("GatePassTypeId"));
		dto.setGatePassStatus(rs.getString("GatePassStatus"));
		dto.setAadhaarNumber(rs.getString("AadharNumber"));
		dto.setFirstName(rs.getString("FirstName"));
		dto.setLastName(rs.getString("LastName"));
		dto.setDateOfBirth(rs.getString("DOB"));
		dto.setGender(rs.getString("Gender"));
		dto.setRelationName(rs.getString("RelativeName"));
		dto.setIdMark(rs.getString("IdMark"));
		dto.setMobileNumber(rs.getString("MobileNumber"));
		dto.setMaritalStatus(rs.getString("MaritalStatus"));
		dto.setPrincipalEmployer(rs.getString("UnitId"));
		dto.setContractor(rs.getString("ContractorId"));
		dto.setWorkorder(rs.getString("WorkorderId"));
		dto.setTrade(rs.getString("TradeId"));
		dto.setSkill(rs.getString("SkillId"));
		dto.setDepartment(rs.getString("DepartmentId"));
		dto.setSubdepartment(rs.getString("AreaId"));
		dto.setEic(rs.getString("EicId"));
		dto.setNatureOfJob(rs.getString("NatureOfJob"));
		dto.setWcEsicNo(rs.getString("WcEsicNo"));
		dto.setHazardousArea(rs.getString("HazardousArea"));
		dto.setAccessArea(rs.getString("AccessAreaId"));
		dto.setUanNumber(rs.getString("UanNumber"));
		dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
		dto.setPfNumber(rs.getString("pfnumber"));
		dto.setEsicNumber(rs.getString("esicNumber"));
		dto.setBloodGroup(rs.getString("BloodGroupId"));
		dto.setAccommodation(rs.getString("Accommodation"));
		dto.setAcademic(rs.getString("AcademicId"));
		dto.setTechnical(rs.getString("Technical"));
		dto.setIfscCode(rs.getString("IfscCode"));
		dto.setAccountNumber(rs.getString("AccountNumber"));
		dto.setEmergencyName(rs.getString("EmergencyContactName"));
		dto.setEmergencyNumber(rs.getString("EmergencyContactNumber"));
		dto.setWageCategory(rs.getString("WorkmenWageCategoryId"));
		dto.setBonusPayout(rs.getString("BonusPayoutId"));
		dto.setZone(rs.getString("ZoneId"));
		dto.setBasic(new BigDecimal(rs.getString("Basic")));
		dto.setDa(new BigDecimal(rs.getString("DA")));
		dto.setHra(new BigDecimal(rs.getString("HRA")));
		dto.setWashingAllowance(new BigDecimal(rs.getString("WashingAllowance")));
		dto.setOtherAllowance(new BigDecimal(rs.getString("OtherAllowance")));
		dto.setUniformAllowance(new BigDecimal(rs.getString("UniformAllowance")));
		dto.setPfCap(rs.getString("PfCap"));
		dto.setCreatedBy(rs.getString("UpdatedBy"));
		dto.setAadharDocName(rs.getString("AadharDocName"));
		dto.setPhotoName(rs.getString("PhotoName"));
		dto.setPoliceVerificationDocName(rs.getString("PoliceVerificationDocName"));
		dto.setBankDocName(rs.getString("BankDocName"));
		dto.setIdProof2DocName(rs.getString("IdProof2DocName"));
		dto.setMedicalDocName(rs.getString("MedicalDocName"));
		dto.setForm11DocName(rs.getString("Form11DocName"));
		dto.setOtherDocName(rs.getString("OtherDocName"));
		dto.setTrainingDocName(rs.getString("TrainingDocName"));
		dto.setEducationDocName(rs.getString("EducationDocName"));
		dto.setComments(rs.getString("Comments"));
		dto.setAddress(rs.getString("Address"));
		dto.setDoj(rs.getString("DOJ"));
		dto.setPfApplicable(rs.getString("pfapplicable"));
		dto.setPoliceVerificationDate(rs.getString("policeverificationDate"));
		dto.setDot(rs.getString("DOT"));
		dto.setLlNo(rs.getString("LLNo"));
		dto.setAppointmentDocName(rs.getString("AppointmentDocName"));
		dto.setDisability(rs.getString("disability"));
		dto.setWorkmenType(rs.getString("WorkmenType"));
		dto.setReasoning(rs.getString("Reasoning"));
		dto.setExitLetterDocName(rs.getString("ExitLetterDocName"));
		dto.setFNFDocName(rs.getString("FNFDocName"));
		dto.setFeedbackFormDocName(rs.getString("FeedbackFormDocName"));
		dto.setRateManagerDocName(rs.getString("RateManagerDocName"));
		dto.setLOCDocName(rs.getString("LOCDocName"));
		dto.setProficiency(rs.getString("Proficiency"));
	}
	log.info("Exiting from getIndividualContractWorkmenDetails dao method "+gatePassId);
	return dto;
}
public String getPersonIdFromCmsPerson() {
	return QueryFileWatcher.getQuery("GET_PERSONID_FROM_CMSPERSON");
}
@Override
public long getPersonIdFromCmsPerson(String gatePassId) {
	String query= getPersonIdFromCmsPerson();
	//String query="select EMPLOYEEID from cmsperson where EMPLOYEECODE=?";
	long personId = 0;
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,gatePassId);
	if(rs.next()) {
		personId = rs.getLong("EMPLOYEEID");
	}
	return personId;
}
public String getCustomDefID() {
	return QueryFileWatcher.getQuery("GET_CUSTOM_DEFID_CMSPERSONCUSTOMDATADEFINITION");
}
public String getMaxRefID() {
	return QueryFileWatcher.getQuery("GET_MAX_REFID_CMSPERSONCUSTOMDATA");
}
public String updateEffectiveTill() {
	return QueryFileWatcher.getQuery("UPDATE_EFFECTIVE_TILL_CUSTOM_DATA");
}
public String updateEffectiveTillDeblackUnblock() {
	return QueryFileWatcher.getQuery("UPDATE_EFFECTIVE_TILL_ON_DEBLOCK_UNBLOCK_CUSTOM_DATA");
}
@Override
public boolean updateCmsPersonCustDataEffectiveTill(long personId) {

    // 1. Get CSTMDEFID for Status
	String defSql = getCustomDefID();
    //String defSql = "SELECT CSTMDEFID FROM CMSPERSONCUSTOMDATADEFINITION "
    //              + "WHERE ISACTIVE = 1 AND CSTMDEFNAME = 'Status'";

    Integer defId = jdbcTemplate.queryForObject(defSql, Integer.class);

    if (defId == null) {
        return false; // No definition → nothing to update
    }

    // 2. Get latest REFID
    String refSql = getMaxRefID();
   // String refSql = "SELECT MAX(REFID) FROM CMSPERSONCUSTOMDATA "
   //               + "WHERE CSTMDEFID = ? AND EMPLOYEEID = ?";

    Long refId = jdbcTemplate.queryForObject(refSql, Long.class, defId, personId);

    if (refId == null || refId == 0) {
        return false; // No record → nothing to update
    }

    // 3. Update EFFECTIVETILL
    String updateSql = updateEffectiveTill();
    //String updateSql = "UPDATE CMSPERSONCUSTOMDATA "
    //                 + "SET EFFECTIVETILL = CONVERT(date, GETDATE() - 1) "
    //                 + "WHERE REFID = ?";

    return jdbcTemplate.update(updateSql, refId) > 0;
}
public String getCustomDefIDforGPtype() {
	return QueryFileWatcher.getQuery("GET_CUSTOMDEFID_FOR_GP_TYPE");
}
public String getCustomDefIDforreasoning() {
	return QueryFileWatcher.getQuery("GET_CUSTOMDEFID_FOR_REASONING");
}
public String insertIntoCustData() {
	return QueryFileWatcher.getQuery("INSERT_CUSTOM_DATA");
}
@Override
public boolean insertIntoCustData(String updatedBy,long personId,String gatePassStatus,String reasoning,String dot) {
	String defSqlGatePass  = getCustomDefIDforGPtype();
	String defSqlReasoning  = getCustomDefIDforreasoning();
	//String defSql = "SELECT CSTMDEFID FROM CMSPERSONCUSTOMDATADEFINITION "
	//		+ "WHERE ISACTIVE = 1 AND CSTMDEFNAME = 'GatePassType'";

	Integer gatePassDefId  = jdbcTemplate.queryForObject(defSqlGatePass, Integer.class);
	Integer reasoningDefId  = jdbcTemplate.queryForObject(defSqlReasoning, Integer.class);
	
	if (gatePassDefId == null || reasoningDefId == null) {
        log.error("Custom definition IDs not found");
        return false;
    }
	
	boolean result = false;
	String sql = insertIntoCustData();
	//String sql = "INSERT INTO CMSPERSONCUSTOMDATA "
    //        + "(EMPLOYEEID, CSTMDEFID, CUSTOMDATATEXT, EFFECTIVEFROM, EFFECTIVETILL, CREATEDTM, UPDATEDTM, UPDATEDBY) "
    //        + "VALUES (?, ?, ?, CONVERT(date, GETDATE()), '3000-01-01', GETDATE(), GETDATE(), ?)";

   //Object[] parameters = new Object[] {personId,gatePassDefId,gatePassStatus,updatedBy};
	 int count1 =jdbcTemplate.update(sql,personId,gatePassDefId,gatePassStatus,dot,updatedBy);
	 int count2 =jdbcTemplate.update(sql,personId,reasoningDefId,reasoning,"3000-01-01",updatedBy);
   try {
   //int status = jdbcTemplate.update(sql,count1,count2);
   if (count1 > 0 && count2>0) {
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
public String isPersonActiveInStatusMM() {
	return QueryFileWatcher.getQuery("IS_PERSON_ACTIVE_IN_STATUSMM");
}
@Override
public boolean isPersonActiveInStatusMM(long personId) {
	String sql = isPersonActiveInStatusMM();
   // String sql = "SELECT COUNT(1) "
    //           + "FROM CMSPERSONSTATUSMM "
    //           + "WHERE EMPLOYEEID = ? "
    //           + "  AND GETDATE() BETWEEN VALIDFROM AND VALIDTO "
    //           + "  AND ISACTIVE = 1";

    Integer count = jdbcTemplate.queryForObject(sql, Integer.class, personId);

    return count != null && count > 0;
}
public String getActivePersonStatusIds() {
	return QueryFileWatcher.getQuery("GET_ACTIVE_PERSON_STATUS_ID");
}
public String getInactivePersonStatusIds() {
	return QueryFileWatcher.getQuery("GET_INACTIVE_PERSON_STATUS_ID");
}
@Override
public PersonStatusIds getPersonStatusIds(long personId) {

    PersonStatusIds ids = new PersonStatusIds();

    // Fetch Active ID
    String activeSql = getActivePersonStatusIds();
  //  String activeSql = "SELECT PERSONSTATUSMMID "
   //                  + "FROM CMSPERSONSTATUSMM "
   //                  + "WHERE EMPLOYEEID = ? "
  //                   + "  AND GETDATE() BETWEEN VALIDFROM AND VALIDTO "
 //                    + "  AND ISACTIVE = 1";

    try {
        Long activeId = jdbcTemplate.queryForObject(activeSql, Long.class, personId);
        ids.setActiveId(activeId);
    } catch (EmptyResultDataAccessException ex) {
        ids.setActiveId(null);
    }

    // Fetch Inactive ID
    String inactiveSql = getInactivePersonStatusIds();
    //String inactiveSql = "SELECT PERSONSTATUSMMID "
     //                  + "FROM CMSPERSONSTATUSMM "
     //                  + "WHERE EMPLOYEEID = ? "
     //                  + "  AND VALIDTO = '3000-01-01' "
     //                  + "  AND ISACTIVE = 0";

    try {
        Long inactiveId = jdbcTemplate.queryForObject(inactiveSql, Long.class, personId);
        ids.setInactiveId(inactiveId);
    } catch (EmptyResultDataAccessException ex) {
        ids.setInactiveId(null);
    }

    return ids;
}
public String updateValidtoYesterday() {
	return QueryFileWatcher.getQuery("UPDATE_ACTIVE_VALID_TO_YESTERDAY");
}
public String updateValidfromToday() {
	return QueryFileWatcher.getQuery("UPDATE_INACTIVE_VALID_FROM_TODAY");
}
@Override
public boolean updatePersonStatusValidity(Long activeId, Long inactiveId) {

    boolean updated = false;

    // Update active record → VALIDTO = yesterday
    if (activeId != null) {
    	 String sqlActive = updateValidtoYesterday() ;
        //String sqlActive = "UPDATE CMSPERSONSTATUSMM "
        //                 + "SET VALIDTO = CONVERT(date, GETDATE() - 1) "
        //                 + "WHERE PERSONSTATUSMMID = ?";

        int count1 = jdbcTemplate.update(sqlActive, activeId);
        updated = updated || count1 > 0;
    }

    // Update inactive record → VALIDFROM = today
    if (inactiveId != null) {
    	 String sqlInactive = updateValidfromToday() ;
       // String sqlInactive = "UPDATE CMSPERSONSTATUSMM "
       //                    + "SET VALIDFROM = CONVERT(date, GETDATE()) "
       //                    + "WHERE PERSONSTATUSMMID = ?";

        int count2 = jdbcTemplate.update(sqlInactive, inactiveId);
        updated = updated || count2 > 0;
    }

    return updated;
}
public String getActiveWorkmenCount() {
	return QueryFileWatcher.getQuery("GET_ACTIVE_WORKMEN_COUNT");
}



public String getPlantMaxWorkmenCount() {
	return QueryFileWatcher.getQuery("GET_PLANT_COUNTS");
}

@Override
public int getActiveWorkmenCount(String unitId,String contractorId,String gatePassStatus, String gatePassType) {
	String query = getActiveWorkmenCount() ;
	//String query="WITH Defs AS ( "
			//+ "    SELECT  "
			//+ "        MAX(CASE WHEN CSTMDEFNAME = 'ContractorId' THEN CSTMDEFID END) AS ContractorDefId, "
			//+ "        MAX(CASE WHEN CSTMDEFNAME = 'UnitId' THEN CSTMDEFID END) AS UnitDefId, "
			//+ "        MAX(CASE WHEN CSTMDEFNAME = 'Status' THEN CSTMDEFID END) AS StatusDefId, "
			//+ "        MAX(CASE WHEN CSTMDEFNAME = 'GatePassType' THEN CSTMDEFID END) AS GatePassTypeDefId "
			//+ "    FROM CMSPERSONCUSTOMDATADEFINITION "
			//+ "), "
			//+ "EmployeeData AS ( "
			//+ "    SELECT  "
			//+ "        E.EMPLOYEEID, "
			//+ "        MAX(CASE WHEN C.CSTMDEFID = D.ContractorDefId THEN C.CUSTOMDATATEXT END) AS ContractorValue, "
			//+ "        MAX(CASE WHEN C.CSTMDEFID = D.UnitDefId THEN C.CUSTOMDATATEXT END) AS UnitValue, "
			//+ "        MAX(CASE WHEN C.CSTMDEFID = D.StatusDefId THEN C.CUSTOMDATATEXT END) AS StatusValue, "
			//+ "        MAX(CASE WHEN C.CSTMDEFID = D.GatePassTypeDefId THEN C.CUSTOMDATATEXT END) AS GatePassValue "
			//+ "    FROM CMSPERSONCUSTOMDATA C "
			//+ "    CROSS JOIN Defs D "
			//+ "    JOIN ( "
			//+ "        SELECT DISTINCT EMPLOYEEID  "
			//+ "        FROM CMSPERSONCUSTOMDATA "
			//+ "    ) E ON E.EMPLOYEEID = C.EMPLOYEEID "
			//+ "    GROUP BY E.EMPLOYEEID "
			//+ ") "
			//+ "SELECT COUNT(DISTINCT ED.EMPLOYEEID) AS ActiveEmployeeCount "
			//+ "FROM EmployeeData ED "
			//+ "JOIN CMSPERSONSTATUSMM S "
			//+ "    ON S.EMPLOYEEID = ED.EMPLOYEEID "
			//+ "    AND S.ISACTIVE = 1 "
			//+ "WHERE ED.ContractorValue = ? "
			//+ "  AND ED.UnitValue = ? "
			//+ "  AND ED.StatusValue in ('3','4') "
			//+ "  AND ED.GatePassValue =?; "
			//+ "";
	
	
	int activeCount = 0;
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId,contractorId);
	if(rs.next()) {
		activeCount = rs.getInt("ActiveEmployeeCount");
	}
	return activeCount;
	
}
public String getLLDeploymentCountByUnitId() {
	return QueryFileWatcher.getQuery("GET_LL_DEPLOYMENTCOUNT_BY_UNITID");
}
@Override
public int getLLDeploymentCountByUnitId(String unitId) {
	String query = getLLDeploymentCountByUnitId();
	//String query ="  select COUNT_NO from CMS_LL_DEPLOYMENT_COUNT where UNIT_ID=? ";
	int llCount = 0;
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId);
	if(rs.next()) {
		llCount = rs.getInt("COUNT_NO");
	}
	return llCount;
	
}

public String getPreviousDocuments() {
	return QueryFileWatcher.getQuery("GET_PREVIOUS_DOCUMENTS");
}

@Override
public Map<String, String> getPreviousDocuments(String transactionId) {
	String  sql = getPreviousDocuments();
   // String sql = "SELECT AadharDocName, PhotoName, BankDocName, PoliceVerificationDocName, " +
   //              "IdProof2DocName, MedicalDocName, EducationDocName, Form11DocName, " +
   //              "TrainingDocName, OtherDocName " +
   //              "FROM GATEPASSMAIN WHERE TransactionId = ?";

    return jdbcTemplate.query(sql, rs -> {
        Map<String, String> map = new LinkedHashMap<>();
        if (rs.next()) {
        //Map<String, String> map = new LinkedHashMap<>();
        map.put("AADHAR", rs.getString("AadharDocName"));
        map.put("PHOTO", rs.getString("PhotoName"));
        map.put("BANK", rs.getString("BankDocName"));
        map.put("POLICE", rs.getString("PoliceVerificationDocName"));
        map.put("IDPROOF2", rs.getString("IdProof2DocName"));
        map.put("MEDICAL", rs.getString("MedicalDocName"));
        map.put("EDUCATION", rs.getString("EducationDocName"));
        map.put("FORM11", rs.getString("Form11DocName"));
        map.put("TRAINING", rs.getString("TrainingDocName"));
        map.put("OTHER", rs.getString("OtherDocName"));
        }
        return map;
    }, transactionId);
    }

public String getRenewalDocs() {
	return QueryFileWatcher.getQuery("GET_RENEWAL_DOCUMENTS");
}

@Override
public List<Map<String, Object>> getRenewalDocs(String transactionId) {
	String sql = getRenewalDocs();
    //String sql = "SELECT DOCTYPE, FILENAME, VERSIONNO " +
    //             "FROM GATEPASSDOCUMENTS WHERE TRANSACTIONID = ? ORDER BY VERSIONNO ASC";
    return jdbcTemplate.queryForList(sql, transactionId);
}
public String getLLlicenseExistsAndCount() {
	return QueryFileWatcher.getQuery("GET_LL_LICENSE_EXISTS_COUNT");
}
public String getWCESIClicenseExistsAndCount() {
	return QueryFileWatcher.getQuery("GET_WC_ESIC_LICENSE_EXISTS_COUNT");
}
//@Override
//public int licenseExistsAndCount(String unitId,String contractorId,String workorderId,String licenseType,String licenseId) {
//	String query=null;
//	if(licenseType.equals("LL")) {
//		 query = getLLlicenseExistsAndCount() ;
//		
//	}else {
//		 query = getWCESIClicenseExistsAndCount() ;
//	}
//	int licenseCount=0;
//	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId,contractorId,workorderId,licenseId);
//	if(rs.next()) {
//		licenseCount = rs.getInt("WC_TOTAL");
//	}
//	return licenseCount;
//}

@Override
public Map<String, Object> licenseExistsAndCount(
        String unitId,
        String contractorId,
        String workorderId,
        String licenseType,
        String licenseId) {
	  String query = null;
	  if(licenseType.equals("LL")) {
			 query = getLLlicenseExistsAndCount() ;
			
		}else {
			 query = getWCESIClicenseExistsAndCount() ;
		}
    Map<String, Object> result = new HashMap<>();

    // ---------- IMPORTANT: Handle LL not mapped ----------
    if ("LL".equals(licenseType) &&
        (licenseId == null || licenseId.trim().isEmpty())) {

        result.put("exists", false);
        result.put("count", 0);
        result.put("expiryDate", null);
        return result;
    }

  

    SqlRowSet rs = jdbcTemplate.queryForRowSet(
            query,
            licenseType,
            unitId,
            contractorId,
            workorderId,
            licenseId
    );

    boolean exists = false;
    int count = 0;
    Date expiryDate = null;

    if (rs.next()) {
        exists = rs.getInt("LICENSE_EXISTS") == 1;
        count = rs.getInt("WC_TOTAL");
        expiryDate = rs.getDate("WC_TO_DTM");
    }

    result.put("exists", exists);
    result.put("count", count);
    result.put("expiryDate", expiryDate);

    return result;
}


public String selectMaxVesionFromGatepass() {
	return QueryFileWatcher.getQuery("GET_MAX_VERSION_DOCS_FROM_GATEPASS");
}
public String saveRenewDocuments() {
	return QueryFileWatcher.getQuery("SAVE_RENEW_DOCUMENTS");
}
@Override
public void saveRenewedDocuments(String transactionId, String userId,
                                 MultipartFile aadharFile,
                                 MultipartFile policeFile,
                                 MultipartFile profilePic,
                                 List<MultipartFile> additionalFiles,
                                 List<String> documentTypes,String filePath) {
    try {
        // ✅ Determine next version number
    	String versionSql = selectMaxVesionFromGatepass();
        //String versionSql = "SELECT MAX(VERSIONNO)+ 1 FROM GATEPASSDOCUMENTS WHERE TRANSACTIONID = ?";
        Integer currentVersion = jdbcTemplate.queryForObject(versionSql, Integer.class, transactionId);

        // If no version found, start from 2 (since version 1 is from GATEPASSMAIN)
        int nextVersion = (currentVersion == null) ? 2 : currentVersion + 1;
        String insertSql = saveRenewDocuments();
       // String insertSql = "INSERT INTO GATEPASSDOCUMENTS (TRANSACTIONID, DOCTYPE, FILENAME, VERSIONNO, USERID, FILEPATH) " +
        //        "VALUES (?, ?, ?, ?, ?, ?)";

// Ensure folder exists
File folder = new File(filePath);
if (!folder.exists()) folder.mkdirs();

// 2️⃣ Save Aadhaar File
if (aadharFile != null && !aadharFile.isEmpty()) {
 String newFileName = "aadhar_V" + nextVersion + getFileExtension(aadharFile.getOriginalFilename());
 File dest = new File(filePath + newFileName);
 aadharFile.transferTo(dest);

 jdbcTemplate.update(insertSql, transactionId, "AADHAR", newFileName, nextVersion, userId, dest.getAbsolutePath());
}

// 3️⃣ Save Police File
if (policeFile != null && !policeFile.isEmpty()) {
 String newFileName = "police_V" + nextVersion + getFileExtension(policeFile.getOriginalFilename());
 File dest = new File(filePath + newFileName);
 policeFile.transferTo(dest);

 jdbcTemplate.update(insertSql, transactionId, "POLICE", newFileName, nextVersion, userId, dest.getAbsolutePath());
}

// 4️⃣ Save Profile Pic
if (profilePic != null && !profilePic.isEmpty()) {
 String newFileName = "photo_V" + nextVersion + getFileExtension(profilePic.getOriginalFilename());
 File dest = new File(filePath + newFileName);
 profilePic.transferTo(dest);

 jdbcTemplate.update(insertSql, transactionId, "PHOTO", newFileName, nextVersion, userId, dest.getAbsolutePath());
}

// 5️⃣ Save Additional Files
if (additionalFiles != null && documentTypes != null) {
 for (int i = 0; i < additionalFiles.size(); i++) {
     MultipartFile file = additionalFiles.get(i);
     String docType = documentTypes.get(i);
     if (file != null && !file.isEmpty()) {
         String newFileName = docType.toLowerCase() + "_V" + nextVersion + getFileExtension(file.getOriginalFilename());
         File dest = new File(filePath + newFileName);
         file.transferTo(dest);

         jdbcTemplate.update(insertSql, transactionId, docType.toUpperCase(), newFileName, nextVersion, userId, dest.getAbsolutePath());
     }
 }
}

log.info("✅ Renewed documents saved successfully with version V{}", nextVersion);

} catch (Exception e) {
log.error("❌ Error saving renewed documents: ", e);
}
}

/** Helper method to get extension from filename */
     private String getFileExtension(String filename) {
      int dotIndex = filename.lastIndexOf(".");
     return (dotIndex != -1) ? filename.substring(dotIndex) : "";
   }
private Object[] prepareRenewGatePassParameters1(String transId, GatePassMain gatePassMain) {
	
    return new Object[]{
    		
        gatePassMain.getGatePassAction(),
        gatePassMain.getGatePassStatus(),
        gatePassMain.getAadhaarNumber(),
        gatePassMain.getFirstName(),
        gatePassMain.getLastName(),
        gatePassMain.getDateOfBirth(),
        gatePassMain.getGender(),
        gatePassMain.getRelationName(),
        gatePassMain.getIdMark(),
        gatePassMain.getMobileNumber(),
        gatePassMain.getMaritalStatus(),	
        gatePassMain.getPrincipalEmployer(),
        gatePassMain.getContractor(),
        gatePassMain.getWorkorder(),
        gatePassMain.getTrade(),
        gatePassMain.getSkill(),
        gatePassMain.getDepartment(),
        gatePassMain.getSubdepartment(),
        gatePassMain.getEic(),
        gatePassMain.getNatureOfJob(),
        gatePassMain.getWcEsicNo(),
        gatePassMain.getHazardousArea(),
        gatePassMain.getAccessArea(),
        gatePassMain.getUanNumber(),
        gatePassMain.getHealthCheckDate(),
        gatePassMain.getPfNumber(),
        gatePassMain.getEsicNumber(),
        gatePassMain.getBloodGroup(),
        gatePassMain.getAccommodation(),
        gatePassMain.getAcademic(),
        gatePassMain.getTechnical(),
        gatePassMain.getIfscCode(),
        gatePassMain.getAccountNumber(),
        gatePassMain.getEmergencyName(),
        gatePassMain.getEmergencyNumber(),
        gatePassMain.getWageCategory(),
        gatePassMain.getBonusPayout(),
        gatePassMain.getPfCap(),
        gatePassMain.getZone(),
        gatePassMain.getBasic(),
        gatePassMain.getDa(),
        gatePassMain.getHra(),
        gatePassMain.getWashingAllowance(),
        gatePassMain.getOtherAllowance(),
        gatePassMain.getUniformAllowance(),
        0,
        gatePassMain.getComments()!=null?gatePassMain.getComments():"",
        		gatePassMain.getAddress()!=null?gatePassMain.getAddress():"",
        				gatePassMain.getPfApplicable(),gatePassMain.getPoliceVerificationDate(),gatePassMain.getDot(),
        gatePassMain.getUserId(),gatePassMain.getLlNo(),
        gatePassMain.getAadharDocName(),gatePassMain.getPhotoName(),gatePassMain.getBankDocName(),
        gatePassMain.getPoliceVerificationDocName(),gatePassMain.getIdProof2DocName(),gatePassMain.getMedicalDocName(),
        gatePassMain.getEducationDocName(),gatePassMain.getForm11DocName(),gatePassMain.getTrainingDocName(),gatePassMain.getOtherDocName(),
        gatePassMain.getAppointmentDocName(),gatePassMain.getDisability(),gatePassMain.getWorkmenType(),gatePassMain.getProficiency(),gatePassMain.getIsmw(),transId
    };
}
@Override
public List<GatePassListingDto> getGatePassUnblockDeblackListingDetails(
        String unitId,
        String deptId,
        String userId,
        String gatePassTypeId,
        String previousGatePassAction,
        String renewGatePassAction,
        List<PersonOrgLevel> contList) {

    log.info("Entering into getGatePassUnblockDeblackListingDetails dao method");

    List<GatePassListingDto> listDto = new ArrayList<>();

    String query = getGatePassUnblockDeblackListingDetailsQuery();

    List<String> contractorIds = new ArrayList<>();

    if (contList != null && !contList.isEmpty()) {
        contractorIds = contList.stream()
                .map(PersonOrgLevel::getId) // change getter if needed
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .distinct()
                .collect(Collectors.toList());
    }

    String contractorFilter = "";

    if (!contractorIds.isEmpty()) {
        String inClause = contractorIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        contractorFilter = " AND gpm.ContractorId IN (" + inClause + ") ";
    }

    query = query.replace("{CONTRACTOR_FILTER}", contractorFilter);

    List<Object> params = new ArrayList<>();

    params.add(gatePassTypeId);

    params.add(deptId);
    params.add(deptId);
    params.add(unitId);

    params.add(previousGatePassAction);
    params.add(renewGatePassAction);
    params.add(GatePassStatus.APPROVED.getStatus());

    params.add(gatePassTypeId);

    params.addAll(contractorIds);

    log.info("Query to getGatePassUnblockDeblackListingDetails {}", query);

    SqlRowSet rs = jdbcTemplate.queryForRowSet(query, params.toArray());

    while (rs.next()) {
        GatePassListingDto dto = new GatePassListingDto();

        dto.setTransactionId(rs.getString("TransactionId"));
        dto.setGatePassId(rs.getString("GatePassId"));
        dto.setFirstName(rs.getString("FirstName"));
        dto.setLastName(rs.getString("LastName"));
        dto.setGender(rs.getString("GMNAME"));
        dto.setDateOfBirth(rs.getString("DOB"));
        dto.setAadhaarNumber(rs.getString("AadharNumber"));
        dto.setContractorName(rs.getString("ContractorName"));
        dto.setVendorCode(rs.getString("VendorCode"));
        dto.setUnitName(rs.getString("UnitName"));

        String gatePassType = rs.getString("GatePassTypeId");

        if (GatePassType.CREATE.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Create");
        } else if (GatePassType.BLOCK.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Block");
        } else if (GatePassType.UNBLOCK.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Unblock");
        } else if (GatePassType.BLACKLIST.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Blacklist");
        } else if (GatePassType.DEBLACKLIST.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Deblacklist");
        } else if (GatePassType.CANCEL.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Cancel");
        } else if (GatePassType.LOSTORDAMAGE.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Lost/Damage");
        } else if (GatePassType.RENEW.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Renew");
        } else if (GatePassType.BULKCANCEL.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Bulk Cancel");
        } else if (GatePassType.BULKRENEW.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Bulk Renew");
        }

        String status = rs.getString("GatePassStatus");

        if (GatePassStatus.APPROVALPENDING.getStatus().equals(status)) {
            dto.setStatus("Approval Pending");
        } else if (GatePassStatus.APPROVED.getStatus().equals(status)) {
            dto.setStatus("Approved");
        } else if (GatePassStatus.REJECTED.getStatus().equals(status)) {
            dto.setStatus("Rejected");
        } else if (GatePassStatus.DRAFT.getStatus().equals(status)) {
            dto.setStatus("Draft");
        }

        dto.setOnboardingType(rs.getString("OnboardingType"));
        listDto.add(dto);
    }

    log.info("Exiting from getGatePassUnblockDeblackListingDetails dao method {}", listDto.size());
    return listDto;
}
public String getTransactionIdByGatePassId() {
	return QueryFileWatcher.getQuery("GET_TRANSACTIONID_BY_GATEPASSID");
}
public String getTransactionIdByGatePassId(String gatePassId) {
	String sql = getTransactionIdByGatePassId();
    //String sql = "SELECT TransactionId FROM GATEPASSMAIN WHERE GatePassId = ?";

    try {
        return jdbcTemplate.queryForObject(sql, new Object[]{gatePassId}, String.class);
    } catch (EmptyResultDataAccessException e) {
        return null; // No record found
    }
}

//@Override
//public String checkAadharUniqueness(String aadharNumber, String gatePassId, String transactionId) {
//String actionType = null;
//String gatePassIdNormalized = 
//(gatePassId == null || gatePassId.isBlank()) ? null : gatePassId;
//
//actionType = (gatePassIdNormalized == null) ? "CREATE" : "RENEW";
//
//
//String sql = this.getAadharExistsQuery();
//    return jdbcTemplate.queryForObject(
//            sql,
//            new Object[]{aadharNumber, transactionId, actionType,gatePassIdNormalized},
//            String.class
//    );
//}
@Override
public AadharCheckDto checkAadharUniqueness(String aadharNumber, String gatePassId, String transactionId) {

    String gatePassIdNormalized = 
            (gatePassId == null || gatePassId.trim().isEmpty() || "null".equalsIgnoreCase(gatePassId))
            ? null 
            : gatePassId.trim();

    String actionType = (gatePassIdNormalized == null) ? "CREATE" : "RENEW";

    String sql = this.getAadharExistsQuery();

    return jdbcTemplate.queryForObject(
        sql,
        new Object[]{aadharNumber, transactionId, actionType, gatePassIdNormalized},
        (rs, rowNum) -> {
            AadharCheckDto dto = new AadharCheckDto();
            dto.setStatus(rs.getString("AadhaarUniquenessStatus"));
            dto.setGatePassIds(rs.getString("GatePassIds"));
            return dto;
        }
    );
}
@Override
public String getAadharStatus(String aadharNumber) {
	// TODO Auto-generated method stub
	return null;
}

public String getContractWorkmenDetailsByGpIdRenew() {
	return QueryFileWatcher.getQuery("GET_CONTRACT_WORKMEN_DETAILS_BY_GPID_RENEW");
}
@Override
public GatePassMain getIndividualContractWorkmenDetailsByGatePassIdRenew(String gatePassId) {
	log.info("Entering into getIndividualContractWorkmenDetails dao method ");
	GatePassMain dto = null;
	String query = getContractWorkmenDetailsByGpIdRenew();
	log.info("Query to getIndividualContractWorkmenDetails "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,gatePassId);
	if(rs.next()) {
		dto = new GatePassMain();
		dto.setTransactionId(rs.getString("TransactionId"));
		dto.setGatePassId(rs.getString("GatePassId"));
		dto.setUnitId(rs.getString("peId"));
		dto.setGatePassAction(rs.getString("GatePassTypeId"));
		dto.setGatePassStatus(rs.getString("GatePassStatus"));
		dto.setAadhaarNumber(rs.getString("AadharNumber"));
		dto.setFirstName(rs.getString("FirstName"));
		dto.setLastName(rs.getString("LastName"));
		dto.setDateOfBirth(rs.getString("DOB"));
		dto.setGender(rs.getString("Gender"));
		dto.setRelationName(rs.getString("RelativeName"));
		dto.setIdMark(rs.getString("IdMark"));
		dto.setMobileNumber(rs.getString("MobileNumber"));
		dto.setMaritalStatus(rs.getString("MaritalStatus"));
		dto.setPrincipalEmployer(rs.getString("UnitId"));
		dto.setContractor(rs.getString("ContractorId"));
		dto.setWorkorder(rs.getString("WorkorderId"));
		dto.setTrade(rs.getString("TradeId"));
		dto.setSkill(rs.getString("SkillId"));
		dto.setDepartment(rs.getString("DepartmentId"));
		dto.setSubdepartment(rs.getString("AreaId"));
		dto.setEic(rs.getString("EicId"));
		dto.setNatureOfJob(rs.getString("NatureOfJob"));
		dto.setWcEsicNo(rs.getString("WcEsicNo"));
		dto.setHazardousArea(rs.getString("HazardousArea"));
		dto.setAccessArea(rs.getString("AccessAreaId"));
		dto.setUanNumber(rs.getString("UanNumber"));
		dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
		dto.setPfNumber(rs.getString("pfnumber"));
		dto.setEsicNumber(rs.getString("esicNumber"));
		dto.setBloodGroup(rs.getString("BloodGroupId"));
		dto.setAccommodation(rs.getString("Accommodation"));
		dto.setAcademic(rs.getString("AcademicId"));
		dto.setTechnical(rs.getString("Technical"));
		dto.setIfscCode(rs.getString("IfscCode"));
		dto.setAccountNumber(rs.getString("AccountNumber"));
		dto.setEmergencyName(rs.getString("EmergencyContactName"));
		dto.setEmergencyNumber(rs.getString("EmergencyContactNumber"));
		dto.setWageCategory(rs.getString("WorkmenWageCategoryId"));
		dto.setBonusPayout(rs.getString("BonusPayoutId"));
		dto.setZone(rs.getString("ZoneId"));
		dto.setBasic(new BigDecimal(rs.getString("Basic")));
		dto.setDa(new BigDecimal(rs.getString("DA")));
		dto.setHra(new BigDecimal(rs.getString("HRA")));
		dto.setWashingAllowance(new BigDecimal(rs.getString("WashingAllowance")));
		dto.setOtherAllowance(new BigDecimal(rs.getString("OtherAllowance")));
		dto.setUniformAllowance(new BigDecimal(rs.getString("UniformAllowance")));
		dto.setPfCap(rs.getString("PfCap"));
		dto.setCreatedBy(rs.getString("UpdatedBy"));
		dto.setAadharDocName(rs.getString("AadharDocName"));
		dto.setPhotoName(rs.getString("PhotoName"));
		dto.setPoliceVerificationDocName(rs.getString("PoliceVerificationDocName"));
		dto.setBankDocName(rs.getString("BankDocName"));
		dto.setIdProof2DocName(rs.getString("IdProof2DocName"));
		dto.setMedicalDocName(rs.getString("MedicalDocName"));
		dto.setForm11DocName(rs.getString("Form11DocName"));
		dto.setOtherDocName(rs.getString("OtherDocName"));
		dto.setTrainingDocName(rs.getString("TrainingDocName"));
		dto.setEducationDocName(rs.getString("EducationDocName"));
		dto.setComments(rs.getString("Comments"));
		dto.setAddress(rs.getString("Address"));
		dto.setDoj(rs.getString("DOJ"));
		dto.setPfApplicable(rs.getString("pfapplicable"));
		dto.setPoliceVerificationDate(rs.getString("policeverificationDate"));
		dto.setDot(rs.getString("DOT"));
		dto.setLlNo(rs.getString("LLNo"));
		dto.setAppointmentDocName(rs.getString("AppointmentDocName"));
		dto.setDisability(rs.getString("disability"));
		dto.setWorkmenType(rs.getString("WorkmenType"));
		dto.setProficiency(rs.getString("Proficiency"));
	}
	log.info("Exiting from getIndividualContractWorkmenDetails dao method "+gatePassId);
	return dto;
}
public String getActiveCountDetails() {
	return QueryFileWatcher.getQuery("GET_ACTIVE_COUNT_DETAILS");
}
@Override
public GatePassMain getActiveCountDetails(String transactionId) {
	log.info("Entering into getIndividualContractWorkmenDetails dao method ");
	GatePassMain dto = null;
	String query = getActiveCountDetails();
//	String query = "SELECT gpm.UnitId,gpm.ContractorId,gpm.WorkorderId,\r\n"
//			+ " gpm.WcEsicNo,gpm.LLNo,gpm.EsicNumber\r\n"
//			+ " FROM GATEPASSMAIN gpm \r\n"
//			+ " where  gpm.TransactionId=?"
//			+ " union "
//			+ " SELECT gpm.UnitId,gpm.ContractorId,gpm.WorkorderId,"
//			+ "	gpm.WcEsicNo,gpm.LLNo,gpm.EsicNumber"
//			+ "	FROM GATEPASSMAIN gpm "
//			+ " join GatePassTransactionMapping gptm on gptm.GatePassId = gpm.GatePassId"
//			+ " where gptm.TransactionId=? and gptm.GatePassTypeId='2' ";
	log.info("Query to getIndividualContractWorkmenDetails "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,transactionId,transactionId);
	if(rs.next()) {
		dto = new GatePassMain();
		dto.setTransactionId(transactionId);
		dto.setPrincipalEmployer(rs.getString("UnitId"));
		dto.setContractor(rs.getString("ContractorId"));
		dto.setWorkorder(rs.getString("WorkorderId"));
		dto.setWcEsicNo(rs.getString("WcEsicNo"));
		dto.setLlNo(rs.getString("LLNo"));
		dto.setEsicNumber(rs.getString("EsicNumber"));
		dto.setTrade(rs.getString("TradeId"));
		dto.setSkill(rs.getString("SkillId"));
		}
	log.info("Exiting from getIndividualContractWorkmenDetails dao method "+transactionId);
	return dto;
}
public String getRenewTransactionIfExists() {
	return QueryFileWatcher.getQuery("GET_RENEW_TRANSACTIONID_IF_EXISTS");
}
public String updateGatePassMainWithReasoningTab() {
	return QueryFileWatcher.getQuery("UPDATE_GATEPASSMAIN_WITH_REASONINGTAB");
}
@Override
public String getRenewTransactionIfExists(String gatePassId) {
	String transactionId=null;
	String query =getRenewTransactionIfExists(); 
	//String query = "select TransactionId from GatePassTransactionMapping where gatepassId=? and GatePassTypeId='2'  order by CreatedDate desc";
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,gatePassId);
	if(rs.next()) {
		transactionId = rs.getString("TransactionId");
	}
	return transactionId;
}

@Override
public boolean updateGatePassMainWithCancelReasoningTab(GatePassActionDto dto,MultipartFile exitFile,MultipartFile fnfFile,
        MultipartFile feedbackFile,MultipartFile rateManagerFile,MultipartFile locFile) {
	String sql =updateGatePassMainWithReasoningTab();
   // String sql = "update GATEPASSMAIN set Reasoning=?,ExitLetterDocName=?,FNFDocName=?,FeedbackFormDocName=?,RateManagerDocName=?,LOCDocName=? where GatePassId=?";
    try {

        String exitName = (exitFile != null && !exitFile.isEmpty()) ? "exitletter" : null;
        String fnfName = (fnfFile != null && !fnfFile.isEmpty()) ? "fnf" : null;
        String feedbackName = (feedbackFile != null && !feedbackFile.isEmpty()) ? "feedback" : null;
        String rateManagerName = (rateManagerFile != null && !rateManagerFile.isEmpty()) ? "ratemanager" : null;
        String locName = (locFile != null && !locFile.isEmpty()) ? "loc" : null;

        int result = jdbcTemplate.update(sql,
                dto.getReasoning(),
                exitName,
                fnfName,
                feedbackName,
                rateManagerName,
                locName,
                dto.getGatePassId());

        return result > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
@Override
public boolean updateCmsPersonCustDataEffectiveTillonDeblackUnblock(long personId,String dot){

    // 1. Get CSTMDEFID for Status
	String defSql = getCustomDefID();
    //String defSql = "SELECT CSTMDEFID FROM CMSPERSONCUSTOMDATADEFINITION "
    //              + "WHERE ISACTIVE = 1 AND CSTMDEFNAME = 'Status'";

    Integer defId = jdbcTemplate.queryForObject(defSql, Integer.class);

    if (defId == null) {
        return false; // No definition → nothing to update
    }

    // 2. Get latest REFID
    String refSql = getMaxRefID();
   // String refSql = "SELECT MAX(REFID) FROM CMSPERSONCUSTOMDATA "
   //               + "WHERE CSTMDEFID = ? AND EMPLOYEEID = ?";

    Long refId = jdbcTemplate.queryForObject(refSql, Long.class, defId, personId);

    if (refId == null || refId == 0) {
        return false; // No record → nothing to update
    }

    // 3. Update EFFECTIVETILL
    String updateSql = updateEffectiveTillDeblackUnblock();

        return jdbcTemplate.update(updateSql, dot, dot, refId) > 0;
}
public String updateDeblockUnblockEffectiveTill() {
    return QueryFileWatcher.getQuery("UPDATE_VALIDITY_TO_RENEW");
}
public String updateDeblockUnblockFromEffectiveTill() {
    return QueryFileWatcher.getQuery("UPDATE_VALIDITY_FROM_RENEW");
}
@Override
public boolean updatePersonStatusOnDeblockUnblock(Long activeId, Long inactiveId,String dot) {

    // Convert dot to LocalDate
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate dotDate = LocalDate.parse(dot, formatter);
    LocalDate today = LocalDate.now();

    // Decide dates
    LocalDate activeValidTo;
    LocalDate inactiveValidFrom;

    if (dotDate.isBefore(today)) {
        activeValidTo = today;
        inactiveValidFrom = today.plusDays(1);
    } else {
        activeValidTo = dotDate;
        inactiveValidFrom = dotDate.plusDays(1);
    }

    boolean updated = false;

    // ================= UPDATE ACTIVE RECORD =================
    if (activeId != null) {

        String sqlActive =updateDeblockUnblockEffectiveTill();
         //   "UPDATE CMSPERSONSTATUSMM SET VALIDTO = ? WHERE PERSONSTATUSMMID = ?";

        int count1 = jdbcTemplate.update(
                sqlActive,
                java.sql.Date.valueOf(activeValidTo),
                activeId
        );
        updated = updated || count1 > 0;
    }

    // ================= UPDATE INACTIVE RECORD =================
    if (inactiveId != null) {

        String sqlInactive =updateDeblockUnblockFromEffectiveTill();
           // "UPDATE CMSPERSONSTATUSMM SET VALIDFROM = ? WHERE PERSONSTATUSMMID = ?";

        int count2 = jdbcTemplate.update(
                sqlInactive,
                java.sql.Date.valueOf(inactiveValidFrom),
                inactiveId
        );
        updated = updated || count2 > 0;
    }

    return updated;
}
public String updateRenewEffectiveTill() {
    return QueryFileWatcher.getQuery("UPDATE_RENEW_EFFECTIVETILL");
}

@Override
public boolean updateCmsPersonCustDataRenewEffectiveTill(long personId,String dot) {

    // 1. Get CSTMDEFID for Status
	String defSql = getCustomDefID();
    //String defSql = "SELECT CSTMDEFID FROM CMSPERSONCUSTOMDATADEFINITION "
    //              + "WHERE ISACTIVE = 1 AND CSTMDEFNAME = 'Status'";

    Integer defId = jdbcTemplate.queryForObject(defSql, Integer.class);

    if (defId == null) {
        return false; // No definition → nothing to update
    }

    // 2. Get latest REFID
    String refSql = getMaxRefID();
   // String refSql = "SELECT MAX(REFID) FROM CMSPERSONCUSTOMDATA "
   //               + "WHERE CSTMDEFID = ? AND EMPLOYEEID = ?";

    Long refId = jdbcTemplate.queryForObject(refSql, Long.class, defId, personId);

    if (refId == null || refId == 0) {
        return false; // No record → nothing to update
    }

    // 3. Update EFFECTIVETILL
    String updateSql = updateRenewEffectiveTill();
    //String updateSql = "UPDATE CMSPERSONCUSTOMDATA SET EFFECTIVETILL = CONVERT(date, ?) WHERE REFID = ?";

    return jdbcTemplate.update(updateSql,dot, refId) > 0;
}
@Override
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
public String updateValidtodot() {
    return QueryFileWatcher.getQuery("UPDATE_VALIDITY_TO_RENEW");
}

public String updateValidfromdotnextday() {
    return QueryFileWatcher.getQuery("UPDATE_VALIDITY_FROM_RENEW");
}
@Override
public boolean updatePersonStatusValidityRenew(Long activeId, Long inactiveId, String dot) {

    boolean updated = false;

    // Convert dot to LocalDate
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate dotDate = LocalDate.parse(dot, formatter);
    LocalDate nextDay = dotDate.plusDays(1);

    // ================= UPDATE ACTIVE VALIDTO = dot =================
    if (activeId != null) {
        String sqlActive = updateValidtodot(); 
        // Must be: UPDATE CMSPERSONSTATUSMM SET VALIDTO = ? WHERE PERSONSTATUSMMID = ?

        int count1 = jdbcTemplate.update(
                sqlActive,
                java.sql.Date.valueOf(dotDate),
                activeId
        );
        updated = updated || count1 > 0;
    }

    // ================= UPDATE INACTIVE VALIDFROM = dot + 1 =================
    if (inactiveId != null) {
        String sqlInactive = updateValidfromdotnextday();
        // Must be: UPDATE CMSPERSONSTATUSMM SET VALIDFROM = ? WHERE PERSONSTATUSMMID = ?

        int count2 = jdbcTemplate.update(
                sqlInactive,
                java.sql.Date.valueOf(nextDay),
                inactiveId
        );
        updated = updated || count2 > 0;
    }

    return updated;
}
public String updateonboardingDocTypeinGP() {
	return QueryFileWatcher.getQuery("UPDATE_ONBOARDING_DOCUMENT_TYPE_IN_GP");
}
@Override
public void updateonboardingDocTypeinGP(String onboardingDocType, String transactionId) {
	String sql=updateonboardingDocTypeinGP();
	// "update GATEPASSMAIN set OnboardingDocType=? where TransactionId=?",
	jdbcTemplate.update(sql,onboardingDocType,transactionId);
	
}
public String getStateOnPrincipalEmployer() {
	return QueryFileWatcher.getQuery("GET_STATEID_BASED_ON_UNITID");
}
public String getMinimumWage() {
	return QueryFileWatcher.getQuery("GET_MINIMUMWAGE_DETAILS_FOR_WORKMEN");
}
@Override
public String getStateOnPrincipalEmployer(String principalEmployer) {
	String sql=getStateOnPrincipalEmployer();
	
	 // String sql = "select STATEID from CMSPRINCIPALEMPLOYER where UNITID=? and ISACTIVE=1";
    try {
        return jdbcTemplate.queryForObject(sql, String.class, principalEmployer);
    } catch (EmptyResultDataAccessException e) {
        return null; // State not found
    }
}

@Override
public Map<String, Object> getMinimumWage(String principalEmployer, String stateId, String zone, String skill) {

    Map<String, Object> result = new HashMap<>();
    
    String sql=getMinimumWage();
    
   // String sql = "select BASICWG,DA,OTHERALLOWANCE from CMSSTATEMINIMUMWAGE where UNITID=? and STATEID=? and ZONENM=? and SKILLID=? and DELETEDSW=0";

    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql,
            principalEmployer, stateId, zone, skill);

    if (!list.isEmpty()) {
        Map<String, Object> row = list.get(0);

        result.put("basic", row.get("BASICWG"));
        result.put("da", row.get("DA"));
        result.put("otherAllowance", row.get("OTHERALLOWANCE"));
    }

    return result;
}
public String updateGatePassMainWithAttachmentOfReasoning() {
	return QueryFileWatcher.getQuery("UPDATE_GATEPASSMAIN_WITH_ATTACHMENT_OF_REFERECNCE_REASONING");
}
@Override
public boolean updateGatePassMainWithReasoningTab(GatePassActionDto dto, MultipartFile attachmentOfReference) {
	String sql =updateGatePassMainWithAttachmentOfReasoning();
   // String sql = "update GATEPASSMAIN set Reasoning=? , AttachmentOfReference=? where GatePassId=?";
    try {

        String AttachmentOfReference = (attachmentOfReference != null && !attachmentOfReference.isEmpty()) ? "attachmentOfReference" : null;

        int result = jdbcTemplate.update(sql,
                dto.getReasoning(),
                AttachmentOfReference,
                dto.getGatePassId());

        return result > 0;

    } catch (Exception e) {
        e.printStackTrace();
        return false;
    }
}
public String getActiveCustomDefintion() {
	return QueryFileWatcher.getQuery("GET_ACTIVE_CUSTOM_DEFINITIONS");
}
@Transactional(rollbackFor = Exception.class)
@Override
public boolean saveCMSPERSONCUSTDATA(GatePassMain gp, long employeeId) {

    String sql = saveCMSPERSONCUSTDATA(); 
    // Expected SQL:
    // INSERT INTO CMSPERSONCUSTOMDATA
    // (EMPLOYEEID, CSTMDEFID, CUSTOMDATATEXT, EFFECTIVEFROM, EFFECTIVETILL, CREATEDTM, UPDATEDTM, UPDATEDBY)
    // VALUES (?, ?, ?, CONVERT(date, GETDATE()), ?, GETDATE(), GETDATE(), ?)

    // Fetch all active custom definitions
    //String defSql = "SELECT CSTMDEFID, CSTMDEFNAME FROM CMSPERSONCUSTOMDATADEFINITION WHERE ISACTIVE = 1";
    String defSql =getActiveCustomDefintion();
    List<Map<String, Object>> defList = jdbcTemplate.queryForList(defSql);

    boolean isQuick = "quick".equalsIgnoreCase(gp.getOnboardingType());

    // Allowed fields only for quick onboarding
    List<String> quickAllowed = Arrays.asList(
            "IdProof",
            "MobileNumber",
            "ContractorId",
            "UnitId",
            "GatePassType",
            "Status"
    );

    List<Object[]> batchArgs = new ArrayList<>();

    for (Map<String, Object> def : defList) {

        int defId = (Integer) def.get("CSTMDEFID");
        String fieldName = (String) def.get("CSTMDEFNAME");

        // If quick onboarding, allow only selected fields
        if (isQuick && !quickAllowed.contains(fieldName)) {
            continue;
        }

        String value = mapGatePassValue(fieldName, gp);

        // Skip null/empty values
        if (value == null || value.trim().isEmpty()) {
            continue;
        }

        // ✅ Set EFFECTIVETILL conditionally
        Object effectiveTill = "GatePassType".equalsIgnoreCase(fieldName)
                ? gp.getDot()              // only GatePassType gets DOT
                : "3000-01-01";           // others get default

        batchArgs.add(new Object[]{
                employeeId,        // ?
                defId,             // ?
                value,             // ?
                effectiveTill,     // ? (EFFECTIVETILL)
                gp.getCreatedBy()  // ?
        });
    }

    if (batchArgs.isEmpty()) {
        return false; // nothing to insert
    }

    jdbcTemplate.batchUpdate(sql, batchArgs);

    return true; // records inserted
}

public String getUpdateGatepassMainDOT() {
	return QueryFileWatcher.getQuery("UPDATE_GATEPASSMAIN_DOT");
}

@Override
public synchronized boolean updateDOTGatePassMainDOT(String gatePassId, String dot) {
	boolean res=false;
	Object[] object=new Object[]{dot,gatePassId};
	String query= getUpdateGatepassMainDOT();
	int i = jdbcTemplate.update(query,object);
	if(i>0){
		res=true;
	}
	return res;
}

@Override
public Map<String, Integer> getPlantCounts(String unitId) {
    String query = getPlantMaxWorkmenCount();

    return jdbcTemplate.queryForObject(query, new Object[]{unitId}, (rs, rowNum) -> {
        Map<String, Integer> map = new HashMap<>();
        map.put("maxCount", rs.getInt("MaxCount"));
        map.put("activeCount", rs.getInt("ActiveCount"));
        return map;
    });
}  
public String getAllZonesBasedOnPE() {
	return QueryFileWatcher.getQuery("GET_ALL_ZONES_BASED_ON_PE");
}
@Override
public List<Zone> getAllZonesBasedOnPE(String unitId){
	log.info("Entering into getAllZonesBasedOnPE dao method "+unitId);
	List<Zone> zoneList= new ArrayList<Zone>();
	String query = getAllZonesBasedOnPE();
	
	//String query = "select distinct cgm.GMID as ZoneId,cmssm.ZONENM as Zone  from CMSSTATEMINIMUMWAGE cmssm inner join CMSGENERALMASTER cgm on  cgm.GMNAME = cmssm.ZONENM where cmssm.UNITID=?";
	
	log.info("Query to getAllTradesBasedOnPE "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,unitId);
	while(rs.next()) {
		Zone zone = new Zone();
		zone.setZoneId(rs.getString("ZoneId"));
		zone.setZoneName(rs.getString("Zone"));
		zoneList.add(zone);
	}
	log.info("Exiting from getAllTradesBasedOnPE dao method "+zoneList.size());
	return zoneList;
}
public String getWorkmenDetailsByAadhar() {
	return QueryFileWatcher.getQuery("GET_BASIC_DETAILS_ON_AADHAR");
}
@Override
public Map<String, Object> getWorkmenDetailsByAadhar(String aadharNumber) {

	String sql = getWorkmenDetailsByAadhar();
//    String sql = "SELECT FirstName, LastName, RelativeName, DOB, Gender, " +
//                 "MobileNumber, MaritalStatus, disability, WorkmenType, Address " +
//                 "FROM GATEPASSMAIN WHERE AadharNumber = ? and GatePassTypeId=9 and GatePassStatus=4";

    List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, aadharNumber);

    return list.isEmpty() ? null : list.get(0);
}
private String getContractWorkmenReportQuery() {
	return QueryFileWatcher.getQuery("GET_CONTRACTOR_WORKMEN_REPORT");
}
@Override
public List<ContractWorkmenReportDTO> getContractWorkmenReportData(String unitId, String contractorId,String startDate,String endDate) {
    //String sql = "EXEC dbo.usp_GetApprovedGatePassExportDetails @UnitId = ?, @ContractorId = ?";
    String sql =getContractWorkmenReportQuery();
    return jdbcTemplate.query(sql, new Object[] { Long.parseLong(unitId), Long.parseLong(contractorId),startDate,endDate},
            new RowMapper<ContractWorkmenReportDTO>() {
                @Override
                public ContractWorkmenReportDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                	ContractWorkmenReportDTO dto = new ContractWorkmenReportDTO();

                    dto.setGatePassType(rs.getString("GatePassType"));
                    dto.setGatePassId(rs.getString("GatePassId"));
                    dto.setFirstName(rs.getString("FirstName"));
                    dto.setLastName(rs.getString("LastName"));
                    dto.setRelativeName(rs.getString("RelativeName"));
                    dto.setDoj(rs.getString("doj"));
                    dto.setBirthDate(rs.getString("birthDate"));
                    dto.setPhone1(rs.getString("phone1"));
                    dto.setAddress(rs.getString("Address"));
                    dto.setEmploymentStatus(rs.getString("employmentStatus"));
                    dto.setDot(rs.getString("DOT"));
                    dto.setReasoning(rs.getString("reasoning"));
                    dto.setUnitCode(rs.getString("unitCode"));
                    dto.setUnitName(rs.getString("unitName"));
                    dto.setMainContractorCode(rs.getString("MainContractorCode"));
                    dto.setMainContractorName(rs.getString("mainContractorName"));
                    dto.setSubContractorCode(rs.getString("subContractorCode"));
                    dto.setSubContractorName(rs.getString("subContractorName"));
                    dto.setSapWorkOrderNum(rs.getString("SAP_WORKORDER_NUM"));
                    dto.setDepartment(rs.getString("department"));
                    dto.setSection(rs.getString("section"));
                    dto.setTrade(rs.getString("trade"));
                    dto.setSkill(rs.getString("skill"));
                    dto.setAadharNumber(rs.getString("AadharNumber"));
                    dto.setGender(rs.getString("gender"));
                    dto.setMaritalStatus(rs.getString("MaritalStatus"));
                    dto.setHazardousArea(rs.getString("HazardousArea"));
                    dto.setAccessAreaId(rs.getString("AccessAreaId"));
                    dto.setTechnical(rs.getString("Technical"));
                    dto.setAcademic(rs.getString("academic"));
                    dto.setBloodgroup(rs.getString("bloodgroup"));
                    dto.setAccommodation(rs.getString("Accommodation"));
                    dto.setBankbranch(rs.getString("bankbranch"));
                    dto.setAccountNumber(rs.getString("AccountNumber"));
                    dto.setMobileNumber(rs.getString("MobileNumber"));
                    dto.setEicmanager(rs.getString("eicmanager"));
                    dto.setInsuranceType(rs.getString("insuranceType"));
                    dto.setWcesicno(rs.getString("wcesicno"));
                    dto.setEsicNumber(rs.getString("EsicNumber"));
                    dto.setLlNo(rs.getString("llNo"));
                    dto.setPfapplicable(rs.getString("pfapplicable"));
                    dto.setPfNumber(rs.getString("PfNumber"));
                    dto.setUanNumber(rs.getString("UanNumber"));
                    dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
                    dto.setZone(rs.getString("zone"));
                    dto.setPvsDocType(rs.getString("pvsDocType"));
                    dto.setEmergencyContactName(rs.getString("EmergencyContactName"));
                    dto.setEmergencyContactNumber(rs.getString("EmergencyContactNumber"));

                    return dto;
                }
            });
}

public String getInactiveWorkmenReportData() {
    return QueryFileWatcher.getQuery("GET_INACTIVE_WORKMEN_REPORT_DATA");
}

@Override
public List<ContractWorkmenReportDTO> getInactiveWorkmenReportData(String unitId, String contractorId) {
   // String sql = "EXEC dbo.usp_GetInactiveGatePassExportDetails @UnitId = ?, @ContractorId = ?";
    String sql =getInactiveWorkmenReportData();
    return jdbcTemplate.query(sql, new Object[] { Long.parseLong(unitId), Long.parseLong(contractorId) },
            new RowMapper<ContractWorkmenReportDTO>() {
                @Override
                public ContractWorkmenReportDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                	ContractWorkmenReportDTO dto = new ContractWorkmenReportDTO();

                    dto.setGatePassType(rs.getString("GatePassType"));
                    dto.setGatePassId(rs.getString("GatePassId"));
                    dto.setFirstName(rs.getString("FirstName"));
                    dto.setLastName(rs.getString("LastName"));
                    dto.setRelativeName(rs.getString("RelativeName"));
                    dto.setDoj(rs.getString("doj"));
                    dto.setBirthDate(rs.getString("birthDate"));
                    dto.setPhone1(rs.getString("phone1"));
                    dto.setAddress(rs.getString("Address"));
                    dto.setEmploymentStatus(rs.getString("employmentStatus"));
                    dto.setDot(rs.getString("DOT"));
                    dto.setReasoning(rs.getString("reasoning"));
                    dto.setUnitCode(rs.getString("unitCode"));
                    dto.setUnitName(rs.getString("unitName"));
                    dto.setMainContractorCode(rs.getString("MainContractorCode"));
                    dto.setMainContractorName(rs.getString("mainContractorName"));
                    dto.setSubContractorCode(rs.getString("subContractorCode"));
                    dto.setSubContractorName(rs.getString("subContractorName"));
                    dto.setSapWorkOrderNum(rs.getString("SAP_WORKORDER_NUM"));
                    dto.setDepartment(rs.getString("department"));
                    dto.setSection(rs.getString("section"));
                    dto.setTrade(rs.getString("trade"));
                    dto.setSkill(rs.getString("skill"));
                    dto.setAadharNumber(rs.getString("AadharNumber"));
                    dto.setGender(rs.getString("gender"));
                    dto.setMaritalStatus(rs.getString("MaritalStatus"));
                    dto.setHazardousArea(rs.getString("HazardousArea"));
                    dto.setAccessAreaId(rs.getString("AccessAreaId"));
                    dto.setTechnical(rs.getString("Technical"));
                    dto.setAcademic(rs.getString("academic"));
                    dto.setBloodgroup(rs.getString("bloodgroup"));
                    dto.setAccommodation(rs.getString("Accommodation"));
                    dto.setBankbranch(rs.getString("bankbranch"));
                    dto.setAccountNumber(rs.getString("AccountNumber"));
                    dto.setMobileNumber(rs.getString("MobileNumber"));
                    dto.setEicmanager(rs.getString("eicmanager"));
                    dto.setInsuranceType(rs.getString("insuranceType"));
                    dto.setWcesicno(rs.getString("wcesicno"));
                    dto.setEsicNumber(rs.getString("EsicNumber"));
                    dto.setLlNo(rs.getString("llNo"));
                    dto.setPfapplicable(rs.getString("pfapplicable"));
                    dto.setPfNumber(rs.getString("PfNumber"));
                    dto.setUanNumber(rs.getString("UanNumber"));
                    dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
                    dto.setZone(rs.getString("zone"));
                    dto.setPvsDocType(rs.getString("pvsDocType"));
                    dto.setEmergencyContactName(rs.getString("EmergencyContactName"));
                    dto.setEmergencyContactNumber(rs.getString("EmergencyContactNumber"));

                    return dto;
                }
            });
}
public String getPoliceverificationWorkmenReportData() {
    return QueryFileWatcher.getQuery("GET_POLICEVERIFICATION_WORKMEN_REPORT_DATA");
}
@Override
public List<ContractWorkmenReportDTO> getPoliceverificationWorkmenReportData(String unitId, String contractorId) {
    //String sql = "EXEC dbo.usp_GetPoliceverificationGatePassExportDetails @UnitId = ?, @ContractorId = ?";
    String sql = getPoliceverificationWorkmenReportData();
    return jdbcTemplate.query(sql, new Object[] { Long.parseLong(unitId), Long.parseLong(contractorId) },
            new RowMapper<ContractWorkmenReportDTO>() {
                @Override
                public ContractWorkmenReportDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                	ContractWorkmenReportDTO dto = new ContractWorkmenReportDTO();

                    dto.setGatePassType(rs.getString("GatePassType"));
                    dto.setGatePassId(rs.getString("GatePassId"));
                    dto.setFirstName(rs.getString("FirstName"));
                    dto.setLastName(rs.getString("LastName"));
                    dto.setRelativeName(rs.getString("RelativeName"));
                    dto.setDoj(rs.getString("doj"));
                    dto.setBirthDate(rs.getString("birthDate"));
                    dto.setPhone1(rs.getString("phone1"));
                    dto.setAddress(rs.getString("Address"));
                    dto.setEmploymentStatus(rs.getString("employmentStatus"));
                    dto.setDot(rs.getString("DOT"));
                    dto.setReasoning(rs.getString("reasoning"));
                    dto.setUnitCode(rs.getString("unitCode"));
                    dto.setUnitName(rs.getString("unitName"));
                    dto.setMainContractorCode(rs.getString("MainContractorCode"));
                    dto.setMainContractorName(rs.getString("mainContractorName"));
                    dto.setSubContractorCode(rs.getString("subContractorCode"));
                    dto.setSubContractorName(rs.getString("subContractorName"));
                    dto.setSapWorkOrderNum(rs.getString("SAP_WORKORDER_NUM"));
                    dto.setDepartment(rs.getString("department"));
                    dto.setSection(rs.getString("section"));
                    dto.setTrade(rs.getString("trade"));
                    dto.setSkill(rs.getString("skill"));
                    dto.setAadharNumber(rs.getString("AadharNumber"));
                    dto.setGender(rs.getString("gender"));
                    dto.setMaritalStatus(rs.getString("MaritalStatus"));
                    dto.setHazardousArea(rs.getString("HazardousArea"));
                    dto.setAccessAreaId(rs.getString("AccessAreaId"));
                    dto.setTechnical(rs.getString("Technical"));
                    dto.setAcademic(rs.getString("academic"));
                    dto.setBloodgroup(rs.getString("bloodgroup"));
                    dto.setAccommodation(rs.getString("Accommodation"));
                    dto.setBankbranch(rs.getString("bankbranch"));
                    dto.setAccountNumber(rs.getString("AccountNumber"));
                    dto.setMobileNumber(rs.getString("MobileNumber"));
                    dto.setEicmanager(rs.getString("eicmanager"));
                    dto.setInsuranceType(rs.getString("insuranceType"));
                    dto.setWcesicno(rs.getString("wcesicno"));
                    dto.setEsicNumber(rs.getString("EsicNumber"));
                    dto.setLlNo(rs.getString("llNo"));
                    dto.setPfapplicable(rs.getString("pfapplicable"));
                    dto.setPfNumber(rs.getString("PfNumber"));
                    dto.setUanNumber(rs.getString("UanNumber"));
                    dto.setPoliceverificationDate(rs.getString("PoliceverificationDate"));
                    dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
                    dto.setZone(rs.getString("zone"));
                    dto.setPvsDocType(rs.getString("pvsDocType"));
                    dto.setEmergencyContactName(rs.getString("EmergencyContactName"));
                    dto.setEmergencyContactNumber(rs.getString("EmergencyContactNumber"));

                    return dto;
                }
            });
}
public String getPolicyExpiryWorkmenReportData() {
    return QueryFileWatcher.getQuery("GET_POLICY_EXPIRY_WORKMEN_REPORT_DATA");
}
@Override
public List<ContractWorkmenReportDTO> getPolicyExpiryWorkmenReportData(String unitId, String contractorId) {
    //String sql = "EXEC dbo.usp_policyExpiryGatePassExportDetails @UnitId = ?, @ContractorId = ?";
    String sql = getPolicyExpiryWorkmenReportData();
    return jdbcTemplate.query(sql, new Object[] { Long.parseLong(unitId), Long.parseLong(contractorId) },
            new RowMapper<ContractWorkmenReportDTO>() {
                @Override
                public ContractWorkmenReportDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
                	ContractWorkmenReportDTO dto = new ContractWorkmenReportDTO();

                    dto.setGatePassType(rs.getString("GatePassType"));
                    dto.setGatePassId(rs.getString("GatePassId"));
                    dto.setFirstName(rs.getString("FirstName"));
                    dto.setLastName(rs.getString("LastName"));
                    dto.setRelativeName(rs.getString("RelativeName"));
                    dto.setDoj(rs.getString("doj"));
                    dto.setBirthDate(rs.getString("birthDate"));
                    dto.setPhone1(rs.getString("phone1"));
                    dto.setAddress(rs.getString("Address"));
                    dto.setEmploymentStatus(rs.getString("employmentStatus"));
                    dto.setDot(rs.getString("DOT"));
                    dto.setReasoning(rs.getString("reasoning"));
                    dto.setUnitCode(rs.getString("unitCode"));
                    dto.setUnitName(rs.getString("unitName"));
                    dto.setMainContractorCode(rs.getString("MainContractorCode"));
                    dto.setMainContractorName(rs.getString("mainContractorName"));
                    dto.setSubContractorCode(rs.getString("subContractorCode"));
                    dto.setSubContractorName(rs.getString("subContractorName"));
                    dto.setSapWorkOrderNum(rs.getString("SAP_WORKORDER_NUM"));
                    dto.setWorkorderExpiryDate(rs.getString("WOVALIDTO"));
                    dto.setInsuranceType(rs.getString("insuranceType"));
                    dto.setWcesicno(rs.getString("wcesicno"));
                    dto.setWcesicExpiryDate(rs.getString("wcesiceTODate"));
                    dto.setLlNo(rs.getString("llNo"));
                    dto.setLicenseExpiryDate(rs.getString("licenseValidTo"));
                    dto.setEsicNumber(rs.getString("EsicNumber"));
                    dto.setDepartment(rs.getString("department"));
                    dto.setSection(rs.getString("section"));
                    dto.setTrade(rs.getString("trade"));
                    dto.setSkill(rs.getString("skill"));
                    dto.setAadharNumber(rs.getString("AadharNumber"));
                    dto.setGender(rs.getString("gender"));
                    dto.setMaritalStatus(rs.getString("MaritalStatus"));
                    dto.setHazardousArea(rs.getString("HazardousArea"));
                    dto.setAccessAreaId(rs.getString("AccessAreaId"));
                    dto.setTechnical(rs.getString("Technical"));
                    dto.setAcademic(rs.getString("academic"));
                    dto.setBloodgroup(rs.getString("bloodgroup"));
                    dto.setAccommodation(rs.getString("Accommodation"));
                    dto.setBankbranch(rs.getString("bankbranch"));
                    dto.setAccountNumber(rs.getString("AccountNumber"));
                    dto.setMobileNumber(rs.getString("MobileNumber"));
                    dto.setEicmanager(rs.getString("eicmanager"));
                    dto.setPfapplicable(rs.getString("pfapplicable"));
                    dto.setPfNumber(rs.getString("PfNumber"));
                    dto.setUanNumber(rs.getString("UanNumber"));
                    dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
                    dto.setZone(rs.getString("zone"));
                    dto.setPvsDocType(rs.getString("pvsDocType"));
                    dto.setEmergencyContactName(rs.getString("EmergencyContactName"));
                    dto.setEmergencyContactNumber(rs.getString("EmergencyContactNumber"));

                    return dto;
                }
            });
}

public String getDOJFromGPM() {
    return QueryFileWatcher.getQuery("GET_DOJ_FROM_GATEPASSMAIN");
}
public String updatePERSONJOBHISTValidtoYesterday() {
    return QueryFileWatcher.getQuery("UPDATE_PERSONJOBHIST_VALIDTO_YESTERDAY");
}
public String insertIntoPERSONJOBHISTOnRenewal() {
    return QueryFileWatcher.getQuery("INSERT_PERSONJOBHIST_ON_RENEWAL");
}
@Override
public boolean updateCmsPersonJobHistRenew(GatePassMain gpm, long personId) {

    try {

    	 // STEP 1: Get data from GATEPASSMAIN
    	String fetchSql =getDOJFromGPM();
        //String fetchSql ="select DOJ from GATEPASSMAIN where GatePassId=?";
               

        GatePassMain data = jdbcTemplate.queryForObject(
                fetchSql,
                new Object[]{gpm.getGatePassId()},
                (rs, rowNum) -> {

                    GatePassMain gp = new GatePassMain();
                    gp.setDoj(rs.getString("DOJ"));
                    return gp;
                }
        );
    	
        // STEP 1: Expire existing record
        String updateSql =updatePERSONJOBHISTValidtoYesterday();
       // String updateSql ="UPDATE CMSPERSONJOBHIST SET VALIDTO = DATEADD(DAY, -1, GETDATE()) WHERE EMPLOYEEID = ? " ;
               

        jdbcTemplate.update(updateSql, personId);

        // STEP 2: Insert new record
        String insertSql =insertIntoPERSONJOBHISTOnRenewal();
//        String insertSql =
//                "INSERT INTO CMSPERSONJOBHIST ( " +
//                "EMPLOYEEID, TRADEID,SKILLID,UNITID, CONTRACTORID, DEPARTMENTID, " +
//                "SUBDEPARTMENTID, WORKORDERID, EICID, VALIDFROM, VALIDTO ,UPDATEDTM) " +
//                "VALUES (?,?,?,?,?,?,?, ?, ?,?,?,getdate())";

        int inserted = jdbcTemplate.update(
                insertSql,
                personId,
                gpm.getTrade(),
                gpm.getSkill(),
                gpm.getUnitId(),
                gpm.getContractor(),
                gpm.getDepartment(),
                gpm.getSubdepartment(),
                gpm.getWorkorder(),
                gpm.getEic(),
               // java.sql.Date.valueOf(gpm.getDoj()),
                data.getDoj(),
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

@Override
public void updateGatePassIdByEmpCode(GatePassMain gatePassMain) {

    String sql = getUpdateGatepassid();

    jdbcTemplate.update(sql,gatePassMain.getGatePassId(),gatePassMain.getTransactionId());
}
public String getTrainingDetails() {
    return QueryFileWatcher.getQuery("GET_TRAININGTYPE_TRAININGNAME_DETAILS");
}
@Override
public List<ApproveRejectGatePassDto> getTrainingDetails(String unitId, String department) {

	 String sql =getTrainingDetails();
   // String sql ="SELECT TRAINING_TYPE, TRAINING_NAME FROM CMSTRAININGDETAILS WHERE UNIT_ID = ? AND DEP_NAME = ? AND MANDATORY = 'YES'";

    return jdbcTemplate.query(sql,new Object[]{unitId, department},new RowMapper<ApproveRejectGatePassDto>() {

                @Override
                public ApproveRejectGatePassDto mapRow(ResultSet rs, int rowNum)
                        throws SQLException {

                	ApproveRejectGatePassDto obj = new ApproveRejectGatePassDto();

                    obj.setTrainingType(rs.getString("TRAINING_TYPE"));

                    obj.setTrainingName(rs.getString("TRAINING_NAME"));

                    return obj;
                }
            }
    );
}
//private String getMaxTrainingIdQuery() {
//	return QueryFileWatcher.getQuery("GET_MAX_TRAININGID_FOR_GATEPASSID");
//}

public String generateTrainingIdForGatePasses() {
    return QueryFileWatcher.getQuery("GENERATE_TRAININGID_FOR_GATEPASS");
}

public synchronized String generateTrainingIdForGatePass() {

    String trainingId = "";

    try {

        // SQL SERVER QUERY
    	String sql =generateTrainingIdForGatePasses();
//        String sql =
//                "SELECT ISNULL(MAX(CAST(SUBSTRING(SFTID, 3, LEN(SFTID)) AS INT)), 0) " +
//                "FROM CMSPERSONSAFETYMM " +
//                "WHERE SFTID IS NOT NULL " +
//                "AND SFTID LIKE 'TR%'";

        Integer maxId = jdbcTemplate.queryForObject(sql, Integer.class);

        // IF NULL
        if (maxId == null) {
            maxId = 0;
        }

        // NEXT ID
        int nextId = maxId + 1;

        // FORMAT -> TR00001
        trainingId = String.format("TR%05d", nextId);

        log.info("Generated Training ID : {}", trainingId);

    } catch (Exception e) {

        log.error("Error generating Training ID", e);
    }

    return trainingId;
}
public String isExistingTrainingId() {
    return QueryFileWatcher.getQuery("IS_TRAININGID_EXISTS");
}
public String deleteOlderTrainingRecordsOfGP() {
    return QueryFileWatcher.getQuery("DELETE_OLDER_TRAINING_RECORDS_OF_GP");
}
public String updateTrainingIdInGatepassMain() {
    return QueryFileWatcher.getQuery("UPDATE_TRAININGID_IN_GATEPASSMAIN");
}
public String insertTrainingRecordsInSafetyMM() {
    return QueryFileWatcher.getQuery("INSERT_TRAINING_RECORDS_IN_SAFETYMM");
}
@Override
public void saveTrainingDetails(ApproveRejectGatePassDto dto) {

    String transactionId = dto.getTransactionId();

    String sftId = null;

    //STEP 1:CHECK TRAINING ID EXISTS
    String checkSql =isExistingTrainingId();
   // String checkSql ="SELECT TRAININGID FROM GATEPASSMAIN WHERE TRANSACTIONID = ?";

    List<String> existingIds = jdbcTemplate.query(checkSql,new Object[]{transactionId},(rs, rowNum) -> rs.getString("TRAININGID"));

    // STEP 2:IF EXISTS USE SAME SFTID
    if (existingIds != null && !existingIds.isEmpty() && existingIds.get(0) != null) {

        sftId = existingIds.get(0);

        //STEP3 : DELETE OLD RECORDS
        String deleteSql =deleteOlderTrainingRecordsOfGP();
        //String deleteSql ="DELETE FROM CMSPERSONSAFETYMM WHERE SFTID = ?";

        jdbcTemplate.update(deleteSql, sftId);

    } else {
        //STEP4: GENERATE NEW TRAINING ID
        sftId = generateTrainingIdForGatePass();
       
        //STEP5:  UPDATE GATEPASSMAIN
        String updateSql =updateTrainingIdInGatepassMain();
        //String updateSql ="UPDATE GATEPASSMAIN SET TRAININGID = ? WHERE TRANSACTIONID = ?";

        jdbcTemplate.update(updateSql,sftId,transactionId);
    }

    //STEP6: BATCH INSERT TRAINING RECORDS
    String insertSql =insertTrainingRecordsInSafetyMM();
//    String insertSql ="INSERT INTO CMSPERSONSAFETYMM(SFTID,DATETAKEN,FRMTIME,TOTIME, NEXTDATE,TRNDESC,DEPTID,SECID,FUNC,NJOB,MODULE,TNI,FACULTYNM,VENUE,\r\n"
//    		+ "  PREMO,PREMM,PREPERCENT,POSTMO,POSTMM,POSTPERCENT,RECOM,REMARKS,UPDATEDBY,UPDATEDON,PMMID)\r\n"
//    		+ "     VALUES(?,?,?,?,?,?,null,null,null,null,null,?,?,null,null,null,null,null,null,?,?,?,?,getdate(),'1')";



    List<Object[]> batchArgs = new ArrayList<>();

     //LOOP TRAINING ROWS
    for (ApproveRejectGatePassDto training : dto.getTrainingDetailsList()) {

        Object[] obj = new Object[]{sftId,
        		                training.getTrainingFromDate(),
        		                convertToSqlTime(training.getFromTime()),
        		                convertToSqlTime(training.getToTime()),
        						training.getNextTrainingDate(),
        						training.getTrainingName(),
        		                training.getTrainingType(),
        		                training.getFaculty(),
        		                training.getMarks(),
        						training.getEfficency(),
        						training.getRemarks(),
        						dto.getApproverId()
        };

        batchArgs.add(obj);
    }
     // FINAL BATCH INSERT
    jdbcTemplate.batchUpdate(insertSql,batchArgs);
}
public String getExistingTrainingRecords() {
    return QueryFileWatcher.getQuery("GET_EXISTSTING_TRAINING_RECORDS");
}
@Override
public List<ApproveRejectGatePassDto> getExistingTrainingRecords(String transactionId) {
	String sql =getExistingTrainingRecords();
    //String sql ="select CONVERT(varchar, mm.DATETAKEN, 23) AS TRAINING_FROM_DATE,'' as TRAINING_TO_DATE ,LEFT(CONVERT(varchar, mm.FRMTIME, 108), 5) AS FROM_TIME,LEFT(CONVERT(varchar, mm.TOTIME, 108), 5) AS TO_TIME,CONVERT(varchar, mm.NEXTDATE, 23) AS NEXT_TRAINING_DATE,mm.TRNDESC as TRAINING_NAME,mm.TNI as TRAINING_TYPE,mm.FACULTYNM as FACULTY,mm.POSTPERCENT as MARKS,mm.RECOM as EFFICIENCY,mm.REMARKS as REMARKS from CMSPERSONSAFETYMM mm JOIN GATEPASSMAIN GPM  ON GPM.TRAININGID = MM.SFTID WHERE GPM.TRANSACTIONID = ?";
            
    return jdbcTemplate.query(sql,new Object[]{transactionId},new RowMapper<ApproveRejectGatePassDto>() {

                @Override
                public ApproveRejectGatePassDto mapRow(ResultSet rs,int rowNum) throws SQLException {

                	ApproveRejectGatePassDto obj = new ApproveRejectGatePassDto();

                    obj.setTrainingType(rs.getString("TRAINING_TYPE"));
                    obj.setTrainingName(rs.getString("TRAINING_NAME"));
                    obj.setTrainingFromDate(rs.getString("TRAINING_FROM_DATE"));
                    obj.setTrainingToDate(rs.getString("TRAINING_TO_DATE"));
                    obj.setFromTime(rs.getString("FROM_TIME"));
                    obj.setToTime(rs.getString("TO_TIME"));
                    obj.setFaculty(rs.getString("FACULTY"));
                    obj.setMarks(rs.getString("MARKS"));
                    obj.setEfficency(rs.getString("EFFICIENCY"));
                    obj.setNextTrainingDate(rs.getString("NEXT_TRAINING_DATE"));
                    obj.setRemarks(rs.getString("REMARKS"));

                    return obj;
                }
            });
}
private String convertToSqlTime(String time) {
    if (time == null || time.isEmpty()) {
        return null;
    }
    // Convert 10.00 -> 10:00:00
    return time.replace(".", ":") + ":00";
}

public String insertActiveRecordInStatusMM() {
    return QueryFileWatcher.getQuery("INSERT_ACTIVE_RECORD_IN_STATUSMM");
}
public String insertInactiveRecordInStatusMM() {
    return QueryFileWatcher.getQuery("INSERT_INACTIVE_RECORD_IN_STATUSMM");
}
public String updateInactiveRecordValidToInStatusMM() {
    return QueryFileWatcher.getQuery("UPDATE_INACTIVE_RECORD_VALIDTO_IN_STATUSMM");
}

@Override
public boolean insertActiveStatusRecord(long employeeId,LocalDate validFrom,LocalDate validTo) {
	String sql =insertActiveRecordInStatusMM();
/// String sql = "INSERT INTO CMSPERSONSTATUSMM ( EMPLOYEEID , ISACTIVE , VALIDFROM , VALIDTO)  VALUES (?,?,?,? )";
     return jdbcTemplate.update(sql,employeeId,1,validFrom,validTo) > 0;
}
@Override
public boolean insertInactiveStatusRecord(long employeeId,LocalDate validFrom,LocalDate validTo) {
	String sql =insertInactiveRecordInStatusMM();
//String sql = "INSERT INTO CMSPERSONSTATUSMM ( EMPLOYEEID , ISACTIVE , VALIDFROM , VALIDTO)  VALUES (?,?,?,? )";
     return jdbcTemplate.update(sql,employeeId,0,validFrom,validTo) > 0;
}
@Override
public boolean updateInactiveValidTo(Long inactiveId, LocalDate minusDays){
	String sql =updateInactiveRecordValidToInStatusMM();
//String sql ="UPDATE CMSPERSONSTATUSMM SET VALIDTO = ? WHERE PERSONSTATUSMMID = ?";

   return jdbcTemplate.update(sql,minusDays,inactiveId) > 0;
}
public String isActionDoneToday() {
    return QueryFileWatcher.getQuery("IS_ACTION_DONE_TODAY");
}
@Override
public boolean isActionDoneToday(String gatePassId, Long typeId) {
	String sql =isActionDoneToday();
    //String sql = "SELECT 1 FROM (SELECT TOP 1 CreatedDate FROM GatePassTransactionMapping WHERE GatePassId = ? AND GatePassTypeId = ? ORDER BY CreatedDate DESC) t WHERE CAST(t.CreatedDate AS DATE) = CAST(GETDATE() AS DATE)";

    List<Integer> result = jdbcTemplate.query(sql,new Object[]{gatePassId, typeId},(rs, rowNum) -> rs.getInt(1));

    return !result.isEmpty();
}
public String getoldDotFromActiveRecord() {
    return QueryFileWatcher.getQuery("GET_OLD_DOT_FROM_ACTIVE_RECORDS");
}
@Override
public String getoldDotFromActiveRecord(Long activeId) {
	String query=getoldDotFromActiveRecord();
	//String query="select CONVERT(varchar, VALIDTO, 23) AS  VALIDTO from CMSPERSONSTATUSMM where PERSONSTATUSMMID=?";
	String oldDot = null;
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,activeId);
	if(rs.next()) {
		oldDot = rs.getString("VALIDTO");
	}
	return oldDot;
}
@Transactional(rollbackFor = Exception.class)
@Override
public String saveFullTimeContractorGatePass(GatePassMain gatePassMain) {
    log.info("Entering into saveGatePass dao method");
    String transId=gatePassMain.getTransactionId();
   
	
    	log.info("Gatepass generated for Aadhaar: " + gatePassMain.getAadhaarNumber() + " is: " + transId);
        Object[] parameters = prepareFullTimeContractorGatePassParameters(transId, gatePassMain); 
        try {
        	String query = getSaveContractWorkmen();
            int result = jdbcTemplate.update(query, parameters);
            if (result > 0) {
                log.info("GatePass saved successfully for transId: " + transId);
            } else {
                log.warn("Failed to save GatePass for transId: " + transId);
                throw new RuntimeException("Failed to save GatePass transId: " + transId);
            }
        } catch (Exception e) {
            log.error("Error saving GatePass for transId: " + transId, e);
            //return null;
            throw new RuntimeException(e);
        }
        return transId;
}
private Object[] prepareFullTimeContractorGatePassParameters(String transId, GatePassMain gatePassMain) {
	//String gatePassType = gatePassMain.getOnboardingType().equals("project")?GatePassType.PROJECT.getStatus():GatePassType.CREATE.getStatus();
    return new Object[]{
    		transId," ",
    		gatePassMain.getGatePassAction(),
        gatePassMain.getGatePassStatus(),
        gatePassMain.getAadhaarNumber(),
        gatePassMain.getFirstName(),
        gatePassMain.getLastName(),
        gatePassMain.getDateOfBirth(),
        gatePassMain.getGender(),
        gatePassMain.getRelationName(),
        gatePassMain.getIdMark(),
        gatePassMain.getMobileNumber(),
        gatePassMain.getMaritalStatus(),
        gatePassMain.getPrincipalEmployer(),
        gatePassMain.getContractor(),
        gatePassMain.getWorkorder()!=null?gatePassMain.getWorkorder():"",
        gatePassMain.getTrade()!=null?gatePassMain.getTrade():"",
        gatePassMain.getSkill()!=null?gatePassMain.getSkill():"",
        gatePassMain.getDepartment(),
        gatePassMain.getSubdepartment(),
        gatePassMain.getEic()!=null?gatePassMain.getEic():"",
        gatePassMain.getNatureOfJob()!=null?gatePassMain.getNatureOfJob():"",
        gatePassMain.getWcEsicNo()!=null?gatePassMain.getWcEsicNo():"",
        gatePassMain.getHazardousArea()!=null?gatePassMain.getHazardousArea():"",
        gatePassMain.getAccessArea()!=null?gatePassMain.getAccessArea():"",
        gatePassMain.getUanNumber()!=null?gatePassMain.getUanNumber():"",
        gatePassMain.getHealthCheckDate()!=null?gatePassMain.getHealthCheckDate():"",
        gatePassMain.getPfNumber()!=null?gatePassMain.getPfNumber():"",
        gatePassMain.getEsicNumber()!=null?gatePassMain.getEsicNumber():"",
        gatePassMain.getBloodGroup(),
        gatePassMain.getAccommodation(),
        gatePassMain.getAcademic(),
        gatePassMain.getTechnical(),
        gatePassMain.getIfscCode(),
        gatePassMain.getAccountNumber(),
        gatePassMain.getEmergencyName(),
        gatePassMain.getEmergencyNumber(),
        gatePassMain.getWageCategory()!=null?gatePassMain.getWageCategory():"",
        gatePassMain.getBonusPayout()!=null?gatePassMain.getBonusPayout():"",
        gatePassMain.getPfCap()!=null?gatePassMain.getPfCap():"",
        gatePassMain.getZone()!=null?gatePassMain.getZone():"",
        gatePassMain.getBasic()!=null?gatePassMain.getBasic():"",
        gatePassMain.getDa()!=null?gatePassMain.getDa():"",
        gatePassMain.getHra()!=null?gatePassMain.getHra():"",
        gatePassMain.getWashingAllowance()!=null?gatePassMain.getWashingAllowance():"",
        gatePassMain.getOtherAllowance()!=null?gatePassMain.getOtherAllowance():"",
        gatePassMain.getUniformAllowance()!=null?gatePassMain.getUniformAllowance():"",
        gatePassMain.getAadharDocName(),gatePassMain.getPhotoName(),gatePassMain.getBankDocName(),
        gatePassMain.getPoliceVerificationDocName(),gatePassMain.getIdProof2DocName(),gatePassMain.getMedicalDocName(),
        gatePassMain.getEducationDocName(),gatePassMain.getForm11DocName(),gatePassMain.getTrainingDocName(),gatePassMain.getOtherDocName(),
        gatePassMain.getWorkFlowType(),
        gatePassMain.getComments()!=null?gatePassMain.getComments():"",
        		gatePassMain.getAddress()!=null?gatePassMain.getAddress():"",
        				gatePassMain.getDoj(),gatePassMain.getPfApplicable()!=null?gatePassMain.getPfApplicable():"",gatePassMain.getPoliceVerificationDate()!=null?gatePassMain.getPoliceVerificationDate():"",gatePassMain.getDot()!=null?gatePassMain.getDot():"",
        gatePassMain.getUserId(),
        gatePassMain.getOnboardingType(),gatePassMain.getLlNo()!=null?gatePassMain.getSkill():"",gatePassMain.getAppointmentDocName(),gatePassMain.getDisability(),gatePassMain.getWorkmenType(),gatePassMain.getProficiency()!=null?gatePassMain.getSkill():""
        	,gatePassMain.getIsmw()!=null?gatePassMain.getIsmw():""};

}
@Transactional(rollbackFor = Exception.class)
@Override
public long saveFullTimeContractorIntoCMSPerson(CMSPerson person) {
	String sql= saveIntoCMSPerson();
   // String sql = "INSERT INTO CMSPERSON (EMPLOYEECODE, FIRSTNAME, RELATIONNAME, LASTNAME, DATEOFBIRTH, DATEOFJOINING, " +
   //         "DATEOFTERMINATION, BLOODGROUP, HAZARDOUSAREA, GENDER, ACADEMICS, ACCOMODATION, BANKBRANCH, ACCOUNTNO, " +
   //         "EMERGENCYNAME, EMERGENCYNUMBER, MOBILENUMBER, ACCESSLEVEL, ESICNUMBER, UANNUMBER, ISPFELIGIBLE, IDMARK, " +
   //         "PANNO, UPDATEDBY, AADHARNUMBER) " +
   //         "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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
        person.getDateOfJoining(),
       // person.getDateOfTermination(),
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
            return -1;
        }
    } catch (Exception e) {
        log.error("Error inserting into CMSPerson for EmployeeCode: " + person.getEmployeeCode(), e);
        return -1;
    }
}
@Transactional(rollbackFor = Exception.class)
@Override
public boolean saveFullTimeContractorIntoCMSPERSONJOBHIST(GatePassMain gpm, long employeeId) {
	boolean result = false;
	String sql = saveIntoCMSPERSONJOBHIST();
	// String sql = "INSERT INTO CMSPERSONJOBHIST ( EMPLOYEEID , TRADEID , SKILLID , UNITID , CONTRACTORID , DEPARTMENTID , "
	// 		+ " SUBDEPARTMENTID , WORKORDERID , EICID , VALIDFROM , VALIDTO  ) "
	// 		+ "     VALUES (?,?,?,?,?,?,?,?,?,?,?)";
     Object[] parameters = new Object[] {employeeId,gpm.getTrade(),gpm.getSkill(),gpm.getUnitId(),gpm.getContractor(),gpm.getDepartment(),
    		 gpm.getSubdepartment(),gpm.getWorkorder(),gpm.getEic(),gpm.getDoj(), "1/1/3000"};
     try {
     int status = jdbcTemplate.update(sql, parameters);
     if (status > 0) {
     	result=true;
     }else {
         log.warn("Failed to create GatePass action for GatePassId: " + gpm.getGatePassId());
     }
     }catch (Exception e) {
         log.error("Error creating GatePass action for GatePassId: " + gpm.getGatePassId(), e);
         return false;
     }
     return result;
}
@Transactional(rollbackFor = Exception.class)
@Override
public boolean saveFullTimeContractorCMSPERSONSTATUSMM(GatePassMain gpm, long employeeId) {
	
	boolean result = false;
	String sql = saveCMSPERSONSTATUSMM();
	// String sql = "INSERT INTO CMSPERSONSTATUSMM ( EMPLOYEEID , ISACTIVE , VALIDFROM , VALIDTO)  VALUES (?,?,?,? )";
    Object[] parameters = new Object[] {employeeId,1,gpm.getDoj(),"1/1/3000"};
    try {
    int status = jdbcTemplate.update(sql, parameters);
    if (status > 0) {
    	result=true;
    }else {
        log.warn("Failed to create GatePass action for GatePassId: " + gpm.getGatePassId());
    }
    }catch (Exception e) {
        log.error("Error creating GatePass action for GatePassId: " + gpm.getGatePassId(), e);
        return false;
    }
    return result;
}
@Transactional(rollbackFor = Exception.class)
@Override
public boolean saveFullTimeContractorCMSPERSONCUSTOMDATA(GatePassMain gp, long employeeId) {

    String sql = saveCMSPERSONCUSTDATA(); 
    // Expected SQL:
    // INSERT INTO CMSPERSONCUSTOMDATA
    // (EMPLOYEEID, CSTMDEFID, CUSTOMDATATEXT, EFFECTIVEFROM, EFFECTIVETILL, CREATEDTM, UPDATEDTM, UPDATEDBY)
    // VALUES (?, ?, ?, CONVERT(date, GETDATE()), ?, GETDATE(), GETDATE(), ?)

    // Fetch all active custom definitions
    //String defSql = "SELECT CSTMDEFID, CSTMDEFNAME FROM CMSPERSONCUSTOMDATADEFINITION WHERE ISACTIVE = 1";
    String defSql =getActiveCustomDefintion();
    List<Map<String, Object>> defList = jdbcTemplate.queryForList(defSql);


    List<Object[]> batchArgs = new ArrayList<>();

    for (Map<String, Object> def : defList) {

        int defId = (Integer) def.get("CSTMDEFID");
        String fieldName = (String) def.get("CSTMDEFNAME");


        String value = mapGatePassValue(fieldName, gp);

        // Skip null/empty values
        if (value == null || value.trim().isEmpty()) {
            continue;
        }

        // ✅ Set EFFECTIVETILL conditionally
        Object effectiveTill = "GatePassType".equalsIgnoreCase(fieldName)
                ? "3000-01-01"              // only GatePassType gets DOT
                : "3000-01-01";           // others get default

        batchArgs.add(new Object[]{
                employeeId,        // ?
                defId,             // ?
                value,             // ?
                effectiveTill,     // ? (EFFECTIVETILL)
                gp.getCreatedBy()  // ?
        });
    }

    if (batchArgs.isEmpty()) {
        return false; // nothing to insert
    }

    jdbcTemplate.batchUpdate(sql, batchArgs);

    return true; // records inserted
}
public String getFullTimeContractWorkmenDetailsByTransId() {
    return QueryFileWatcher.getQuery("GET_FULL_TIME_CONTRACTOR_WORKMEN_BY_TRANSACTIONID");
}
@Override
public GatePassMain getFullTimeIndividualContractWorkmenDetails(String transactionId) {
	log.info("Entering into getIndividualContractWorkmenDetails dao method ");
	GatePassMain dto = null;
	String query = getFullTimeContractWorkmenDetailsByTransId();
	log.info("Query to getIndividualContractWorkmenDetails "+query);
	SqlRowSet rs = jdbcTemplate.queryForRowSet(query,transactionId);
	if(rs.next()) {
		dto = new GatePassMain();
		dto.setTransactionId(rs.getString("TransactionId"));
		dto.setGatePassId(rs.getString("GatePassId"));
		dto.setUnitId(rs.getString("peId"));
		dto.setGatePassAction(rs.getString("GatePassTypeId"));
		dto.setGatePassStatus(rs.getString("GatePassStatus"));
		dto.setAadhaarNumber(rs.getString("AadharNumber"));
		dto.setFirstName(rs.getString("FirstName"));
		dto.setLastName(rs.getString("LastName"));
		dto.setDateOfBirth(rs.getString("DOB"));
		dto.setGender(rs.getString("Gender"));
		dto.setRelationName(rs.getString("RelativeName"));
		dto.setIdMark(rs.getString("IdMark"));
		dto.setMobileNumber(rs.getString("MobileNumber"));
		dto.setMaritalStatus(rs.getString("MaritalStatus"));
		dto.setPrincipalEmployer(rs.getString("UnitId"));
		dto.setContractor(rs.getString("ContractorId"));
		//dto.setWorkorder(rs.getString("WorkorderId"));
		//dto.setTrade(rs.getString("TradeId"));
		//dto.setSkill(rs.getString("SkillId"));
		dto.setDepartment(rs.getString("DepartmentId"));
		dto.setSubdepartment(rs.getString("AreaId"));
		//dto.setEic(rs.getString("EicId"));
//		dto.setNatureOfJob(rs.getString("NatureOfJob"));
//		dto.setWcEsicNo(rs.getString("WcEsicNo"));
//		dto.setHazardousArea(rs.getString("HazardousArea"));
//		dto.setAccessArea(rs.getString("AccessAreaId"));
//		dto.setUanNumber(rs.getString("UanNumber"));
//		dto.setHealthCheckDate(rs.getString("HealthCheckDate"));
//		dto.setPfNumber(rs.getString("pfnumber"));
//		dto.setEsicNumber(rs.getString("esicNumber"));
		dto.setBloodGroup(rs.getString("BloodGroupId"));
		dto.setAccommodation(rs.getString("Accommodation"));
		dto.setAcademic(rs.getString("AcademicId"));
		dto.setTechnical(rs.getString("Technical"));
		dto.setIfscCode(rs.getString("IfscCode"));
		dto.setAccountNumber(rs.getString("AccountNumber"));
		dto.setEmergencyName(rs.getString("EmergencyContactName"));
		dto.setEmergencyNumber(rs.getString("EmergencyContactNumber"));
//		dto.setWageCategory(rs.getString("WorkmenWageCategoryId"));
//		dto.setBonusPayout(rs.getString("BonusPayoutId"));
//		dto.setZone(rs.getString("ZoneId"));
//		dto.setBasic(new BigDecimal(rs.getString("Basic")));
//		dto.setDa(new BigDecimal(rs.getString("DA")));
//		dto.setHra(new BigDecimal(rs.getString("HRA")));
//		dto.setWashingAllowance(new BigDecimal(rs.getString("WashingAllowance")));
//		dto.setOtherAllowance(new BigDecimal(rs.getString("OtherAllowance")));
//		dto.setUniformAllowance(new BigDecimal(rs.getString("UniformAllowance")));
//		dto.setPfCap(rs.getString("PfCap"));
		dto.setCreatedBy(rs.getString("UpdatedBy"));
		dto.setAadharDocName(rs.getString("AadharDocName"));
		dto.setPhotoName(rs.getString("PhotoName"));
		dto.setPoliceVerificationDocName(rs.getString("PoliceVerificationDocName"));
		dto.setBankDocName(rs.getString("BankDocName"));
		dto.setIdProof2DocName(rs.getString("IdProof2DocName"));
		dto.setMedicalDocName(rs.getString("MedicalDocName"));
		dto.setForm11DocName(rs.getString("Form11DocName"));
		dto.setOtherDocName(rs.getString("OtherDocName"));
		dto.setTrainingDocName(rs.getString("TrainingDocName"));
		dto.setEducationDocName(rs.getString("EducationDocName"));
		dto.setComments(rs.getString("Comments"));
		dto.setAddress(rs.getString("Address"));
		dto.setDoj(rs.getString("DOJ"));
		//dto.setPfApplicable(rs.getString("pfapplicable"));
		dto.setPoliceVerificationDate(rs.getString("policeverificationDate"));
		//dto.setDot(rs.getString("DOT"));
		dto.setOnboardingType(rs.getString("OnboardingType"));
		//dto.setLlNo(rs.getString("LLNo"));
		dto.setAppointmentDocName(rs.getString("AppointmentDocName"));
		dto.setDisability(rs.getString("disability"));
		dto.setWorkmenType(rs.getString("WorkmenType"));
		//dto.setReasoning(rs.getString("Reasoning"));
//		dto.setExitLetterDocName(rs.getString("ExitLetterDocName"));
//		dto.setFNFDocName(rs.getString("FNFDocName"));
//		dto.setFeedbackFormDocName(rs.getString("FeedbackFormDocName"));
//		dto.setRateManagerDocName(rs.getString("RateManagerDocName"));
//		dto.setLOCDocName(rs.getString("LOCDocName"));
//		dto.setProficiency(rs.getString("Proficiency"));
//		dto.setTrainingId(rs.getString("TRAININGID"));
		}
	log.info("Exiting from getIndividualContractWorkmenDetails dao method "+transactionId);
	return dto;
}
@Override
public List<GatePassListingDto> getFullTimeContGatePassListingDetails(
        String unitId,
        String deptId,
        String userId,
        String gatePassTypeId,
        String type,
        List<PersonOrgLevel> contList) {

    log.info("Entering into getGatePassListingDetails dao method");

    List<GatePassListingDto> listDto = new ArrayList<>();

    String query = getAllGatePassForContractor();

    List<String> contractorIds = new ArrayList<>();

    if (contList != null && !contList.isEmpty()) {
        contractorIds = contList.stream()
                .map(PersonOrgLevel::getId) // change getter if needed
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .distinct()
                .collect(Collectors.toList());
    }

    String contractorFilter = "";

    if (!contractorIds.isEmpty()) {
        String inClause = contractorIds.stream()
                .map(id -> "?")
                .collect(Collectors.joining(","));

        contractorFilter = " AND gpm.ContractorId IN (" + inClause + ") ";
    }

    query = query.replace("{CONTRACTOR_FILTER}", contractorFilter);

    log.info("Query to getGatePassListingDetails {}", query);

    List<Object> params = new ArrayList<>();

    // first SELECT params
    params.add(gatePassTypeId);
    params.add(deptId);
    params.add(deptId);
    params.add(unitId);
    params.add(type);
    params.addAll(contractorIds);

    // UNION SELECT params
    params.add(gatePassTypeId);
    params.add(type);
    params.addAll(contractorIds);

    SqlRowSet rs = jdbcTemplate.queryForRowSet(query, params.toArray());

    while (rs.next()) {
        GatePassListingDto dto = new GatePassListingDto();

        dto.setTransactionId(rs.getString("TransactionId"));
        dto.setGatePassId(rs.getString("GatePassId"));
        dto.setFirstName(rs.getString("FirstName"));
        dto.setLastName(rs.getString("LastName"));
        dto.setGender(rs.getString("GMNAME"));
        dto.setDateOfBirth(rs.getString("DOB"));
        dto.setAadhaarNumber(rs.getString("AadharNumber"));
        dto.setContractorName(rs.getString("ContractorName"));
        dto.setVendorCode(rs.getString("VendorCode"));
        dto.setUnitName(rs.getString("UnitName"));

        String gatePassType = rs.getString("GatePassTypeId");

        if (GatePassType.FULLTIMECONTRACTOR.getStatus().equals(gatePassType)) {
            dto.setGatePassType("Full Time Contractor");
        }

        String status = rs.getString("GatePassStatus");

         if (GatePassStatus.APPROVED.getStatus().equals(status)) {
            dto.setStatus("Approved");
        } 

        dto.setOnboardingType(type);
        listDto.add(dto);
    }

    log.info("Exiting from getGatePassListingDetails dao method {}", listDto.size());
    return listDto;
}
public String checkContractorBlocked() {
    return QueryFileWatcher.getQuery("CHECK_CONTRACTOR_BLOCKED_IN_CMSCONTRACTOR");
}

@Override
public boolean checkContractorBlocked(String contractorId) {

    //String sql = "SELECT COUNT(*) FROM CMSCONTRACTOR WHERE ISBLOCKED = 1 AND CONTRACTORID = ?";
    String sql =checkContractorBlocked();
    Integer count = jdbcTemplate.queryForObject(sql,Integer.class,contractorId);
    return count != null && count > 0;
}

public String getStateByPincode() {
    return QueryFileWatcher.getQuery("GET_STATE_BY_PINCODE");
}

@Override
public String getStateByPincode(String pincode) {

    //String sql = "SELECT TOP 1 State FROM CMSPINCODEMASTER_TEMP  WHERE Pincode = ?";
	String sql =getStateByPincode(); 
    try {
        return jdbcTemplate.queryForObject(sql,String.class,pincode);
    } catch (EmptyResultDataAccessException e) {
        return null;
    } catch (Exception e) {
        log.error("Error while fetching pincode from CMSPINCODEMASTER: {}",pincode,e);
        return null;
    }
}

public String getPEStateByUnitId() {
    return QueryFileWatcher.getQuery("GET_PE_STATE_BY_UNITID");
}

@Override
public String getPEStateByUnitId(String unitId) {
	 String sql =getPEStateByUnitId();
   // String sql = "SELECT TOP 1 cmss.STATENM FROM CMSPESTATE pes join CMSSTATE cmss on cmss.STATEID=pes.STATEID WHERE pes.UNITID = ?";
    try {
        return jdbcTemplate.queryForObject(sql,String.class,unitId);
    } catch (EmptyResultDataAccessException e) {
        return null;
    }
}
public String checkISMWPrincipalEmployer() {
    return QueryFileWatcher.getQuery("CHECK_ISMW_APPLICABILITY_IN_PRINCIPALEMPLOYER");
}
@Override
public boolean checkISMWPrincipalEmployer(String unitId) {
	String sql =checkISMWPrincipalEmployer();
//    String sql =
//        "SELECT ISMWAPPLICABILITY " +
//        "FROM CMSPRINCIPALEMPLOYER " +
//        "WHERE UNITID = ?";

    Integer result = jdbcTemplate.queryForObject(sql, Integer.class, unitId);

    return result != null && result == 1;
}
public String TradeskillWorkmenCountFromGpm() {
    return QueryFileWatcher.getQuery("GET_TRADE_SKILL_WORKMEN_COUNT_FROM_GPM");
}
@Override
public int TradeskillWorkmenCountFromGpm(String principalEmployerId,String tradeId,String skillId) {

    //String sql = "SELECT COUNT(*) FROM GATEPASSMAIN gpm INNER JOIN UnitTradeSkillMapping utsm ON utsm.TradeId = gpm.TradeId AND utsm.SkillId = gpm.SkillId AND utsm.PrincipalEmployerId = gpm.UnitId WHERE gpm.DOT > GETDATE() AND utsm.TradeId = ? AND utsm.SkillId = ? AND utsm.PrincipalEmployerId = ? AND gpm.GatePassTypeId IN (1, 2, 12, 15) AND gpm.GatePassStatus = 4";
	String sql =TradeskillWorkmenCountFromGpm();

    Integer count = jdbcTemplate.queryForObject(sql,Integer.class,tradeId,skillId,principalEmployerId);
    return count != null ? count : 0;
}
public String getWorkmenTradeSkillCountFromMapping() {
    return QueryFileWatcher.getQuery("GET_WORKMEN_TRADE_SKILL_FROM_UNITMAPPING");
}
@Override
public Integer getWorkmenTradeSkillCountFromMapping(String principalEmployerId,String tradeId,String skillId) {

    //String sql = "SELECT WorkmenCount FROM UnitTradeSkillMapping WHERE PrincipalEmployerId = ? AND TradeId = ? AND SkillId = ?";
    String sql =getWorkmenTradeSkillCountFromMapping();
    return jdbcTemplate.query(sql, rs -> {
        if (!rs.next()) {
            // No mapping record
            return null;
        }
        String workmenCount = rs.getString("WorkmenCount");
        // NULL or empty means no limit configured
        if (workmenCount == null || workmenCount.trim().isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(workmenCount.trim());
        } catch (NumberFormatException e) {
            // Invalid value is treated as no limit configured
            return null;
        }

    }, principalEmployerId, tradeId, skillId);
}
}