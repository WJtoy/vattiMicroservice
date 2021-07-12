package com.kkl.kklplus.b2b.vatti.http.command;

import com.kkl.kklplus.b2b.vatti.http.request.ModeifyInstallOrderRequestParam;
import com.kkl.kklplus.b2b.vatti.http.request.ModeifyRepairOrderRequestParam;
import com.kkl.kklplus.b2b.vatti.http.request.RequestParam;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class OperationCommand {

    public enum OperationCode {

        MODEIFYINSTALLSERVICEORDER(1001, "修改安装工单", "/api/installorder/modify", ModeifyInstallOrderRequestParam.class),
        MODEIFYREPAIRSERVICEORDER(1002, "修改维修工单", "/api/repairorder/modify", ModeifyRepairOrderRequestParam.class),
        TODOLIST(1003, "获取待办", "/api/ext/todolist", ModeifyInstallOrderRequestParam.class),
        ORDER(1004, "获取工单详情", "/api/ext/order", ModeifyInstallOrderRequestParam.class);

        public int code;
        public String name;
        public String apiUrl;
        public Class reqBodyClass;

        private OperationCode(int code, String name, String apiUrl, Class reqBodyClass) {
            this.code = code;
            this.name = name;
            this.apiUrl = apiUrl;
            this.reqBodyClass = reqBodyClass;
        }
    }

    @Getter
    @Setter
    private OperationCode opCode;

    @Getter
    @Setter
    private RequestParam reqBody;

    public static OperationCommand newInstance(OperationCode opCode, RequestParam reqBody) {
        OperationCommand command = new OperationCommand();
        command.opCode = opCode;
        command.reqBody = reqBody;
        return command;
    }
}
