package com.nerdery.jhipster.web.rest.mapper;

import com.nerdery.jhipster.domain.RefreshToken;
import com.nerdery.jhipster.web.rest.dto.RefreshTokenDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper for the entity RefreshToken and its DTO RefreshTokenDTO.
 */
@Mapper(componentModel = "spring", uses = {UserMapper.class, })
public interface RefreshTokenMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.email", target = "userEmail")
    RefreshTokenDTO refreshTokenToRefreshTokenDTO(RefreshToken refreshToken);

    List<RefreshTokenDTO> refreshTokensToRefreshTokenDTOs(List<RefreshToken> refreshTokens);

    @Mapping(source = "userId", target = "user")
    RefreshToken refreshTokenDTOToRefreshToken(RefreshTokenDTO refreshTokenDTO);

    List<RefreshToken> refreshTokenDTOsToRefreshTokens(List<RefreshTokenDTO> refreshTokenDTOs);
}
