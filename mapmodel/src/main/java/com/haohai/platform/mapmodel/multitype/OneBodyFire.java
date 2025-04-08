package com.haohai.platform.mapmodel.multitype;

/**
 * Created by geyang on 2020/12/3.
 */
public class OneBodyFire {

    // @ApiModelProperty(value = "组织id")
    private String groupId;
    /**
     * 火点id
     */
    // @ApiModelProperty(value = "火点id")
    // @Null(groups = Save.class,message = "插入时id为空")
    private String id;
    /**
     * 监控点id
     */
    // @ApiModelProperty(value = "监控点id",example = "586758af-5a30-4f7d-8c4c-d550b532f35f")
    //  @NotNull(groups = Save.class,message = "插入时监控点id不能为空")
    private String monitorId;
    /**
     * 所属网格ID
     */
    //  @ApiModelProperty(value = "所属网格ID")
    private String gridId;
    /**
     * 监控点类别
     */
    //   @ApiModelProperty(value = "监控点类别")
    //   @NotNull(groups = Save.class,message = "插入时监控点类别不能为空")
    private String monitorType;
    /**
     * 水平角度
     */
    //  @ApiModelProperty(value = "水平角度")
    private Double horizonAngle;
    /**
     * 垂直角度
     */
    //   @ApiModelProperty(value = "垂直角度")
    private Double verticalAngle;
    /**
     * 火点编号
     */
    //  @ApiModelProperty(value = "火点编号")
    private String fireNo;
    /**
     * 最高热值
     */
    //  @ApiModelProperty(value = "最高热值")
    private Double maxHeat;
    /**
     * 热值X
     */
    //  @ApiModelProperty(value = "热值X")
    private Double heatX;
    /**
     * 热值Y
     */
    //  @ApiModelProperty(value = "热值Y")
    private Double heatY;

    /**
     * 火警经度
     */
    // @ApiModelProperty(value = "火警经度")
    private Double alarmLongitude;
    /**
     * 火警纬度
     */
    //  @ApiModelProperty(value = "火警纬度")
    private Double alarmLatitude;
    /**
     * 火警海拔
     */
    //   @ApiModelProperty(value = "火警海拔")
    private Double alarmAltitude;
    /**
     * 省代码
     */
    //   @ApiModelProperty(value = "省代码")
    //   @NotNull(groups = Save.class,message = "插入时省代码不能为空")
    private String provinceCode;
    /**
     * 省名称
     */
    //  @ApiModelProperty(value = "省名称")
    private String provinceName;
    /**
     * 市代码
     */
    //   @ApiModelProperty(value = "市代码")
    //  @NotNull(groups = Save.class,message = "插入时市代码不能为空")

    private String cityCode;
    /**
     * 市名称
     */
    //   @ApiModelProperty(value = "市名称")
    private String cityName;
    /**
     * 县代码
     */
    private String countyCode;
    /**
     * 县名称
     */
    private String countyName;
    /**
     * 详细地址
     */
    private String address;
    /**
     * 火警时间
     */
    private String alarmDatetime;
    /**
     * 发现方式
     */
    private Integer findType;
    /**
     * 截图1
     */
    private String picPath1;
    /**
     * 截图2
     */
    private String picPath2;
    /**
     * 视频1
     */
    private String videoPath1;
    /**
     * 视频2
     */
    private String videoPath2;
    /**
     * 是否真实火点
     */
    private Integer isReal;
    /**
     * 是否定位
     */
    private Integer isLocation;
    /**
     * 是否处理
     */
    private Integer isHandle;
    /**
     * 处理人
     */
    private String handlePerson;
    /**
     * 处理时间
     */
    private String handleTime;
    /**
     * 处理用户
     */
    private String handleUser;
    /**
     * 处理意见
     */
    private String handleOpinions;
    /**
     * 是否生成档案
     */
    private int isArchives;

    private String startTime;

    private String endTime;

    // @ApiModelProperty(value = "监控点名称")
    private String name;
    // @ApiModelProperty(value = "是否委派")
    private Integer isDelegate;
    // @ApiModelProperty(value = "报警类型")
    private Integer type;

    public OneBodyFire() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getGridId() {
        return gridId;
    }

    public void setGridId(String gridId) {
        this.gridId = gridId;
    }

    public String getMonitorType() {
        return monitorType;
    }

    public void setMonitorType(String monitorType) {
        this.monitorType = monitorType;
    }

    public Double getHorizonAngle() {
        return horizonAngle;
    }

    public void setHorizonAngle(Double horizonAngle) {
        this.horizonAngle = horizonAngle;
    }

    public Double getVerticalAngle() {
        return verticalAngle;
    }

    public void setVerticalAngle(Double verticalAngle) {
        this.verticalAngle = verticalAngle;
    }

    public String getFireNo() {
        return fireNo;
    }

    public void setFireNo(String fireNo) {
        this.fireNo = fireNo;
    }

    public Double getMaxHeat() {
        return maxHeat;
    }

    public void setMaxHeat(Double maxHeat) {
        this.maxHeat = maxHeat;
    }

    public Double getHeatX() {
        return heatX;
    }

    public void setHeatX(Double heatX) {
        this.heatX = heatX;
    }

    public Double getHeatY() {
        return heatY;
    }

    public void setHeatY(Double heatY) {
        this.heatY = heatY;
    }

    public Double getAlarmLongitude() {
        return alarmLongitude;
    }

    public void setAlarmLongitude(Double alarmLongitude) {
        this.alarmLongitude = alarmLongitude;
    }

    public Double getAlarmLatitude() {
        return alarmLatitude;
    }

    public void setAlarmLatitude(Double alarmLatitude) {
        this.alarmLatitude = alarmLatitude;
    }

    public Double getAlarmAltitude() {
        return alarmAltitude;
    }

    public void setAlarmAltitude(Double alarmAltitude) {
        this.alarmAltitude = alarmAltitude;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAlarmDatetime() {
        return alarmDatetime;
    }

    public void setAlarmDatetime(String alarmDatetime) {
        this.alarmDatetime = alarmDatetime;
    }

    public Integer getFindType() {
        return findType;
    }

    public void setFindType(Integer findType) {
        this.findType = findType;
    }

    public String getPicPath1() {
        return picPath1;
    }

    public void setPicPath1(String picPath1) {
        this.picPath1 = picPath1;
    }

    public String getPicPath2() {
        return picPath2;
    }

    public void setPicPath2(String picPath2) {
        this.picPath2 = picPath2;
    }

    public String getVideoPath1() {
        return videoPath1;
    }

    public void setVideoPath1(String videoPath1) {
        this.videoPath1 = videoPath1;
    }

    public String getVideoPath2() {
        return videoPath2;
    }

    public void setVideoPath2(String videoPath2) {
        this.videoPath2 = videoPath2;
    }

    public Integer getIsReal() {
        return isReal;
    }

    public void setIsReal(Integer isReal) {
        this.isReal = isReal;
    }

    public Integer getIsLocation() {
        return isLocation;
    }

    public void setIsLocation(Integer isLocation) {
        this.isLocation = isLocation;
    }

    public Integer getIsHandle() {
        return isHandle;
    }

    public void setIsHandle(Integer isHandle) {
        this.isHandle = isHandle;
    }

    public String getHandlePerson() {
        return handlePerson;
    }

    public void setHandlePerson(String handlePerson) {
        this.handlePerson = handlePerson;
    }

    public String getHandleTime() {
        return handleTime;
    }

    public void setHandleTime(String handleTime) {
        this.handleTime = handleTime;
    }

    public String getHandleUser() {
        return handleUser;
    }

    public void setHandleUser(String handleUser) {
        this.handleUser = handleUser;
    }

    public String getHandleOpinions() {
        return handleOpinions;
    }

    public void setHandleOpinions(String handleOpinions) {
        this.handleOpinions = handleOpinions;
    }

    public int getIsArchives() {
        return isArchives;
    }

    public void setIsArchives(int isArchives) {
        this.isArchives = isArchives;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getIsDelegate() {
        return isDelegate;
    }

    public void setIsDelegate(Integer isDelegate) {
        this.isDelegate = isDelegate;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "OneBodyFire{" +

                ", alarmLongitude=" + alarmLongitude +
                ", alarmLatitude=" + alarmLatitude +
                ", alarmAltitude=" + alarmAltitude +
                ", address='" + address + '\'' +
                ", picPath1='" + picPath1 + '\'' +
                ", picPath2='" + picPath2 + '\'' +
                ", videoPath1='" + videoPath1 + '\'' +
                ", videoPath2='" + videoPath2 + '\'' +
                ", isReal=" + isReal +
                ", isHandle=" + isHandle +
                ", handleTime='" + handleTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}