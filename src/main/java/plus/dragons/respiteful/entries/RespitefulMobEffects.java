package plus.dragons.respiteful.entries;

import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import plus.dragons.respiteful.Respiteful;

import static plus.dragons.respiteful.Respiteful.REGISTRATE;

public class RespitefulMobEffects {
    public static final RegistryEntry<MobEffect> VITALITY = REGISTRATE.mobEffect("vitality")
        .description("Grants resistance for Wither effect and increases healing amount; " +
            "higher levels grants stronger resistance and gives more additional healing amount.")
        .category(MobEffectCategory.BENEFICIAL)
        .color(0x519641)
        .register();

    public static final RegistryEntry<MobEffect> TENACITY = REGISTRATE.mobEffect("tenacity")
        .description("Increases armor toughness and knockback resistance; higher levels gives more armor toughness and knockback resistance.")
        .category(MobEffectCategory.BENEFICIAL)
        .color(0xECCB45)
        .onRegister(effect -> effect
            .addAttributeModifier(Attributes.ARMOR_TOUGHNESS,
                "6624c857-48be-49ff-921b-15172d3c19c1", 2, AttributeModifier.Operation.ADDITION)
            .addAttributeModifier(Attributes.KNOCKBACK_RESISTANCE,
                "d673225e-8a54-4362-987f-5e82f7ca99e3", 0.2, AttributeModifier.Operation.ADDITION))
        .register();

    public static final RegistryEntry<MobEffect> MATURITY = REGISTRATE.mobEffect("maturity")
        .description("Grants immunity for harmful effects from food and increases food restore when eating; higher levels restores more food.")
        .category(MobEffectCategory.BENEFICIAL)
        .color(0x783E27)
        .register();

    public static void register(IEventBus modBus) {
    }

    @EventBusSubscriber(modid = Respiteful.ID)
    public static class ForgeEventHandler {

        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void increaseHealingAmount(LivingHealEvent event) {
            var entity = event.getEntity();
            var vitality = entity.getEffect(VITALITY.get());
            if (vitality == null)
                return;
            float amount = event.getAmount();
            int amplifier = vitality.getAmplifier();
            float increase = Math.min(amount, 1L << amplifier);
            event.setAmount(amount + increase);
        }

        @SubscribeEvent(priority = EventPriority.HIGHEST)
        public static void witherResistance(MobEffectEvent.Applicable event) {
            var wither = event.getEffectInstance();
            if (wither.getEffect() != MobEffects.WITHER)
                return;
            var entity = event.getEntity();
            var vitality = entity.getEffect(VITALITY.get());
            if (vitality == null)
                return;
            if (wither.getAmplifier() <= vitality.getAmplifier())
                event.setResult(Event.Result.DENY);
        }

    }

}
