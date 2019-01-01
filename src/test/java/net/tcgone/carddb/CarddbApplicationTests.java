/*
Copyright 2018 axpendix@hotmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package net.tcgone.carddb;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import net.tcgone.carddb.model.Card;
import net.tcgone.carddb.model.SetFile;
import net.tcgone.carddb.repository.CardRepository;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.*;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author axpendix@hotmail.com
 * @since 31.12.2018
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class CarddbApplicationTests {

	private static final Logger log = LoggerFactory.getLogger(CarddbApplicationTests.class);
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private ApplicationContext context;

	@Test
	public void contextLoads() throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		Resource[] resources = context.getResources("classpath:/cards/*.yaml");
		List<SetFile> setFiles = new ArrayList<>();
		for (Resource resource : resources) {
				SetFile setFile = mapper.readValue(resource.getInputStream(), SetFile.class);
				setFile.filename = resource.getFilename();
				setFiles.add(setFile);
				validateSetFile(setFile);
		}

		cardRepository.deleteAll();
		for (SetFile setFile : setFiles) {
				for (Card card : setFile.cards) {
						card.set = setFile.set;
				}
				cardRepository.saveAll(setFile.cards);
				log.info("Saved {} to DB", setFile.set.name);
		}
	}

	private void validateSetFile(SetFile setFile){
			if(setFile.set == null || setFile.set.abbr == null){
					throw new IllegalArgumentException("set is empty: "+setFile.filename);
			}
			//|| isBlank(setFile.set.pioId) || isBlank(setFile.set.seoName)
			if(isBlank(setFile.set.abbr) || isBlank(setFile.set.enumId) || isBlank(setFile.set.name) || isBlank(setFile.set.id) ){
					throw new IllegalArgumentException("a property of set is empty: "+setFile.filename);
			}
//        if(setFile.cards.isEmpty()){
//            throw new IllegalArgumentException("no cards in: "+setFile.filename);
//        }
			for (Card card : setFile.cards) {
					validateCard(card, setFile);
			}
	}

	private static final List<String> validSuperTypes = Arrays.asList("Pokémon", "Trainer", "Energy");
	private static final Map<String, Set<String>> validSubTypes = ImmutableMap.<String, Set<String>>builder()
					.put("Pokémon", ImmutableSet.of("Basic", "Stage 1", "Stage 2", "Mega", "EX", "ex", "Break", "LV.X", "Restored", "Baby", "Prime", "GX"))
					.put("Trainer", ImmutableSet.of("Basic", "Supporter", "Stadium", "Item", "Pokémon Tool", "Technical Machine", "Flare", "Ace Spec", "Fossil"))
					.put("Energy", ImmutableSet.of("Basic", "Special")).build();

	private void validateCard(Card card, SetFile setFile){
			if(card == null){
					throw new IllegalArgumentException("card null in: "+setFile.filename);
			}
			// check super type
//        card.superType = WordUtils.capitalizeFully(card.superType);
//        if(card.superType.equals("Pokemon")){
//            card.superType = "Pokémon";
//        }
//        if(!validSuperTypes.contains(card.superType)){
//            throw new IllegalArgumentException(String.format("Invalid super type %s in %s", card.superType, setFile.filename));
//        }
			// check sub type
			// check empty/null fields, number, ordering, etc, attacks, abilities
	}

}
