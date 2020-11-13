(function(){
    /*background region*/
    var bg = "#121212";
    var borderStyle = "1px solid transparent";
    $("#raspored").css("background-color",bg);
    $("#content-contain").css("background-color",bg);
    $(".vrijeme-mobitel .tok").css("border-right",borderStyle);
    /*base64 encoded dark background image*/
    $(".raspored div.dan div.tok").css("background-image", "url('data:image/png;base64,iVBORw0KGgoAAAANS
        UhEUgAAAA4AAABRCAIAAABt8gCkAAADKnpUWHRSYXcgcHJvZmlsZSB0eXBlIGV4aWYAAHja5ZZbluQmDIbfWUWWYEkIieVwPSc7y
        PLzg6nqrupJpifJW8yxoYQshD4hVxh//D7Db7iIlUNU85RTunDFHDMXDPy6r7yfdMX9vK9xenqVB30oMESCXu6fVo5+gVw/XnisQ
        fVVHvzMsB9DH4b3JWvlNe6fnYScbznFYygfV1N2++xqPYbaUdyunDs+3bq79Tu8CAxR6oqFhHkIybWf8fZA7rvgzniS6NJDW2MJ6
        KI89oqAvGzv0V/X5wC9BJnb2dp79J+jt+BzOXJ5i2V6GEo/niB9k8tzGf68sJxRgPhlooznK1+CPGf3Oce9uxITIppORu1g08MMF
        CtCLvu1hGa4FWPbLaP5Va4G5P1qV0VrlIlBZQaK1KnQpLH7Rg0uRh5s6Jkby5a5GGduIAaKq9FkA70uDm6NRxCBmJ++0F437/UaO
        VbuBFUmGKMN+y9a+LvJX2lhzpUIRJffcUJewC9eeQ03Frn1hBaA0DzcdAf40Q7+61P+IFVBUHeYHRssV71N1Pts37klm7NAT9HfR
        4iC9WMAIcLaCmdIQOBKOAOU6DJmI0IcHYAKPGeJXEGAVLnDSY4iiYOx81ob7xhtXVSpxEuM2rSOjyQxsMH5AqwYFflj0ZFDRUWjq
        iY19aBZS5IUk6aULK0iV0wsmloyM7dsxcWjqyc3d89eMmdBDdScsmXPOZfCoWChAlsF+gWSylVqrFpTteo119KQPi02balZ85Zb6
        dylo0z01K17z70MCgOVYsShIw0bPvIoE7k2ZcapM02bPvMsT2p0ju17+wVqdKjxJrX07EkN0mD2MEGrnOhiBmIcCcRtEUBC82J2O
        cXIi9xidmXGoVCGk7rYhE6LGBDGga/KpCe7D3Lf4hbUv8WNf0YuLHT/BbkAdF+5/YBaX9+5fQ7PKVwxvQSnD+Eo7AE3yiL7v+3/b
        4Z42EyqYyKUba6qhiSYjprRsxqSXZqtOdT5OfB1BYkR8A5gIRUxUUeDcqo1r195esbkrPhmk/zMdPhq+5+ZDt9x+zumA2zjixN1/c
        /R7/XYGTL5T9B6TgjFl7mhAAABhGlDQ1BJQ0MgcHJvZmlsZQAAeJx9kT1Iw0AcxV9TpSotHcwg4pChdbIgKuKoVShChVArtOpgPv
        oFTRqSFBdHwbXg4Mdi1cHFWVcHV0EQ/ABxcnRSdJES/5cUWsR4cNyPd/ced+8ArllVNKtnHNB028ykkkIuvyqEXtGPKHhEEJcUy5
        gTxTR8x9c9Amy9S7As/3N/johasBQgIBDPKoZpE28QT2/aBuN9Yl4pSyrxOfGYSRckfmS67PEb45LLHMvkzWxmnpgnFkpdLHexUj
        Y14inimKrplM/lPFYZbzHWqnWlfU/2wnBBX1lmOs0RpLCIJYgQIKOOCqqwkaBVJ8VChvaTPv5h1y+SSyZXBQo5FlCDBsn1g/3B72
        6t4uSElxROAr0vjvMRB0K7QKvhON/HjtM6AYLPwJXe8deawMwn6Y2OFjsCotvAxXVHk/eAyx1g6MmQTMmVgjS5YhF4P6NvygODt8
        DAmtdbex+nD0CWukrfAAeHwGiJstd93t3X3du/Z9r9/QBVEXKbuRPoygAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB+QJDx
        QNLmYl+T0AAAAzSURBVEjH7dYxEQAwDMSwpFcEQRL+6Erh98qzALhnprJOxaEoiqIoiqIoenc3pG3Lv6cP4noBnzf+AA0AAAAASU
        VORK5CYII=')
    ");
    $(".vrijeme-mobitel .tok .satnica").css("border-top",borderStyle).css("border-bottom",borderStyle);

    /*change element colors*/
    var fg = "#212121";
    var textColor = "#BDBDBD";
    $(".satnica").css("background-color",fg);
    $(".naziv-dan").css("background-color",fg);
    $(".naziv-dan a").css("background-color",fg).css("color",textColor);
    $(".vrijeme-mobitel .satnica").css("color",textColor);

    /*blocks*/
    var blockTextColor = "#EEEEEE";
    $(".blokovi").css("border-top","none");
    $(".blokovi:not(.Ne) p").css("color",blockTextColor);
    $(".PR").css("background-color","#517652");
    $(".AV").css("background-color","#717FA4");
    $(".LV").css("background-color","#9d6f5b");
    $(".IS").css("background-color","#a7843d");
    $(".KV").css("background-color","#a29343");
}());
