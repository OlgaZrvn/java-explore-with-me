package ru.yandex.practicum.compilation;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

  //  Compilation toCompilation(NewCompilationDto newCompilationDto);
    CompilationDto toCompilationDto(Compilation compilation);
}
