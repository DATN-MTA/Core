package edu.mta.dto;

public class TeacherClassResponseDTO extends TeacherClassDTO{
    private ClassDTO classDTO;
    private AccountDataDTO accountDataDTO;

    public ClassDTO getClassDTO() {
        return classDTO;
    }

    public void setClassDTO(ClassDTO classDTO) {
        this.classDTO = classDTO;
    }

    public AccountDataDTO getAccountDataDTO() {
        return accountDataDTO;
    }

    public void setAccountDataDTO(AccountDataDTO accountDataDTO) {
        this.accountDataDTO = accountDataDTO;
    }
}
