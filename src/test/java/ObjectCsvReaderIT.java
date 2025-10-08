import model.EnumDTO;
import model.InvalidDTO;
import model.NameDTO;
import model.OrdinalDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ObjectCsvReaderIT extends BaseIT {

    @Test
    void shouldParseIntoOrdinalDto() {
        loadAndAssertWithMappings(
                "mapping_parser/ordinal_dto/expected.json",
                "mapping_parser/ordinal_dto/input.csv",
                OrdinalDTO.class,
                false
        );
    }

    @Test
    void shouldParseIntoNameDto() {
        loadAndAssertWithMappings(
                "mapping_parser/name_dto/expected.json",
                "mapping_parser/name_dto/input.csv",
                NameDTO.class,
                true
        );
    }

    @Test()
    void shouldThrowIllegalStateExceptionOnBrokenDto() {
        Assertions.assertThrows(IllegalStateException.class, () -> {
            loadAndAssertWithMappings(
                    "mapping_parser/ordinal_dto/expected.json",
                    "mapping_parser/ordinal_dto/input.csv",
                    InvalidDTO.class,
                    false
            );
        });
    }

    @Test
    void shouldParseEnumDTO() {
        loadAndAssertWithMappings(
                "mapping_parser/enum_dto/expected.json",
                "mapping_parser/enum_dto/input.csv",
                EnumDTO.class,
                true
        );
    }
}