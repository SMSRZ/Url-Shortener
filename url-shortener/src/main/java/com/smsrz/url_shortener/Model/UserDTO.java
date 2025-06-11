package com.smsrz.url_shortener.Model;

import java.io.Serializable;

public record UserDTO(long id, String name)  implements Serializable {
}
