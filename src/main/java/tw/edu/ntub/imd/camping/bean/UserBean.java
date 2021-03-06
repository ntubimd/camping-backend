package tw.edu.ntub.imd.camping.bean;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import tw.edu.ntub.imd.camping.databaseconfig.enumerate.Experience;
import tw.edu.ntub.imd.camping.databaseconfig.enumerate.Gender;
import tw.edu.ntub.imd.camping.databaseconfig.enumerate.UserRoleEnum;
import tw.edu.ntub.imd.camping.validation.CreateUser;
import tw.edu.ntub.imd.camping.validation.UpdateUser;

import javax.annotation.Nullable;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Schema(name = "使用者", description = "使用者")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBean {
    @Schema(description = "帳號", example = "test")
    @Null(groups = UpdateUser.class, message = "帳號 - 不得更改")
    @NotBlank(groups = CreateUser.class, message = "帳號 - 未填寫")
    @Size(max = 100, message = "帳號 - 輸入字數大於{max}個字")
    private String account;

    @Schema(description = "密碼", example = "hello")
    @Null(groups = UpdateUser.class, message = "密碼 - 不得更改")
    @NotBlank(groups = CreateUser.class, message = "密碼 - 未填寫")
    private String password;

    @Hidden
    @Null(groups = {CreateUser.class, UpdateUser.class}, message = "roleId - 不得填寫")
    private UserRoleEnum roleId;

    @Hidden
    @Getter(AccessLevel.NONE)
    @Null(groups = {CreateUser.class, UpdateUser.class}, message = "enable - 不得填寫")
    private Boolean enable;

    @Schema(description = "露營經驗(0: 新手/ 1: 有過幾次經驗)", type = "int", example = "0")
    @NotNull(groups = CreateUser.class, message = "露營經驗 - 未填寫")
    private Experience experience;

    @Schema(description = "姓氏", example = "王")
    @Null(groups = UpdateUser.class, message = "姓氏 - 不得更改")
    @NotBlank(groups = CreateUser.class, message = "姓氏 - 未填寫")
    @Size(max = 50, message = "姓氏 - 輸入字數大於{max}個字")
    private String lastName;

    @Schema(description = "名字", example = "小明")
    @Null(groups = UpdateUser.class, message = "名字 - 不得更改")
    @NotBlank(groups = CreateUser.class, message = "名字 - 未填寫")
    @Size(max = 50, message = "名字 - 輸入字數大於{max}個字")
    private String firstName;

    @Schema(description = "暱稱", example = "煞氣a小明")
    @NotBlank(groups = CreateUser.class, message = "暱稱 - 未填寫")
    @Size(max = 50, message = "暱稱 - 輸入字數大於{max}個字")
    private String nickName;

    @Schema(description = "性別(0: 男/ 1: 女/ 2: 未提供)", type = "int", example = "0")
    @Null(groups = UpdateUser.class, message = "性別 - 不得更改")
    @NotNull(groups = CreateUser.class, message = "性別 - 未填寫")
    private Gender gender;

    @Schema(description = "信箱", example = "10646000@ntub.edu.tw")
    @NotBlank(groups = CreateUser.class, message = "信箱 - 未填寫")
    @Size(max = 255, message = "信箱 - 輸入字數大於{max}個字")
    @Email(message = "信箱 - 格式不符合信箱格式")
    private String email;

    @Schema(description = "地址", example = "台北市中正區濟南路321號")
    @NotBlank(groups = CreateUser.class, message = "地址 - 未填寫")
    @Size(max = 50, message = "地址 - 輸入字數大於{max}個字")
    private String address;

    @Schema(description = "生日", type = "string($date)", example = "2020/01/01")
    @Null(groups = UpdateUser.class, message = "生日 - 不得更改")
    @NotNull(groups = CreateUser.class, message = "生日 - 未填寫")
    @Past(message = "生日 - 應為過去日期")
    private LocalDate birthday;

    @Hidden
    @Null(groups = {CreateUser.class, UpdateUser.class}, message = "createDate - 不得填寫")
    private LocalDateTime createDate;

    @Hidden
    @Null(groups = {CreateUser.class, UpdateUser.class}, message = "lastModifyAccount - 不得填寫")
    private String lastModifyAccount;

    @Hidden
    @Null(groups = {CreateUser.class, UpdateUser.class}, message = "lastModifyDate - 不得填寫")
    private LocalDateTime lastModifyDate;

    @Schema(description = "使用者平均評價", example = "4.8")
    @Null(groups = {CreateUser.class, UpdateUser.class}, message = "comment - 不得填寫")
    private Double comment;

    @Hidden
    public String getRoleName() {
        return roleId.name;
    }

    @Hidden
    public String getFullName() {
        return getFullName(null);
    }

    @Hidden
    public String getFullName(@Nullable String delimiter) {
        return (lastName != null ? lastName : "") + (delimiter != null ? delimiter : "") + (firstName != null ? firstName : "");
    }

    @Hidden
    public String getGenderName() {
        return gender.name;
    }

    @Hidden
    public Boolean isEnable() {
        return enable;
    }
}
