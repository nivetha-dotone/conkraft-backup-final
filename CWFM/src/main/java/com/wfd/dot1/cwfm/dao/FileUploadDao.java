package com.wfd.dot1.cwfm.dao;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.wfd.dot1.cwfm.dto.MinimumWageDTO;
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

public interface FileUploadDao {
    void saveData(String[] data);

    void saveGeneralMaster(CmsGeneralMaster gm);
    void saveMinimumWage(MimumWageMasterTemplate mw);
    Long savePrincipalEmployer(PrincipalEmployer p, String createdBy);
    Long insertIntoWageTable(MinimumWageDTO dto);
    void insertIntoMinimumWageTable(LocalDate fromDate, Long wageId);
    Long saveContractor(Contractor contractor);
    void savePemm(CMSContrPemm pemm);
   // int savepe(String code);
	void savewc(CmsContractorWC wc);
	void savecsc(CMSSubContractor csc);

	Long saveWorkorder(Workorder workorder);

	void saveWorkorderLN(CMSWorkorderLN woln);

	void saveWorkorderTyp(ContractorWorkorderTYP wotyp);

	Long getUnitIdByPlantCodeAndOrg(String plantCode, String organization);

	Long getContractorIdbyUnitId(Long unitId);

	//Long getWorkorderIdBySapNumber(String sapWorkorderNumber);	
	
	String getCSVHeaders(String templateType);
	
	boolean isPrincipalEmployerCodeExists(String code);
	
	boolean isContractorCodeExists(String contractorCode);
	
	Long getStateIdByName(String stateName);
	
	void savePEState(Long unitId, Long stateId);
	
	void saveWorkorderToStaging(KTCWorkorderStaging workorderStaging);
	
	void callWorkorderProcessingSP();

	boolean isGmNameGmDescriptionExists(String gmName, String gmDescription);

	void saveWorkmenBulkUploadToStaging(WorkmenBulkUpload staging);

	void callWorkmenBulkUploadDraftProcessingSP();

	Integer getUnitIdByName(String unitCode);
	
	Integer getContractorIdByName(String vendorCode);

	Integer getTradeIdByName(String tradeName);

    // Get Workmen Wage Category ID by name
    Integer getWCECId(String ECNumber, Integer unitId, Integer contractorId, String workorderNumber);

    // Get GMID (Generic Master ID) by GMNAME and master type (like skill, dept, access area, etc.)
    Integer getGeneralMasterId(String gmName);

	Integer getWorkorderId(String workorderNumber,Integer unitId, Integer contractorId);

	Integer getSkillIdByName(String skill);

	Integer geteicId(String department, Integer unitId, String ECnumber);

	int saveWorkmenBulkDraftUploadToStaging(WorkmenBulkUpload staging);

	//void saveToGatePassMain(WorkmenBulkUpload record);
	WorkmenBulkUpload getByTransactionId(int transactionId);

	void saveToGatePassMain(WorkmenBulkUpload data);
	
	void updateRecordStatusByTransactionId(int txnId, String combinedErrors);

	Integer getLlNumber(String LLNumber, Integer unitId, Integer contractorId, String workorderNumber);

	boolean isAadharNumberExists(String aadharNumber);

	Integer getdepartmentIdByUnitId(Integer unitId, String department);

	Integer getAreaByDeptID(Integer unitId, Integer departmentId, String area);

	Integer getTradeIdByUnitId(Integer unitId, String trade);

	Integer getSkillIdByTradeId(Integer unitId, Integer tradeId, String skill);


    Integer getGeneralMasterId(String gmType, String gmName);

    Integer insertGeneralMaster(String gmType, String gmName);

    boolean existsUnitTradeSkillMapping(Integer unitId, Integer tradeId, Integer skillId);

    void insertUnitTradeSkillMapping(Integer unitId, Integer tradeId, Integer skillId, String workmenCount);

	void insertUnitDepartmentSubDepartmentMapping(Integer unitId, Integer departmentId, Integer subDepartmentId);

	long getOrgLevelDefId(String name);

	boolean SavePEOrglevelEntry(List<PrincipalEmployer> list, long orgLevelDefId);

	boolean SaveContOrglevelEntry(List<Contractor> list, long orgLevelDefId);

	boolean SaveWorkorderOrglevelEntry(List<KTCWorkorderStaging> list, long orgLevelDefId);

	boolean saveDeptOrgLevelEntry(List<DeptMapping> finalList, long orgLevelDefId);

	boolean saveAreaOrgLevelEntry(List<DeptMapping> finalList, long orgLevelDefId);

	//void insertOrgLevelEntry(String name, long orgLevelDefId);

	boolean existsInOrgLevelEntry(String name, long orgLevelDefId);

	Long getContractorIdByCode(String subContractorCode);	

	boolean hasActiveWorkorder(Long unitId, Long contractorId, String workOrder);

	void updateContractor(Contractor contractor);

	boolean pemmExists(Long contractorId, Long unitId);

	void updatePemm(CMSContrPemm pemm);

	boolean subContractorExists(String contractorCode, Long unitId, String workOrder, String subContractorCode);

	void updatecsc(CMSSubContractor csc);

	boolean wcExists(Long contractorId, Long unitId, String wcCode,String licenceType);

	void updatewc(CmsContractorWC wc);

	void saveWorkorderLLWC(CMSWorkorderLLWC llwc);

	void updateWorkorderLLWC(CMSWorkorderLLWC llwc);

	boolean llwcExists(String workOrderNumber, String licenseType, String license);

	boolean isLicenseMappedToOtherContractor(Long contractorId,String licenseNumber,String licenseType);

	boolean codeExistsInOrgLevelEntry(String contractorCode,long orgLevelDefId);

	boolean codeExistsInOrgLevelEntry(List<Contractor> list, long orgLevelDefId);

	boolean workorderExists(String workOrder, String contractorCode, String plantCode, String item, String lines, String lineNumber);

	void updateWorkorderToStaging(KTCWorkorderStaging staging);
	
	boolean gatepassNumberExists(String gatepassNumber);

	List<String> getContractorDetailsForCancel(String gatepassNumber);

	void updateGatepass(BulkCancel bc, String createdBy);



	//Integer WorkorderExists(String workorderNumber, String gpContId);

	Integer WCESICExists(String workorderNumber, String wcNumber);

	Integer LLExists(String workorderNumber, String llNumber);

	void updateGatepassBulkRenew(GatePassMain gm, String createdBy, String dot);

	Map<String, Object> workorderExists(String workorderNumber, String gpContId);

	void saveMinimumWageToStaging(MinimumWageDTO staging);

	boolean minimumWageFromExistsInStagging(String unitCode, String stateName, String zoneName, String skillName, Date fromDate);

	void updateMinimumWageToStaging(MinimumWageDTO staging);

	void callMinimumWageProcessingSP();

	boolean minimumWagesExistsInStagging(String unitCode, String stateName, String zoneName, String skillName);

	Integer isUserExists(String userName);

	void saveuserImport(UserImport user, List<Long> roleIds);

	void saveUserRoleMapping(Long userId, Integer roleId);

	Long getContractorIdByCodeInCMSVendor(String subContractorCode);

	void insertContractorInCMSVendor(CMSVendor cmsvendor);

	void updateContractorInCMSVendor(CMSVendor cmsvendor);

	Set<String> getExistingContractorCodes(List<Contractor> list, long orgLevelDefId);

	Integer getZoneIdFromMinimumWage(String zoneValue, Integer unitId);

	void updateuserImport(UserImport user, Integer userId, List<Long> roleIds);

	Long getOrgLevelEntryId(String department, Long plantDefId);

	Long insertUserOrgAccountSet(String userAccount);

	void insertUserOrgMapping(List<Long> orgEntryIds, Long orgSetId);

	Long getOrgAccountSetIdFromSet(String userAccount);

	List<Long> getExistingOrgMappings(Long orgSetId);

	Map<String, Long> getAllOrgLevelDefIds();

	Integer getGmTypeId(String gmType);

	boolean isGmNameGmTypeExists(String gmName, Integer gmTypeId);

	boolean activeGatepassExists(String gatepassNumber);

	Map<String, Object> workorderExistsForPlantAndContractor(String workorderNumber, 
			Integer unitId);

	Map<String, Object> licenseExistsWithWorkorder(String workorderNumber, String wcesicNumber);

	Integer getWorkorderIdBasedonPE(String string, Integer unitId);

	Integer IsWorkorderExistsForOtherContractor(String workOrder, String contractorCode);

	void updatePrincipalEmployer(PrincipalEmployer p, String createdBy, Long unitId);

	Long getPrincipalEmployerExists(String code, String organization, String businessType);

	boolean getPEStateExists(Long unitId, Long stateId);

	Set<String> getExistingPECodes(List<PrincipalEmployer> list, long orgLevelDefId);

	void updateGatepassMainIntraPlantTransfer(GatePassMain gm, String createdBy, String dot);

	void insertIntraPlantTransferTemp(GatePassMain gm, String createdBy, String dot);

	List<String> getContractorDetailsForIntraPlantTransfer(String gatepassNumber);

	String getLastEffectiveFromDateFromJobHist(String gatepassNumber, Date effectiveFrom);

	GatePassMain getAllDeatilsOfWorkmenBasedOnGatePass(String gatepassId);

	void insertSamePlantDiffContIntraPlantTransfer(GatePassMain gm, String createdBy, String dot);

	boolean checkTrainingDetailsExists(Integer unitId, String department, String trainingType, String trainingName);

	void saveTrainingDetails(Integer unitId, String department, String trainingType, String trainingName);
}
