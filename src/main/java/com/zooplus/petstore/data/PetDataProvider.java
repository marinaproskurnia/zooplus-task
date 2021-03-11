package com.zooplus.petstore.data;

import com.zooplus.petstore.model.Category;
import com.zooplus.petstore.model.Pet;
import com.zooplus.petstore.model.Tag;
import org.assertj.core.util.Lists;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.zooplus.petstore.model.Status.AVAILABLE;
import static java.lang.Integer.MAX_VALUE;

public final class PetDataProvider {

    private static final ThreadLocalRandom RANDOM_NUMBER_GENERATOR = ThreadLocalRandom.current();

    public static Pet getValidPetData() {
        return Pet.builder()
                .id(RANDOM_NUMBER_GENERATOR.nextInt(0, MAX_VALUE))
                .name("Dundee")
                .category(new Category(RANDOM_NUMBER_GENERATOR.nextInt(MAX_VALUE), "Crocodile"))
                .photoUrls(List.of("https://i.guim.co.uk/img/media/8c5d882b8501dda8499819d8cc98f6e13fb6c433/" +
                                "0_0_3264_2119/master/3264.jpg?width=620&quality=45&auto=format&fit=max&dpr=2&s=" +
                                "073b4106cc1671d82af95b3c394864b9",
                        "https://s.yimg.com/ny/api/res/1.2/GDZTSHJFOA_8Ww4XpTwR8Q--/YXBwaWQ9aGlnaGxhbmRlcjt3PTk2MDto" +
                                "PTU2MA--/https://67.media.tumblr.com/6669c3a60b21717582c6d9f18bf3f6aa/tumblr_inline_" +
                                "obop4fohhx1ttbdeg_1280.jpg"))
                .tags(List.of(new Tag(RANDOM_NUMBER_GENERATOR.nextInt(MAX_VALUE), "Reptiles")))
                .status(AVAILABLE)
                .build();
    }

    public static Pet getPetWithInvalidId(final long invalidId) {
        return Pet.builder()
                .id(invalidId)
                .name("Rex")
                .category(new Category(invalidId, null))
                .photoUrls(Lists.emptyList())
                .tags(List.of(new Tag(invalidId, null)))
                .status(AVAILABLE)
                .build();
    }
}
