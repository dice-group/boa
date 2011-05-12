package de.uni_leipzig.simba.boa.backend.dao.evaluation;

import java.util.List;

import de.uni_leipzig.simba.boa.backend.dao.AbstractDao;
import de.uni_leipzig.simba.boa.backend.entity.EvaluationResult;

public class EvaluationResultDao extends AbstractDao {

	public EvaluationResultDao() {

		super();
	}

	/**
	 * 
	 * @param pattern
	 * @return 
	 */
    public EvaluationResult createAndSaveEvaluationResult(EvaluationResult evaluationResult) {
    	
        return (EvaluationResult) super.saveOrUpdateEntity(evaluationResult);
    }

	/**
	 * return null!!!!
	 */
	public EvaluationResult createNewEntity() {

		new RuntimeException("dont use this constructor!");
		return null;
	}

	/**
	 * 
	 * @param pattern
	 */
	public void deleteCluster(EvaluationResult evaluationResult) {

		super.deleteEntity(evaluationResult);
	}

	/**
	 * 
	 * @return
	 */
	public List<EvaluationResult> findAllEvaluationResult() {

		return (List<EvaluationResult>) super.findAllEntitiesByClass(EvaluationResult.class);
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	public EvaluationResult findEvaluationResult(int id) {

		return (EvaluationResult) super.findEntityById(EvaluationResult.class, id);
	}

	/**
	 * 
	 * @param pattern
	 */
	public void updateEvaluationResult(EvaluationResult evaluationResult) {

		super.saveOrUpdateEntity(evaluationResult);
	}
}
