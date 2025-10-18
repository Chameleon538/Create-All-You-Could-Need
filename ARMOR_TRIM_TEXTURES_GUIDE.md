# How to Create Armor Trim Textures

This guide explains how to create and add armor trim textures to armor pieces in Minecraft Forge mods.

## Overview

Armor trims in Minecraft 1.20+ allow players to customize their armor appearance using smithing templates. The trim consists of:
- **Pattern**: The visual design/shape of the trim
- **Material**: The color/texture applied to the pattern

## Directory Structure

Armor trim textures are stored in the assets directory of your mod:

```
src/main/resources/
└── assets/
    └── createaycn/  (your mod ID)
        └── textures/
            └── trims/
                └── models/
                    └── armor/
                        ├── cog.png           (pattern overlay for leather armor)
                        ├── cog_leggings.png  (pattern overlay for leather leggings)
                        ├── cog_darker.png    (pattern for chainmail)
                        └── cog_darkest.png   (pattern for all other armor materials)
```

## Creating Pattern Textures

### 1. Base Pattern Files

You need to create 4 texture files for each armor trim pattern:

- **`<pattern_name>.png`** - Used for leather armor (16x16 pixels)
  - This is the base pattern overlay
  
- **`<pattern_name>_leggings.png`** - Used for leather leggings (16x16 pixels)
  - Leggings use a different texture layout

- **`<pattern_name>_darker.png`** - Used for chainmail armor (16x16 pixels)
  - This version should have slightly darker/muted colors

- **`<pattern_name>_darkest.png`** - Used for all other armor types (16x16 pixels)
  - Iron, gold, diamond, netherite armors
  - This should be the darkest/most muted version

### 2. Texture Format

- **Size**: 16x16 pixels (same as armor item textures)
- **Format**: PNG with transparency
- **Color**: The pattern itself should be in **white** or **grayscale**
  - Minecraft will tint it based on the trim material color
  - White areas = full material color
  - Gray areas = partially tinted
  - Black areas = no tinting
  - Transparent areas = no trim overlay

### 3. Creating the Pattern

1. **Design your pattern**: Create a 16x16 pixel design that represents your trim
   - Consider the armor shape when designing
   - Leave transparent areas where no trim should appear
   - Use white/light gray for the main pattern areas

2. **Test different brightness levels**:
   - `_darker.png`: Reduce brightness by 25-30%
   - `_darkest.png`: Reduce brightness by 40-50%
   - This ensures the pattern is visible on all armor types

## Example: Cog Pattern

For the "cog" pattern in this mod:

1. Create a simple horizontal stripe design in white
2. Save it as `cog.png` (for leather)
3. Adjust for leggings layout, save as `cog_leggings.png`
4. Darken by 25%, save as `cog_darker.png` (chainmail)
5. Darken by 45%, save as `cog_darkest.png` (other armor)

## Material Colors

The material colors are defined in the trim material JSON files:
```
src/main/resources/data/createaycn/trims/materials/copper_like.json
```

The material's texture reference determines the base color applied to your pattern:
```json
{
  "asset_name": "copper_like",
  "ingredient": {
    "item": "minecraft:copper_ingot"
  },
  "item_model_index": 0.2,
  "override_armor_materials": {
    "leather": "copper_ingot"
  },
  "description": {
    "translate": "trim_material.createaycn.copper_like"
  }
}
```

## Pattern Registration

Patterns are defined in:
```
src/main/resources/data/createaycn/trims/patterns/cog.json
```

```json
{
  "asset_id": "createaycn:cog",
  "template_item": "createaycn:cog_smithing_template",
  "description": {
    "translate": "trim_pattern.createaycn.cog"
  }
}
```

## Applying Trims to Armor

Once your textures are in place:

1. **In Game**: 
   - Place a smithing table
   - Add the armor piece
   - Add your smithing template
   - Add the material (e.g., copper ingot)
   - Take the trimmed armor from the result slot

2. **Minecraft automatically**:
   - Looks up the pattern from your template
   - Loads the appropriate texture variant for the armor type
   - Applies the material color tint
   - Renders the trim on the armor

## Tips and Best Practices

1. **Start Simple**: Begin with basic geometric patterns before attempting complex designs
2. **Test on All Armor Types**: Check how your pattern looks on leather, chainmail, iron, gold, diamond, and netherite
3. **Consider Visibility**: Ensure the pattern is visible but not overwhelming
4. **Use Reference**: Look at vanilla trim patterns in Minecraft's assets for inspiration
5. **Brightness Variants**: The darker variants ensure patterns remain visible on darker armor

## Troubleshooting

**Pattern not appearing:**
- Verify texture files are in the correct directory
- Check file names match the pattern ID in your JSON
- Ensure textures are 16x16 PNG files
- Confirm the pattern is registered in `trims/patterns/`

**Wrong colors:**
- Check the material JSON references the correct texture
- Verify the pattern textures use white/grayscale (not pre-colored)

**Template doesn't work in smithing table:**
- Ensure the template item is registered in code
- Verify the template is in the `minecraft:trim_templates` tag
- Confirm pattern and material JSON files exist

## Additional Resources

- [Minecraft Wiki - Armor Trims](https://minecraft.wiki/w/Armor_Trim)
- Vanilla armor trim textures: `.minecraft/versions/<version>/assets/minecraft/textures/trims/models/armor/`
- Pattern examples in vanilla: rib, sentry, vex, wild, coast, dune, etc.

## Example File Locations

For this mod (`createaycn`):
- Textures: `src/main/resources/assets/createaycn/textures/trims/models/armor/`
- Pattern data: `src/main/resources/data/createaycn/trims/patterns/`
- Material data: `src/main/resources/data/createaycn/trims/materials/`
- Template tag: `src/main/resources/data/minecraft/tags/items/trim_templates.json`
