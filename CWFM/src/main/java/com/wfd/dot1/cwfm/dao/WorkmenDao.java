package com.wfd.dot1.cwfm.dao;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

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

public interface WorkmenDao {

	public List<PrincipalEmployer> getAllPrincipalEmployer(String userId);
	
	public List<Contractor> getAllContractorBasedOnPE(String unitId,String userId);
	
	public List<Workorder> getAllWorkordersBasedOnPEAndContractor(String unitId, String contractorId);
	
	public List<Trade> getAllTradesBasedOnPE(String unitId);
	
	public List<Skill> getAllSkill();
	
	public List<CmsGeneralMaster> getAllDepartmentAndSubDepartment(String userId);
	
	public List<MasterUser> getAllEicManager(String unitId,String deptId);
	
	public List<CmsContractorWC> getAllWCBasedOnPEAndCont(String unitId, String contractorId,String workorderId);

	public List<CmsGeneralMaster> getAllGenders();

	public List<CmsGeneralMaster> getAllGeneralMasters();

	public String saveGatePass(GatePassMain gatePassMain);

	public List<GatePassListingDto> getGatePassListingDetails(String unitId,String deptId,String userId, String gatePassTypeId,String type, List<PersonOrgLevel> contList);

	public List<GatePassListingDto> getGatePassListingForApprovers(String userId,int workFlowType,String gatePassTypeId,String deptId,String unitId,String type);

	public GatePassMain getIndividualContractWorkmenDetails(String gatePassId);


	public List<CmsGeneralMaster> getAllGeneralMastersForGatePass(GatePassMain gpm);


	public int getWorkFlowTYpe(String principalEmployer);

	public List<MasterUser> getApproversForGatePass(String createdBy);

	public void saveGatePassApprover(String gatePassId, List<MasterUser> approverList,String createdBy);

	public String approveRejectGatePass(ApproveRejectGatePassDto dto);

	public int getWorkFlowTypeForApprovers(String businessType);

	public boolean updateGatePassMainStatus(String gatePassId, String status);

	public boolean isLastApprover(String roleName,String gatePassTypeId,int workflowTypeId);

	public String gatePassAction(GatePassActionDto dto);

	public void saveGatePassStatusLog(GatePassStatusLogDto dto);

	public List<GatePassListingDto> getGatePassActionListingDetails(String unitId,String deptId,String userId, String gatePassTypeId,String previousGatePassAction,String renewGatePassAction,String bulkRenewAction, List<PersonOrgLevel> contList);

	public List<MasterUser> getApproversForGatePassAction(String createdBy,String gatepassAction);

	boolean updateGatePassMainStatusAndType(String gatePassId, String status, String gatePassType);

	public List<GatePassListingDto> getWorkmenDetailBasedOnId(String gatePassId);

	public int getDOTTYpe(String principalEmployer);

	Map<String, LocalDate> getValidityDates(String workOrderId, String wcId, String llNo);

	public List<ApproverStatusDTO> getApprovalDetails(String transactionId,String unitId,String gatePassTypeId, String Comments);

	public List<Contractor> getAllContractorForAdmin(String unitId);

	String draftGatePass(GatePassMain gatePassMain);
	
	public  String generateTransationId();

	GatePassMain getIndividualContractWorkmenDraftDetails(String transactionId);

	public String updateGatePassIdByTransactionId(String transactionId);

	GatePassMain getIndividualContractWorkmenDetailsByTransId(String transactionId);

	List<GatePassListingDto> getRenewListingDetails(String userId, String gatePassTypeId, String gatePassStatus,
			String deptId, String unitId, List<PersonOrgLevel> contList);

	String renewGatePass(GatePassMain gatePassMain);

	GatePassMain getIndividualContractWorkmenDetailsByGatePassId(String gatePassId);


	List<GatePassListingDto> getGatePassActionListingForApprovers(String roleId, int workFlowType,
			String gatePassTypeId, String deptId, String unitId);

	int getWorkFlowTYpeByTransactionId(String transactionId,String actionId);

	boolean isLastApproverForParallel(String gatePassTypeId, String transactionId, String roleId, String unitId);

	boolean isLastApproverForParallelGatePassAction(String gatePassTypeId, String gatePassId, String roleId,String unitId);

	int getWorkFlowTYpeNew(String principalEmployer, String gatePassAction);


	public boolean updateGatePassMainStatusByTransactionId(String transactionId, String status);

	public List<ContractWorkmenExportDto> getContractWorkmenExportData(String unitId);


	public boolean isAadharExists(String aadharNumber,String transactionId);

	

	public List<WageDetailsDto> getWageMasterExportData(String unitId);

	public String findAadharByUanIfExistsWithDifferentAadhar(String uan, String aadhar);

	public String findAadharBypfNumberIfExistsWithDifferentAadhar(String pfNumber, String aadhar);

	String getNextTransactionId();

	void createDraftGatepass(String transactionId, String userId);

	List<GatePassListingDto> getGatePassActionListingDetailsDashboardNav(String unitId, String deptId, String userId,
			String gatePassTypeId);

	public List<DeptMapping> getAllSkills(String unitId, String tradeId);

	public List<DeptMapping> getAllDepartmentsOnPE(String unitId);

	public List<DeptMapping> getAllSubDepartments(String unitId, String departmentId);
	
	public String getTransactionIdByGPId(String gatepassid,String gatepasstypeid);

	public long saveIntoCMSPerson(CMSPerson person);

	public boolean saveIntoCMSPERSONJOBHIST(GatePassMain gpm, long employeeId);

	public boolean saveCMSPERSONSTATUSMM(GatePassMain gpm, long employeeId);

	boolean saveCMSPERSONSTATUSMMTerminated(GatePassMain gpm, long employeeId);

	public boolean saveCMSPERSONCUSTDATA(GatePassMain gpm, long personInsert);

	public GatePassMain getIndividualContractWorkmenDetailsByGatePassIdForApprove(String gatePassId);

	public long getPersonIdFromCmsPerson(String gatePassId);

	boolean updateCmsPersonCustDataEffectiveTill(long personId);

	public boolean insertIntoCustData(String updatedBy,long personId,String gatePassStatus,String reasoning, String dot);

	boolean isPersonActiveInStatusMM(long personId);

	PersonStatusIds getPersonStatusIds(long personId);


	boolean updatePersonStatusValidity(Long activeId, Long inactiveId);


	Map<String, String> getPreviousDocuments(String transactionId);

	//public Map<String, String> getVersionOneDocs(String transactionId);

	public List<Map<String, Object>> getRenewalDocs(String transactionId);

	public void saveRenewedDocuments(String transactionId, String userId, MultipartFile aadharFile,
			MultipartFile policeFile, MultipartFile profilePic, List<MultipartFile> additionalFiles,
			List<String> documentTypes,String filePath);

	


	int getActiveWorkmenCount(String unitId, String contractorId, String gatePassStatus, String gatePassType);

	int getLLDeploymentCountByUnitId(String unitId);

	Map<String, Object> licenseExistsAndCount(String unitId, String contractorId, String workorderId, String licenseType,
			String licenseId);

	public List<GatePassListingDto> getGatePassUnblockDeblackListingDetails(String unitId, String deptId, String userId,
			String gatePassTypeId, String previousGatePassAction, String renewGatePassAction, List<PersonOrgLevel> contList);

	public String getAadharStatus(String aadharNumber);

	public String getTransactionIdByGatePassId(String gatePassId) ;

	int getWorkFlowTypeId(String unitId, String actionId);

	public AadharCheckDto checkAadharUniqueness(String aadharNumber, String gatePassId, String transactionId) ;

	GatePassMain getIndividualContractWorkmenDetailsByGatePassIdRenew(String gatePassId);

	GatePassMain getActiveCountDetails(String transactionId);

	String getRenewTransactionIfExists(String gatePassId);

	public boolean  updateGatePassMainWithCancelReasoningTab(GatePassActionDto dto, MultipartFile exitFile,
			MultipartFile fnfFile, MultipartFile feedbackFile, MultipartFile rateManagerFile, MultipartFile locFile);

	public boolean updateCmsPersonCustDataEffectiveTillonDeblackUnblock(long personId, String dot);



	boolean updatePersonStatusOnDeblockUnblock(Long activeId, Long inactiveId, String dot);

	public boolean updateCmsPersonCustDataRenewEffectiveTill(long personId, String dot);

	boolean insertIntoCustDataRenew(String updatedBy, long personId, String gatePassStatus);

	public boolean updatePersonStatusValidityRenew(Long activeId, Long inactiveId, String dot);

	public void updateonboardingDocTypeinGP(String onboardingDocType, String transactionId);

	public String getStateOnPrincipalEmployer(String principalEmployer);
	
	Map<String, Object> getMinimumWage(String principalEmployer, String stateId, String zone, String skill);

	public boolean updateGatePassMainWithReasoningTab(GatePassActionDto dto, MultipartFile attachmentOfReference);

	boolean updateDOTGatePassMainDOT(String gatePassId, String dot);

	

	Map<String, Integer> getPlantCounts(String unitId);
	public List<Zone> getAllZonesBasedOnPE(String unitId);

	public Map<String, Object> getWorkmenDetailsByAadhar(String aadharNumber);

	List<ContractWorkmenReportDTO> getContractWorkmenReportData(String unitId, String contractorId, String startDate, String endDate);

	public List<ContractWorkmenReportDTO> getInactiveWorkmenReportData(String unitId, String contractorId);

	public List<ContractWorkmenReportDTO> getPoliceverificationWorkmenReportData(String unitId, String contractorId);

	public List<ContractWorkmenReportDTO> getPolicyExpiryWorkmenReportData(String unitId, String contractorId);

	boolean updateGatePassMainDOJ(String gatePassId, String doj);

	public boolean updateCmsPersonJobHistRenew(GatePassMain gpm, long personId);

	public void updateGatePassIdByEmpCode(GatePassMain gatePassMain);

	public List<ApproveRejectGatePassDto> getTrainingDetails(String unitId, String department);

	void saveTrainingDetails(ApproveRejectGatePassDto dto);

	public List<ApproveRejectGatePassDto> getExistingTrainingRecords(String transactionId);

	public boolean updateInactiveValidTo(Long inactiveId, LocalDate minusDays);

	public boolean insertActiveStatusRecord(long personId, LocalDate today, LocalDate dotDate);

	public boolean insertInactiveStatusRecord(long personId, LocalDate plusDays, LocalDate of);

	public boolean isActionDoneToday(String gatePassId, Long gatePassTypeId);

	public String getoldDotFromActiveRecord(Long activeId);

	String saveFullTimeContractorGatePass(GatePassMain gatePassMain);

	public long saveFullTimeContractorIntoCMSPerson(CMSPerson person);

	public boolean saveFullTimeContractorIntoCMSPERSONJOBHIST(GatePassMain gpm, long employeeId);

	public boolean saveFullTimeContractorCMSPERSONSTATUSMM(GatePassMain gpm, long employeeId);

	public boolean saveFullTimeContractorCMSPERSONCUSTOMDATA(GatePassMain gpm, long personInsert);

	public GatePassMain getFullTimeIndividualContractWorkmenDetails(String transactionId);

	List<GatePassListingDto> getFullTimeContGatePassListingDetails(String unitId, String deptId, String userId,
			String gatePassTypeId, String type, List<PersonOrgLevel> contList);

	public boolean checkContractorBlocked(String contractorId);
	
	String getStateByPincode(String pincode);

    String getPEStateByUnitId(String unitId);

	public boolean checkISMWPrincipalEmployer(String unitId);

	public int TradeskillWorkmenCountFromGpm(String principalEmployer, String trade, String skill);

	public Integer getWorkmenTradeSkillCountFromMapping(String principalEmployer, String trade, String skill);
}
