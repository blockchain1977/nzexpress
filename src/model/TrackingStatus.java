package model;

import controller.weichatplatform.expressnz.BackendActs;
import org.apache.log4j.Logger;

/**
 * Created by kyle.yu on 26/08/2015.
 * Tracking Status
 */
public class TrackingStatus {
    private static Logger logger = Logger.getLogger(TrackingStatus.class);

    private String useridentity;
    private String package_number;
    private String package_latest_status;
    private String tracking_enabled; // Y: tracking, N: not tracking,
    private String description;
    private String comments;

    public final static String TRACKING_ENABLE_YES = "Y";
    public final static String TRACKING_ENABLE_NO = "N";

    private boolean dirty;

    public String getUseridentity() {
        return useridentity;
    }

    public void setUseridentity(String useridentity) {
        logger.debug("User Identity is : " + useridentity);
        this.useridentity = useridentity;
        setDirty(true);
    }

    public String getPackage_number() {
        return package_number;
    }

    public void setPackage_number(String package_number) {
        logger.debug("Package Number is : " + package_number);
        this.package_number = package_number;
        setDirty(true);
    }

    public String getPackage_latest_status() {
        return package_latest_status;
    }

    public void setPackage_latest_status(String package_latest_status) {
        if ((package_latest_status != null) && (! package_latest_status.isEmpty())) {
            if (package_latest_status.contains("无效快递单号") || package_latest_status.contains("无法查询")) {
                return;
            }
            int index = package_latest_status.lastIndexOf("\n", package_latest_status.length() - 2);
            String lastLine = package_latest_status.substring(index + 1);
            String status = "";
            if (getPackage_latest_status() != null) {
                status = getPackage_latest_status();
            }
            if (! lastLine.replaceAll("[0-9]", "").equalsIgnoreCase(status.replaceAll("[0-9]", ""))) {
                setDirty(true);
            }
            logger.debug("Package Last status set as : " + lastLine);
            this.package_latest_status = lastLine;
        }
    }

    public String getTracking_enabled() {
        return tracking_enabled;
    }

    public void setTracking_enabled(String tracking_enabled) {
        logger.debug("Tracking Enable set as : " + tracking_enabled);
        this.tracking_enabled = tracking_enabled;
        setDirty(true);
    }

    @Override
    public String toString() {
        return "TrackingStatus{" +
                "useridentity='" + useridentity + '\'' +
                ", package_number='" + package_number + '\'' +
                ", package_latest_status='" + package_latest_status + '\'' +
                ", tracking_enabled='" + tracking_enabled + '\'' +
                ", description='" + description + '\'' +
                ", comments='" + comments + '\'' +
                ", dirty=" + dirty +
                '}';
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        logger.debug("Description is : " + description);
        this.description = description;
        setDirty(true);
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
        setDirty(true);
    }

    public TrackingStatus(String useridentity, String package_number, String description) {
        setUseridentity(useridentity);
        setPackage_number(package_number);
        setDescription(description);

        setTracking_enabled(TRACKING_ENABLE_YES);
        setComments("");
        setPackage_latest_status("");

        setDirty(true);
    }

    public TrackingStatus(String useridentity, String package_number, String description, String tracking_enabled, String comments, String package_latest_status) {
        setUseridentity(useridentity);
        setPackage_number(package_number);
        setDescription(description);

        setTracking_enabled(tracking_enabled);
        setComments(comments);
        setPackage_latest_status(package_latest_status);

        setDirty(true);

    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        logger.debug("Set Dirty as : " + dirty);
        this.dirty = dirty;
    }

    public void delete() {

    }
}
