/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.org.rbc1b.roms.db.volunteer;

/**
 *
 * @author oliver.elder.esq
 */
public class RbcStatus {

    public static final RbcStatus ACTIVE = new RbcStatus(1);
    private Integer rbcStatusId;
    private String description;

    /**
     * Default constructor.
     */
    public RbcStatus() {
        // do nothing
    }

    /**
     * Constructor.
     *
     * @param rbcStatusId id
     */
    public RbcStatus(Integer rbcStatusId) {
        this.rbcStatusId = rbcStatusId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getRbcStatusId() {
        return rbcStatusId;
    }

    public void setRbcStatusId(Integer rbcStatusId) {
        this.rbcStatusId = rbcStatusId;
    }

    @Override
    public String toString() {
        return "RbcStatus{" + "rbcStatusId=" + rbcStatusId + '}';
    }
}
