package tw.edu.ntub.imd.camping.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Data;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tw.edu.ntub.imd.camping.bean.UpdatePasswordBean;
import tw.edu.ntub.imd.camping.bean.UserBean;
import tw.edu.ntub.imd.camping.config.util.SecurityUtils;
import tw.edu.ntub.imd.camping.databaseconfig.enumerate.Experience;
import tw.edu.ntub.imd.camping.service.UserService;
import tw.edu.ntub.imd.camping.util.http.BindingResultUtils;
import tw.edu.ntub.imd.camping.util.http.ResponseEntityBuilder;
import tw.edu.ntub.imd.camping.util.json.object.ObjectData;
import tw.edu.ntub.imd.camping.validation.CreateUser;
import tw.edu.ntub.imd.camping.validation.UpdateUser;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.Optional;

@Tag(name = "User", description = "使用者API")
@RestController
@RequestMapping(path = "/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            tags = "User",
            method = "POST",
            summary = "使用者註冊",
            description = "使用者註冊，預設權限為一般使用者",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "註冊成功",
                    content = @Content(
                            mediaType = "application/json"
                    )
            )
    )
    @PostMapping(path = "")
    public ResponseEntity<String> register(@Validated(CreateUser.class) @RequestBody UserBean userBean, BindingResult bindingResult) {
        BindingResultUtils.validate(bindingResult);
        userService.save(userBean);
        return ResponseEntityBuilder.buildSuccessMessage("註冊成功");
    }

    @Operation(
            tags = "User",
            method = "GET",
            summary = "查詢使用者資訊",
            description = "查詢使用者資訊",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "查詢成功",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UserBean.class)
                    )
            )
    )
    @GetMapping(path = "")
    public ResponseEntity<String> getUserInfo() {
        String account = SecurityUtils.getLoginUserAccount();

        Optional<UserBean> optionalUser = userService.getById(account);
        if (optionalUser.isPresent()) {
            UserBean userBean = optionalUser.get();
            ObjectData data = new ObjectData();
            data.add("account", userBean.getAccount());
            data.add("firstName", userBean.getFirstName());
            data.add("lastName", userBean.getLastName());
            data.add("nickName", userBean.getNickName());
            data.add("gender", userBean.getGenderName());
            data.add("birthday", userBean.getBirthday());
            data.add("experience", userBean.getExperience().ordinal());
            data.add("email", userBean.getEmail());
            data.add("address", userBean.getAddress());
            data.add("comment", userBean.getComment());
            return ResponseEntityBuilder.success("查詢成功")
                    .data(data)
                    .build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            tags = "User",
            method = "PATCH",
            summary = "更新使用者資訊",
            description = "更新使用者資訊",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "更新成功",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    )
    @PatchMapping(path = "")
    public ResponseEntity<String> updateUserInfo(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdateUserInfoSchema.class)
                    )
            ) @Validated(UpdateUser.class) @RequestBody UserBean user,
            BindingResult bindingResult
    ) {
        String account = SecurityUtils.getLoginUserAccount();

        BindingResultUtils.validate(bindingResult);
        userService.update(account, user);
        return ResponseEntityBuilder.buildSuccessMessage("更新成功");
    }

    @Operation(
            tags = "User",
            method = "POST",
            summary = "修改密碼",
            description = "修改密碼",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = UpdatePasswordBean.class)
                    )
            ),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "修改成功",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    )
    @PatchMapping(path = "/password")
    public ResponseEntity<String> changePassword(
            @Valid @RequestBody UpdatePasswordBean updatePasswordBean,
            BindingResult bindingResult
    ) {
        BindingResultUtils.validate(bindingResult);
        String password = updatePasswordBean.getPassword();
        String newPassword = updatePasswordBean.getNewPassword();
        String account = SecurityUtils.getLoginUserAccount();
        userService.updatePassword(account, password, newPassword);

        return ResponseEntityBuilder.success().message("更新成功").build();
    }

    @Operation(
            tags = "User",
            method = "GET",
            summary = "查詢露營經驗列表",
            description = "查詢露營經驗列表",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "查詢成功",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            array = @ArraySchema(schema = @Schema(implementation = ExperienceSchema.class))
                    )
            )
    )
    @GetMapping(path = "/experience")
    public ResponseEntity<String> getExperienceList() {
        return ResponseEntityBuilder.success("查詢成功")
                .data(Arrays.asList(Experience.values()), this::addExperienceToObjectData)
                .build();
    }

    private void addExperienceToObjectData(ObjectData data, Experience experience) {
        data.add("id", experience.id);
        data.add("display", experience.toString());
    }

    @Operation(
            tags = "User",
            method = "POST",
            summary = "新增使用者評價",
            description = "評價使用者，不得重複評價，評價分數介於1 <= comment <= 5",
            parameters = @Parameter(name = "account", description = "使用者帳號", example = "test"),
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "評價成功",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE
                    )
            )
    )
    @PostMapping(path = "/{account}/comment")
    public ResponseEntity<String> createComment(
            @PathVariable String account,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(
                                    implementation = UserCommentSchema.class
                            )
                    )
            ) @RequestBody String requestBodyJsonString
    ) {
        ObjectData requestBody = new ObjectData(requestBodyJsonString);
        Integer comment = requestBody.getInt("comment");
        userService.createComment(account, comment.byteValue());
        return ResponseEntityBuilder.buildSuccessMessage("評價成功");
    }

    // |---------------------------------------------------------------------------------------------------------------------------------------------|
    // |----------------------------------------------------------以下為Swagger所需使用的Schema---------------------------------------------------------|
    // |---------------------------------------------------------------------------------------------------------------------------------------------|

    @Schema(name = "更新使用者資訊", description = "更新使用者資訊")
    @Data
    private static class UpdateUserInfoSchema {
        @Schema(description = "露營經驗(0: 新手/ 1: 有過幾次經驗)", type = "int", example = "0")
        private Experience experience;

        @Schema(description = "暱稱", example = "煞氣a小明")
        private String nickName;

        @Schema(description = "信箱", example = "10646000@ntub.edu.tw")
        private String email;

        @Schema(description = "地址", example = "台北市中正區濟南路321號")
        private String address;
    }

    @Schema(name = "露營經驗", description = "露營經驗")
    @Data
    private static class ExperienceSchema {
        @Schema(description = "編號", example = "0")
        private String id;

        @Schema(description = "顯示文字", example = "0~5次 新手")
        private String display;
    }

    @Schema(name = "使用者評價資料", description = "使用者評價資料")
    @Data
    private static class UserCommentSchema {
        @Schema(description = "評價分數", minimum = "1", maximum = "5")
        private Byte comment;
    }
}
