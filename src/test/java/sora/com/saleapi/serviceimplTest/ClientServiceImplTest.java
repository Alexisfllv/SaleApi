package sora.com.saleapi.serviceimplTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import sora.com.saleapi.dto.ClientDTO.ClientDTORequest;
import sora.com.saleapi.dto.ClientDTO.ClientDTOResponse;
import sora.com.saleapi.dto.RoleDTO.RoleDTORequest;
import sora.com.saleapi.dto.RoleDTO.RoleDTOResponse;
import sora.com.saleapi.entity.Client;
import sora.com.saleapi.entity.Role;
import sora.com.saleapi.exception.ResourceNotFoundException;
import sora.com.saleapi.mapper.ClientMapper;
import sora.com.saleapi.repo.ClientRepo;
import sora.com.saleapi.repo.RoleRepo;
import sora.com.saleapi.service.Impl.ClientServiceImpl;

// static
import java.lang.module.ResolutionException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
public class ClientServiceImplTest {
    // mock
    @Mock
    private ClientRepo clientRepo;

    @Mock
    private ClientMapper clientMapper;

    @InjectMocks
    private ClientServiceImpl clientService;

    // variables reutilizables
    private Client client1;
    private Client client2;
    private Client client3;

    private ClientDTOResponse clientDTOResponse1;
    private ClientDTOResponse clientDTOResponse2;
    private ClientDTOResponse clientDTOResponse3;

    private ClientDTORequest clientDTORequest1;
    private Client clientRequest1;

    // asignar valores
    @BeforeEach
    public void setUp() {
        // start
        client1 = new Client(1L,"Yuno","Schiz","Yunn@gmail.com","C-23321","987654321","LIMA-LIMA-ATE");
        client2 = new Client(2L, "Asta", "Storm", "asta@gmail.com", "C-23322", "912345678", "LIMA-LIMA-SMP");
        client3 = new Client(3L, "Noelle", "Silva", "noelle@gmail.com", "C-23323", "923456789", "LIMA-LIMA-MIRAFLORES");

        clientDTOResponse1 = new ClientDTOResponse(1L,"Yuno","Schiz","Yunn@gmail.com","C-23321","987654321","LIMA-LIMA-ATE");
        clientDTOResponse2 = new ClientDTOResponse(2L, "Asta", "Storm", "asta@gmail.com", "C-23322", "912345678", "LIMA-LIMA-SMP");
        clientDTOResponse3 = new ClientDTOResponse(3L, "Noelle", "Silva", "noelle@gmail.com", "C-23323", "923456789", "LIMA-LIMA-MIRAFLORES");

        clientDTORequest1 = new ClientDTORequest("Yuno","Schiz","Yunn@gmail.com","C-23321","987654321","LIMA-LIMA-ATE");
        clientRequest1 = new Client(null,"Yuno","Schiz","Yunn@gmail.com","C-23321","987654321","LIMA-LIMA-ATE");
    }

    @Nested
    @DisplayName("findAll()")
    class FindAll {

        // test listado client
        @Test
        @DisplayName("Should return all clients when findAll is called")
        void shouldReturnAllClientsWhenFindAll() {
            // Arrange
            List<Client> clientList = List.of(client1,client2,client3);
            when(clientRepo.findAll()).thenReturn(clientList);
            when(clientMapper.toClientDTOResponse(client1)).thenReturn(clientDTOResponse1);
            when(clientMapper.toClientDTOResponse(client2)).thenReturn(clientDTOResponse2);
            when(clientMapper.toClientDTOResponse(client3)).thenReturn(clientDTOResponse3);
            // Act
            List<ClientDTOResponse> result = clientService.findAll();
            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(3, result.size()),
                    () -> assertEquals(clientDTOResponse1,result.get(0)),
                    () -> assertEquals(clientDTOResponse2,result.get(1)),
                    () -> assertEquals(clientDTOResponse3,result.get(2))
            );
            // Verify
            verify(clientRepo, times(1)).findAll();
            verify(clientMapper, times(1)).toClientDTOResponse(client1);
            verify(clientMapper, times(1)).toClientDTOResponse(client2);
            verify(clientMapper, times(1)).toClientDTOResponse(client3);
            verifyNoMoreInteractions(clientRepo, clientMapper);
        }

    }

    @Nested
    @DisplayName("findAllPage(Pageable)")
    class FindAllPage {

        // test de listado client paginado
        @Test
        @DisplayName("Should return paged clients when findAllPage is called")
        void shouldReturnPagedClientsWhenFindAllPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Client> clientPage = new PageImpl<>(List.of(client1,client2));
            ClientDTOResponse clidtores1 = clientDTOResponse1;
            ClientDTOResponse clidtores2 = clientDTOResponse2;

            when(clientRepo.findAll(pageable)).thenReturn(clientPage);
            when(clientMapper.toClientDTOResponse(client1)).thenReturn(clidtores1);
            when(clientMapper.toClientDTOResponse(client2)).thenReturn(clidtores2);

            // Act
            Page<ClientDTOResponse> result = clientService.findAllPage(pageable);

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(2,result.getContent().size()),
                    () -> assertEquals(clidtores1,result.getContent().get(0)),
                    () -> assertEquals(clidtores2,result.getContent().get(1))
            );
            // Verify
            verify(clientRepo, times(1)).findAll(pageable);
            verify(clientMapper, times(1)).toClientDTOResponse(client1);
            verify(clientMapper, times(1)).toClientDTOResponse(client2);
            verifyNoMoreInteractions(clientRepo, clientMapper);
        }

        // test de listado paginado vacio
        @Test
        @DisplayName("Should return empty paged clients when findAllPage is called")
        void shouldReturnPagedEmptyClientsWhenFindAllPage() {
            // Arrange
            Pageable pageable = PageRequest.of(0, 10);
            Page<Client> clientPage = new PageImpl<>(List.of());
            when(clientRepo.findAll(pageable)).thenReturn(clientPage);
            // Act
            Page<ClientDTOResponse> result = clientService.findAllPage(pageable);

            // Assert
            assertAll(
                    () -> assertEquals(0,result.getContent().size())
            );
            // Verify
            verify(clientRepo, times(1)).findAll(pageable);
            verifyNoMoreInteractions(clientRepo);
        }


    }

    @Nested
    @DisplayName("findById(Long id)")
    class FindById {

        // buscar client existente
        @Test
        @DisplayName("Should return client when findbyId is called")
        void shouldReturnClientWhenFindbyId() {
            // Arrange
            Long clientId = 1L;
            Client clientExist = client1;
            ClientDTOResponse clidtores1 = clientDTOResponse1;
            when(clientRepo.findById(clientId)).thenReturn(Optional.of(clientExist));
            when(clientMapper.toClientDTOResponse(clientExist)).thenReturn(clidtores1);
            // Act
            ClientDTOResponse result = clientService.findById(clientId);

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(clidtores1,result),
                    () -> assertEquals(clientId,result.clientId()),
                    () -> assertEquals("Yuno",result.clientFirstName()),
                    () -> assertEquals("Schiz",result.clientLastName()),
                    () -> assertEquals("Yunn@gmail.com",result.clientEmail()),
                    () -> assertEquals("C-23321",result.clientCardId()),
                    () -> assertEquals("987654321",result.clientPhone()),
                    () -> assertEquals("LIMA-LIMA-ATE",result.clientAddress())
            );
            // Verify
            verify(clientRepo, times(1)).findById(clientId);
            verify(clientMapper, times(1)).toClientDTOResponse(clientExist);
            verifyNoMoreInteractions(clientRepo, clientMapper);
        }
        // buscar cliente no existente
        @Test
        @DisplayName("Should throw ResourceNotFoundException when findById is called whith invalid ClientId")
        void shouldThrowNotFoundWhenFindByIdIsInvalidClientId() {

            // Arrange
            Long clientId = 99L;
            when(clientRepo.findById(clientId)).thenReturn(Optional.empty());
            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> clientService.findById(clientId));

            // Verify
            verify(clientRepo, times(1)).findById(clientId);
            verifyNoMoreInteractions(clientRepo);
        }
    }

    @Nested
    @DisplayName("save(ClientDTORequest)")
    class Save {

        // save client
        @Test
        @DisplayName("should return created client when save is called")
        void shouldReturnCreatedClientWhenSave() {
            // Arrange
            when(clientMapper.toClient(any(ClientDTORequest.class))).thenReturn(clientRequest1);
            when(clientRepo.save(any(Client.class))).thenReturn(client1);
            when(clientMapper.toClientDTOResponse(any(Client.class))).thenReturn(clientDTOResponse1);

            // Act
            ClientDTOResponse result = clientService.save(clientDTORequest1);

            // Assert
            assertAll(
                    () -> assertEquals(clientDTOResponse1,result),
                    () -> assertNotNull(result),
                    () -> assertEquals(1L,result.clientId()),
                    () -> assertEquals("Yuno",result.clientFirstName()),
                    () -> assertEquals("Schiz",result.clientLastName()),
                    () -> assertEquals("Yunn@gmail.com",result.clientEmail()),
                    () -> assertEquals("C-23321",result.clientCardId()),
                    () -> assertEquals("987654321",result.clientPhone()),
                    () -> assertEquals("LIMA-LIMA-ATE",result.clientAddress())
            );
            // Verify
            verify(clientMapper, times(1)).toClient(any(ClientDTORequest.class));
            verify(clientRepo, times(1)).save(any(Client.class));
            verify(clientMapper, times(1)).toClientDTOResponse(any(Client.class));
            verifyNoMoreInteractions(clientRepo, clientMapper);
        }
    }

    @Nested
    @DisplayName("update(Long id, ClientDTORequest)")
    class Update {
        // update client correcot
        @Test
        @DisplayName("should return updated client when update is called")
        void shouldReturnUpdatedClientWhenUpdate() {
            // Arrange
            Long clientId = 1L;
            Client ClientUpdate = new Client(null,"Ferr","Faw","Ferr@gmail.com","C-233333","963258741","LIMA-LIMA-CHOSICA");
            ClientDTORequest clientDTOReqUpdate = new ClientDTORequest("Ferr","Faw","Ferr@gmail.com","C-233333","963852741","LIMA-LIMA-CHOSICA");
            ClientDTOResponse clientDTOResUpdate = new ClientDTOResponse(clientId,"Ferr","Faw","Ferr@gmail.com","C-233333","963852741","LIMA-LIMA-CHOSICA");
            when(clientRepo.findById(clientId)).thenReturn(Optional.of(client1));
            when(clientRepo.save(any(Client.class))).thenReturn(ClientUpdate);
            when(clientMapper.toClientDTOResponse(any(Client.class))).thenReturn(clientDTOResUpdate);
            // Act
            ClientDTOResponse result =  clientService.update(clientId, clientDTOReqUpdate);

            // Assert
            assertAll(
                    () -> assertEquals(clientDTOResUpdate,result),
                    () -> assertNotNull(result),
                    () -> assertEquals(1L,result.clientId()),
                    () -> assertEquals("Ferr",result.clientFirstName()),
                    () -> assertEquals("Faw",result.clientLastName()),
                    () -> assertEquals("Ferr@gmail.com",result.clientEmail()),
                    () -> assertEquals("C-233333",result.clientCardId()),
                    () -> assertEquals("963852741",result.clientPhone()),
                    () -> assertEquals("LIMA-LIMA-CHOSICA",result.clientAddress())
            );
            // Verify
            verify(clientRepo, times(1)).findById(clientId);
            verify(clientRepo, times(1)).save(any(Client.class));
            verify(clientMapper, times(1)).toClientDTOResponse(any(Client.class));
            verifyNoMoreInteractions(clientRepo, clientMapper);
        }

        // test de update fallido por id resource not found exception
        @Test
        @DisplayName("Should throw ResourceNotFoundException when update is called with invalid ClientId")
        void shouldThrowNotFoundWhenUpdateIsCalledWithInvalidClientId() {

            // Arrange
            Long clientIdnoExist = 99L;
            when(clientRepo.findById(clientIdnoExist)).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> clientService.update(clientIdnoExist,null));

            // Verify
            verify(clientRepo, times(1)).findById(clientIdnoExist);
            verifyNoMoreInteractions(clientRepo);
        }

    }

    @Nested
    @DisplayName("deleteById(Long id)")
    class DeleteById {
        // test eliminar un client por id
        @Test
        @DisplayName("Should delete client when deleteById is called")
        void shouldReturnDeletedClientWhenDeleteById() {

            // Arrange
            Long clientId = 1L;
            Client clientExist = client1;
            when(clientRepo.findById(clientId)).thenReturn(Optional.of(clientExist));

            // Act
            clientService.deleteById(clientId);
            // Assert & Verify
            verify(clientRepo, times(1)).findById(clientId);
            verify(clientRepo, times(1)).delete(clientExist);
            verifyNoMoreInteractions(clientRepo);
        }

        // test eliminar un client no existente por id
        @Test
        @DisplayName("Should throw ResourceNotFoundException when deleteById is called with invalid ClientId")
        void shouldThrowNotFoundWhenDeleteByIdIsCalledWithInvalidClientId() {
            // Arrange
            Long clientIdnoExist = 99L;
            when(clientRepo.findById(clientIdnoExist)).thenReturn(Optional.empty());
            // Act & Assert
            assertThrows(ResourceNotFoundException.class, () -> clientService.deleteById(clientIdnoExist));
            // Verify
            verify(clientRepo, times(1)).findById(clientIdnoExist);
            verifyNoMoreInteractions(clientRepo);
        }
    }
}
