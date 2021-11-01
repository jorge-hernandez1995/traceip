package com.init.traceip.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@RedisHash("SendCountry")
public class SendCountry implements Serializable {

	private static final long serialVersionUID = -3648185270388431271L;

	@Id
	private String name;
	private Integer distance;
	private Integer invocations;


}
