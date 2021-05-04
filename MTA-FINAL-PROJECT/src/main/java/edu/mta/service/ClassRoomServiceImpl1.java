
package edu.mta.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.mta.dto.ClassRoomRequestDTO;
import edu.mta.model.Class;
import edu.mta.model.ClassRoom;
import edu.mta.model.Room;
import edu.mta.repository.ClassRepository;
import edu.mta.repository.ClassRoomRepository;
import edu.mta.repository.RoomRepository;
import edu.mta.utils.FrequentlyUtils;
import edu.mta.utils.ValidationClassData;
import edu.mta.utils.ValidationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Qualifier("ClassRoomServiceImpl1")
public class ClassRoomServiceImpl1 implements ClassRoomService {

    private ClassRoomRepository classRoomRepository;
    private RoomRepository roomRepository;
    private ValidationClassData validationClassData;
    private FrequentlyUtils frequentlyUtils;
    private ClassRepository classRepository;

    @Autowired
    private ValidationData validationData;

    public ClassRoomServiceImpl1() {
        super();
        // TODO Auto-generated constructor stub }
    }

    @Autowired
    private ClassService classService;

    @Autowired
    private RoomService roomService;

    @Autowired
    public ClassRoomServiceImpl1(ClassRoomRepository classRoomRepository,
                                 RoomRepository roomRepository, ClassRepository classRepository,
                                 @Qualifier("ValidationClassDataImpl1") ValidationClassData validationClassData,
                                 @Qualifier("FrequentlyUtilsImpl1") FrequentlyUtils frequentlyUtils) {

        super();
        this.classRoomRepository = classRoomRepository;
        this.validationClassData = validationClassData;
        this.frequentlyUtils = frequentlyUtils;
        this.roomRepository = roomRepository;
        this.classRepository = classRepository;
    }

    @Override
    public String addNewClassRoom(ClassRoomRequestDTO classRoomRequestDTO) {
        String errorMessage = null;
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = null;
        objectMapper = new ObjectMapper();
        Class classInstance;
        try {
            jsonMap = objectMapper.readValue(objectMapper.writeValueAsString(classRoomRequestDTO), new TypeReference<Map<String, Object>>() {
            });
            // check request body has enough info in right JSON format
            if (!this.frequentlyUtils.checkKeysExist(jsonMap, "beginAt", "finishAt", "weekday", "classID", "roomID")) {
                return "You have to fill all required information!";
            }

            errorMessage = this.validationData.validateClassRoomData(jsonMap);
            if (errorMessage != null) {
                return "Adding new class-room failed because " + errorMessage;
            }

            // check if this class exists
            int classID = Integer.parseInt(jsonMap.get("classID").toString());
            classInstance = classService.findClassByID(classID);
            if (classInstance == null) {
                return "This class do not exist!";
            }

            // check if this room exists
            int roomID = Integer.parseInt(jsonMap.get("roomID").toString());
            Room room = this.roomService.findRoomById(roomID);
            if (room == null) {
                return "This room do not exist!";
            }

            LocalTime beginAt = LocalTime.parse(jsonMap.get("beginAt").toString());
            LocalTime finishAt = LocalTime.parse(jsonMap.get("finishAt").toString());
            int weekday = Integer.parseInt(jsonMap.get("weekday").toString());

            // check if class is available at this duration
            if (this.checkClassAvailable(classID, weekday, beginAt, finishAt) != null) {
                return "This class is not available at this duration!";
            }

            // check if room is available at this duration
            if (this.checkRoomAvailable(roomID, weekday, beginAt, finishAt) != null) {
                return "This room is not available at this duration!";
            }

            ClassRoom classRoom = new ClassRoom(beginAt, finishAt, weekday);
            classRoom.setClassInstance(classInstance);
            classRoom.setRoom(room);

            this.classRoomRepository.save(classRoom);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @Override
    public List<ClassRoom> getListClassRoom(int classID, int roomID) {
        return this.classRoomRepository.findByClassIDAndRoomID(classID, roomID);
    }

    @Override
    public ClassRoom getInfoClassRoom(int classID, int roomID, int weekday, LocalTime checkTime) {
        Optional<ClassRoom> classRoom = this.classRoomRepository.findByClassIDAndRoomIDAndWeekday(classID, roomID,
                weekday, checkTime);

        return classRoom.isPresent() ? classRoom.get() : null;
    }

    @Override
    public List<ClassRoom> checkClassAvailable(int classID, int weekday, LocalTime beginAt, LocalTime finishAt) {
        List<ClassRoom> listClass = this.classRoomRepository.findClassesByIdAndWeekdayAndDuration(classID, weekday,
                beginAt, finishAt);
        if (listClass == null || listClass.isEmpty()) {
            return null;
        }

        return listClass;
    }

    @Override
    public List<ClassRoom> checkRoomAvailable(int roomID, int weekday, LocalTime beginAt, LocalTime finishAt) {
        List<ClassRoom> listRoom = this.classRoomRepository.findRoomByIdAndWeekdayAndDuration(roomID, weekday, beginAt,
                finishAt);
        if (listRoom == null || listRoom.isEmpty()) {
            return null;
        }
        return listRoom;
    }

    @Override
    public ClassRoom findClassRoomByID(int id) {
        Optional<ClassRoom> classRoom = this.classRoomRepository.findById(id);
        if (classRoom.isPresent()) {
            return classRoom.get();
        }
        return null;
    }

    @Override
    public void updateClassRoomInfo(ClassRoom classRoom) {
        this.classRoomRepository.save(classRoom);
        return;
    }

    @Override
    public boolean deleteClassRoom(ClassRoom classRoom) {
        try {
            this.classRoomRepository.deleteById(classRoom.getId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public List<ClassRoom> findClassRoomByWeekday(int currentDay) {
        List<ClassRoom> listClassRoom = this.classRoomRepository.findByWeekday(currentDay);
        if (listClassRoom == null || listClassRoom.isEmpty()) {
            return null;
        }
        return listClassRoom;
    }

    @Override
    public List<ClassRoom> getListClassRoomByClassID(int classID) {
        List<ClassRoom> listClassRoom = this.classRoomRepository.findByClassID(classID);
        if (listClassRoom == null || listClassRoom.isEmpty()) {
            return null;
        }
        return listClassRoom;
    }

    @Override
    public List<Room> getListRoomByClassID(int classID) {
        List<Room> listRoom = this.classRoomRepository.findListRoomByClassID(classID);
        if (listRoom == null || listRoom.isEmpty()) {
            return null;
        }
        return listRoom;
    }

    @Override
    public List<ClassRoom> checkListClassRoom(List<ClassRoom> listClassRoom, int roomID) {
        Iterator<ClassRoom> listIte = listClassRoom.iterator();
        String errorMessage = null;
        String listOfInvalidRows = "";
        ClassRoom tmpClassRoom = null;
        int weekday = -1;
        List<ClassRoom> tmpList = null;
        LocalTime beginAt = null;
        LocalTime finishAt = null;

        // the 1st row of file excel is header => list email begin from 2nd row
        int rowCounter = 1;
        boolean isConflict = false;
        while (listIte.hasNext()) {
            tmpClassRoom = listIte.next();
            rowCounter++;
            System.out.println("========== row  = " + rowCounter);

            errorMessage = this.validationClassData
                    .validateClassNameData(tmpClassRoom.getClassInstance().getClassName());
            if (errorMessage != null) {
                System.out.println("============= class name is invalid = " + errorMessage);
                listOfInvalidRows += rowCounter + ", ";
                listIte.remove();
                continue;
            }

            weekday = tmpClassRoom.getWeekday();
            if (weekday < 2 || weekday > 6) {
                System.out.println("============= weekday is invalid: weekday =  " + weekday);
                listOfInvalidRows += rowCounter + ", ";
                listIte.remove();
                continue;
            }

            beginAt = tmpClassRoom.getBeginAt();
            finishAt = tmpClassRoom.getFinishAt();
            if (beginAt == null || finishAt == null) {
                System.out.println("============= beginAt or finishAt is nul");
                listOfInvalidRows += rowCounter + ", ";
                listIte.remove();
                continue;
            }

            if (beginAt.isAfter(finishAt)) {
                System.out.println("============= beginAt is after finishAt");
                listOfInvalidRows += rowCounter + ", ";
                listIte.remove();
                continue;
            }

            //check if 2 in the same time frame

            String className = tmpClassRoom.getClassInstance().getClassName();
            System.out.println("className = " + className);

            Optional<Class> classOpt = this.classRepository.findByClassName(className);
            if (classOpt == null) {
                System.out.println("============= class is not exist");
                listOfInvalidRows += rowCounter + ", ";
                listIte.remove();
                continue;
            }

            int tmpClassID = classOpt.get().getId();
            System.out.println("class id  = " + tmpClassID);

            tmpList = this.classRoomRepository.findByWeekday(tmpClassRoom.getWeekday());
            for (ClassRoom tmpTarget : tmpList) {
                if (tmpTarget.getClassInstance().getId() != tmpClassID) {
                    continue;
                }

                if (!this.frequentlyUtils.checkTwoTimeConflict(beginAt, tmpTarget.getBeginAt(),
                        finishAt, tmpTarget.getFinishAt())) {
                    System.out.println("============= class is conflict ");
                    listOfInvalidRows += rowCounter + ", ";
                    isConflict = true;
                    listIte.remove();
                    break;
                }
            }

            if (isConflict == false) {
                tmpList = this.classRoomRepository.findAllByRoomID(roomID);
                for (ClassRoom tmpTarget : tmpList) {
                    if (tmpTarget.getClassInstance().getId() != tmpClassID) {
                        continue;
                    }

                    if (!this.frequentlyUtils.checkTwoTimeConflict(beginAt, tmpTarget.getBeginAt(),
                            finishAt, tmpTarget.getFinishAt())) {
                        System.out.println("============= room is conflict");
                        listOfInvalidRows += rowCounter + ", ";
                        isConflict = true;
                        listIte.remove();
                        break;
                    }
                }
            }

        }

        if (listClassRoom == null || listClassRoom.isEmpty()) {
            return null;
        }

        ClassRoom lastRow = new ClassRoom();
        lastRow.setClassInstance(new Class());
        lastRow.getClassInstance().setIdentifyString(listOfInvalidRows);
        listClassRoom.add(lastRow);
        return listClassRoom;
    }

    @Override
    public boolean addNewClassRoom(ClassRoom classRoom, int roomID) {
        if (classRoom.getId() > 0) {
            classRoom.setId(-1);
        }

        Room room = this.roomRepository.findById(roomID).get();

        String className = classRoom.getClassInstance().getClassName();
        System.out.println("className = " + className);
        Class classIntance = this.classRepository.findByClassName(className).get();

        classRoom.setRoom(room);
        classRoom.setClassInstance(classIntance);

        this.classRoomRepository.save(classRoom);
        return true;

    }

    @Override
    public Page<ClassRoom> getClassRoomBySemesterAndCourseAndClass(Integer courseId, Integer classId, Integer roomId, Pageable pageable) {
        if (courseId != null && classId == null) {
            List<Integer> listClassId = this.classRepository.findListClassIdByCourseID(courseId);
            if (listClassId.size() > 0) {
                return this.classRoomRepository.getListClassRoom(listClassId, pageable);
            } else {
                return null;
            }
        } else if (classId != null) {
            return this.classRoomRepository.findByClassID(classId, pageable);
        } else if (roomId != null) {
            return this.classRoomRepository.findAllByRoomID(roomId, pageable);
        } else {
            return this.classRoomRepository.findAll(pageable);
        }
    }

}
