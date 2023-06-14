package service.reservationhandlingservice.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "researchGroup")
public class ResearchGroup {

    @Id
    @GeneratedValue
    @Column(name = "groupId")
    private Long groupId;

    @Column(name = "secretaryId", nullable = false)
    private String secretaryId;


    @ElementCollection(fetch = FetchType.EAGER)
    @Column(name = "group_members", length = 255)
    private List<String> groupmembers;


    /** Description: Creates a new Research group, note a secretary may have multiple groups.
     *
     * @param secretaryId The nedId of the secretary, cannot be null.
     *
     * @param groupmembers A list of group members for that secretary.
     */
    public ResearchGroup(String secretaryId, List<String> groupmembers) {
        this.secretaryId = secretaryId;
        this.groupmembers = new ArrayList<>();
        this.groupmembers.addAll(groupmembers);
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    /** Description: Gets the secretary id (NetId of the secretary).
     *
     * @return The secretary Id of the research group.
     */
    public String getSecretaryId() {
        return secretaryId;
    }

    /** Description: Sets the secretaryId to a new one
     * (imagine you would like another secretary to take charge of that group).
     *
     * @param secretaryId The string (NetId) of the new secretary.
     */
    public void setSecretaryId(String secretaryId) {
        this.secretaryId = secretaryId;
    }

    /** Description: Get all the group members.
     *
     * @return a list of group members.
     */
    public List<String> getGroup_members() {
        return groupmembers;
    }

    /** Description: Set the list of group members.
     *
     * @param groupmembers the new list of group members to be added.
     */
    public void setGroup_members(List<String> groupmembers) {
        this.groupmembers = groupmembers;
    }


    /** Description: Add a new group member to the existing list of group members.
     *
     * @param member The string (Net-ID) of the new member.
     * @throws Exception If the member is already in the group.
     */
    public void addGroupMember(String member) throws Exception {
        if (groupmembers.contains(member)) {
            throw new Exception(member + " is already in this group");
        } else {
            groupmembers.add(member);
        }
    }


    /** Description: Removes an existing group member to the list of group members.
     *
     * @param member The string (Net-ID) of the new member.
     * @throws Exception If the member is not in the group.
     */
    public void removeGroupMember(String member) throws Exception {
        if (!groupmembers.contains(member)) {
            throw new Exception(member + " is not in this group");
        } else {
            groupmembers.remove(member);
        }
    }

    /** Description: Checks if two groups are equal.
     *
     * @param o The object to compare the research group with.
     * @return True if the research groups are the same, false if not.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ResearchGroup)) {
            return false;
        }
        ResearchGroup that = (ResearchGroup) o;
        if (!groupId.equals(that.groupId)
            || !getSecretaryId().equals(that.getSecretaryId())
            || !Objects.equals(groupmembers, that.groupmembers)) {
            return false;
        }
        return true;
    }

    /** Description: Gives the hashcode for a specific group.
     *
     * @return Returns the hashcode of a group.
     */
    @Override
    public int hashCode() {
        return Objects.hash(groupId, getSecretaryId(), groupmembers);
    }

    /** Description: A user friendly string, that describe the research group.
     *
     * @return The string giving the secretary id + the list of users.
     */
    public String toString() {

        if (this.groupmembers.size() == 0) {
            return "The group of " + this.secretaryId + " has no group members\n";
        }
        String result = "The group of " + this.secretaryId + " with group members:\n";

        for (int i = 0; i < groupmembers.size(); i++) {
            result = result + "+" + groupmembers.get(i) + "\n";
        }

        return result;
    }
}
