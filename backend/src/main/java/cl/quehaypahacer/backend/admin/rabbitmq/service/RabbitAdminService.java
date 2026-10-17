package cl.quehaypahacer.backend.admin.rabbitmq.service;

import cl.quehaypahacer.backend.admin.rabbitmq.dto.BindingRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.ExchangeRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.QueueRequestDTO;
import cl.quehaypahacer.backend.admin.rabbitmq.dto.QueueStatusDTO;

public interface RabbitAdminService {

    void crearCola(QueueRequestDTO dto);

    void eliminarCola(String nombre);

    QueueStatusDTO obtenerEstadoCola(String nombre);

    void crearExchange(ExchangeRequestDTO dto);

    void eliminarExchange(String nombre);

    void crearBinding(BindingRequestDTO dto);

    void eliminarBinding(BindingRequestDTO dto);
}
